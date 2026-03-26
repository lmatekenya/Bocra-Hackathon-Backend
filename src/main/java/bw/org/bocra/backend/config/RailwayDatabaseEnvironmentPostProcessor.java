package bw.org.bocra.backend.config;

import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.util.StringUtils;

/**
 * Makes Railway-style database URLs compatible with Spring datasource settings.
 *
 * Railway commonly exposes DATABASE_URL/POSTGRES_URL as postgres://... while
 * the JDBC driver requires jdbc:postgresql://....
 */
public class RailwayDatabaseEnvironmentPostProcessor implements EnvironmentPostProcessor, Ordered {

    private static final String PROPERTY_SOURCE_NAME = "railwayDatabaseEnvironment";

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        String rawUrl = firstNonBlank(
                environment.getProperty("JDBC_DATABASE_URL"),
                environment.getProperty("SPRING_DATASOURCE_URL"),
                environment.getProperty("DATABASE_URL"),
                environment.getProperty("POSTGRES_URL"));

        if (!StringUtils.hasText(rawUrl)) {
            return;
        }

        String jdbcUrl = toJdbcPostgresUrl(rawUrl);
        if (!StringUtils.hasText(jdbcUrl)) {
            return;
        }

        Map<String, Object> overrides = new LinkedHashMap<>();
        overrides.put("spring.datasource.url", jdbcUrl);

        if (!hasAnyValue(environment, "JDBC_DATABASE_USERNAME", "SPRING_DATASOURCE_USERNAME", "PGUSER", "DB_USER")) {
            Credentials credentials = extractCredentials(rawUrl);
            if (credentials != null && StringUtils.hasText(credentials.username)) {
                overrides.put("spring.datasource.username", credentials.username);
                if (StringUtils.hasText(credentials.password)) {
                    overrides.put("spring.datasource.password", credentials.password);
                }
            }
        }

        if (!hasAnyValue(environment, "JDBC_DATABASE_PASSWORD", "SPRING_DATASOURCE_PASSWORD", "PGPASSWORD", "DB_PASSWORD")) {
            Credentials credentials = extractCredentials(rawUrl);
            if (credentials != null && StringUtils.hasText(credentials.password)) {
                overrides.put("spring.datasource.password", credentials.password);
            }
        }

        environment.getPropertySources().addFirst(new MapPropertySource(PROPERTY_SOURCE_NAME, overrides));
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE + 20;
    }

    private static boolean hasAnyValue(ConfigurableEnvironment environment, String... keys) {
        for (String key : keys) {
            if (StringUtils.hasText(environment.getProperty(key))) {
                return true;
            }
        }
        return false;
    }

    private static String firstNonBlank(String... values) {
        for (String value : values) {
            if (StringUtils.hasText(value)) {
                return value.trim();
            }
        }
        return null;
    }

    private static String toJdbcPostgresUrl(String value) {
        String raw = value.trim();
        String candidate = raw.startsWith("jdbc:") ? raw.substring("jdbc:".length()) : raw;
        if (candidate.startsWith("postgres://")) {
            candidate = "postgresql://" + candidate.substring("postgres://".length());
        }
        if (!candidate.startsWith("postgresql://")) {
            return null;
        }

        try {
            URI uri = URI.create(candidate);
            String host = uri.getHost();
            if (!StringUtils.hasText(host)) {
                return null;
            }

            StringBuilder jdbc = new StringBuilder("jdbc:postgresql://").append(host);
            if (uri.getPort() > 0) {
                jdbc.append(':').append(uri.getPort());
            }

            String path = uri.getRawPath();
            if (StringUtils.hasText(path)) {
                jdbc.append(path);
            } else {
                jdbc.append('/');
            }

            String query = uri.getRawQuery();
            if (StringUtils.hasText(query)) {
                jdbc.append('?').append(query);
            }

            return jdbc.toString();
        } catch (IllegalArgumentException ex) {
            return null;
        }
    }

    private static Credentials extractCredentials(String rawUrl) {
        try {
            String candidate = rawUrl.startsWith("jdbc:") ? rawUrl.substring("jdbc:".length()) : rawUrl;
            URI uri = URI.create(candidate);
            String userInfo = uri.getUserInfo();
            if (!StringUtils.hasText(userInfo)) {
                return null;
            }
            String[] parts = userInfo.split(":", 2);
            String username = decode(parts[0]);
            String password = parts.length > 1 ? decode(parts[1]) : "";
            return new Credentials(username, password);
        } catch (IllegalArgumentException ex) {
            return null;
        }
    }

    private static String decode(String value) {
        return URLDecoder.decode(value, StandardCharsets.UTF_8);
    }

    private static final class Credentials {
        private final String username;
        private final String password;

        private Credentials(String username, String password) {
            this.username = username;
            this.password = password;
        }
    }
}
