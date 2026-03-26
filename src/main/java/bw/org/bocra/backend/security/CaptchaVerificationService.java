package bw.org.bocra.backend.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@Slf4j
@Service
public class CaptchaVerificationService {

    private final RestClient restClient;

    @Value("${app.security.captcha.enabled:false}")
    private boolean enabled;

    @Value("${app.security.captcha.secret:}")
    private String secret;

    @Value("${app.security.captcha.verify-url:https://challenges.cloudflare.com/turnstile/v0/siteverify}")
    private String verifyUrl;

    public CaptchaVerificationService(RestClient.Builder restClientBuilder) {
        this.restClient = restClientBuilder.build();
    }

    public void verifyOrThrow(String token, String clientIp) {
        if (!enabled) {
            return;
        }

        if (token == null || token.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Captcha token is required.");
        }

        if (secret == null || secret.isBlank()) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Captcha secret is not configured.");
        }

        MultiValueMap<String, String> payload = new LinkedMultiValueMap<>();
        payload.add("secret", secret);
        payload.add("response", token);
        if (clientIp != null && !clientIp.isBlank()) {
            payload.add("remoteip", clientIp);
        }

        Map<String, Object> response;
        try {
            response = restClient.post()
                    .uri(verifyUrl)
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .body(payload)
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {
                    });
        } catch (Exception ex) {
            log.warn("Captcha verification request failed", ex);
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Captcha verification service is unavailable.");
        }

        boolean success = Boolean.TRUE.equals(response != null ? response.get("success") : null);
        if (!success) {
            Object errorCodes = response == null ? null : response.get("error-codes");
            log.warn("Captcha verification failed: {}", errorCodes);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Captcha verification failed.");
        }
    }
}
