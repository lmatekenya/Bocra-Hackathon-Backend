package bw.org.bocra.backend.security;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.ConsumptionProbe;
import io.github.bucket4j.Refill;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Component
public class RateLimitFilter extends OncePerRequestFilter {

    private static final Set<String> PUBLIC_POST_PATHS = Set.of(
            "/api/v1/auth/login",
            "/api/v1/complaints",
            "/api/v1/inquiries",
            "/api/v1/cyber-incidents"
    );

    private final Map<String, Bucket> cache = new ConcurrentHashMap<>();

    @Value("${app.security.rate-limit.enabled:true}")
    private boolean enabled;

    @Value("${app.security.rate-limit.login.capacity:5}")
    private long loginCapacity;

    @Value("${app.security.rate-limit.login.window-seconds:60}")
    private long loginWindowSeconds;

    @Value("${app.security.rate-limit.public.capacity:20}")
    private long publicCapacity;

    @Value("${app.security.rate-limit.public.window-seconds:300}")
    private long publicWindowSeconds;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return !enabled
                || !"POST".equalsIgnoreCase(request.getMethod())
                || !PUBLIC_POST_PATHS.contains(request.getRequestURI());
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String endpoint = request.getRequestURI();
        String ip = RequestIpResolver.resolveClientIp(request);
        String key = endpoint + ":" + ip;

        Bucket bucket = cache.computeIfAbsent(key, k -> newBucket(endpoint));
        ConsumptionProbe probe = bucket.tryConsumeAndReturnRemaining(1);

        response.setHeader("X-RateLimit-Remaining", String.valueOf(probe.getRemainingTokens()));

        if (!probe.isConsumed()) {
            long retryAfter = Math.max(1, TimeUnit.NANOSECONDS.toSeconds(probe.getNanosToWaitForRefill()));
            response.setStatus(429);
            response.setHeader("Retry-After", String.valueOf(retryAfter));
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.getWriter().write("{\"message\":\"Too many requests. Please wait and retry.\"}");
            return;
        }

        filterChain.doFilter(request, response);
    }

    private Bucket newBucket(String endpoint) {
        if ("/api/v1/auth/login".equals(endpoint)) {
            return Bucket.builder()
                    .addLimit(Bandwidth.classic(loginCapacity, Refill.greedy(loginCapacity, Duration.ofSeconds(loginWindowSeconds))))
                    .build();
        }

        return Bucket.builder()
                .addLimit(Bandwidth.classic(publicCapacity, Refill.greedy(publicCapacity, Duration.ofSeconds(publicWindowSeconds))))
                .build();
    }
}
