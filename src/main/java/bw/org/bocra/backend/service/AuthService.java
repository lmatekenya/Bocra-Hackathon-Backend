package bw.org.bocra.backend.service;

import bw.org.bocra.backend.dto.AuthLoginRequest;
import bw.org.bocra.backend.dto.AuthLoginResponse;
import bw.org.bocra.backend.model.AdminUser;
import bw.org.bocra.backend.repository.AdminUserRepository;
import bw.org.bocra.backend.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AdminUserRepository adminUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuditLogService auditLogService;
    private final InputSanitizer sanitizer;

    @Value("${app.security.auth.max-failed-attempts:5}")
    private int maxFailedAttempts;

    @Value("${app.security.auth.lock-minutes:15}")
    private long lockMinutes;

    @Transactional
    public AuthLoginResponse login(AuthLoginRequest request, String sourceIp) {
        String normalizedUsername = sanitizer.normalize(request.username());
        if (normalizedUsername == null || normalizedUsername.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username is required.");
        }
        final String username = normalizedUsername.toLowerCase(Locale.ROOT);

        AdminUser admin = adminUserRepository
                .findByUsernameIgnoreCase(username)
                .orElseThrow(() -> {
                    auditLogService.log(
                            "ADMIN_LOGIN",
                            username,
                            null,
                            "FAILURE",
                            "Login failed: unknown username.",
                            sourceIp
                    );
                    return new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid username or password.");
                });

        if (!admin.isActive()) {
            auditLogService.log(
                    "ADMIN_LOGIN",
                    admin.getUsername(),
                    null,
                    "FAILURE",
                    "Login denied: account inactive.",
                    sourceIp
            );
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid username or password.");
        }

        LocalDateTime now = LocalDateTime.now();
        if (admin.getLockedUntil() != null && admin.getLockedUntil().isAfter(now)) {
            auditLogService.log(
                    "ADMIN_LOGIN",
                    admin.getUsername(),
                    null,
                    "FAILURE",
                    "Login denied: account temporarily locked.",
                    sourceIp
            );
            throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS, "Account temporarily locked due to repeated failed attempts.");
        }

        if (!passwordEncoder.matches(request.password(), admin.getPasswordHash())) {
            registerFailedAttempt(admin, sourceIp);
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid username or password.");
        }

        admin.setFailedAttempts(0);
        admin.setLockedUntil(null);
        admin.setLastLoginAt(now);
        adminUserRepository.save(admin);

        String token = jwtUtil.generateToken(admin.getUsername(), admin.getRole());
        auditLogService.log(
                "ADMIN_LOGIN",
                admin.getUsername(),
                null,
                "SUCCESS",
                "Administrator authenticated successfully.",
                sourceIp
        );
        return new AuthLoginResponse(admin.getUsername(), admin.getRole(), token);
    }

    private void registerFailedAttempt(AdminUser admin, String sourceIp) {
        int attempts = admin.getFailedAttempts() + 1;
        admin.setFailedAttempts(attempts);

        String details = "Login failed: invalid password.";
        if (attempts >= maxFailedAttempts) {
            admin.setLockedUntil(LocalDateTime.now().plusMinutes(lockMinutes));
            admin.setFailedAttempts(0);
            details = "Account locked after repeated failed login attempts.";
        }

        adminUserRepository.save(admin);

        auditLogService.log(
                "ADMIN_LOGIN",
                admin.getUsername(),
                null,
                "FAILURE",
                details,
                sourceIp
        );
    }
}
