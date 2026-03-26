package bw.org.bocra.backend.controller;

import bw.org.bocra.backend.dto.AdminProfileResponse;
import bw.org.bocra.backend.dto.AuthLoginRequest;
import bw.org.bocra.backend.dto.AuthLoginResponse;
import bw.org.bocra.backend.security.RequestIpResolver;
import bw.org.bocra.backend.service.AuditLogService;
import bw.org.bocra.backend.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final AuditLogService auditLogService;

    @PostMapping("/login")
    public ResponseEntity<AuthLoginResponse> login(
            @Valid @RequestBody AuthLoginRequest request,
            HttpServletRequest servletRequest
    ) {
        String sourceIp = RequestIpResolver.resolveClientIp(servletRequest);
        return ResponseEntity.ok(authService.login(request, sourceIp));
    }

    @GetMapping("/me")
    public ResponseEntity<AdminProfileResponse> me(Authentication authentication) {
        String username = authentication.getName();
        String role = authentication.getAuthorities().stream()
                .findFirst()
                .map(a -> a.getAuthority().replace("ROLE_", ""))
                .orElse("USER");

        return ResponseEntity.ok(new AdminProfileResponse(username, role));
    }

    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout(
            Authentication authentication,
            HttpServletRequest request
    ) {
        if (authentication != null) {
            auditLogService.log(
                    "ADMIN_LOGOUT",
                    authentication.getName(),
                    null,
                    "SUCCESS",
                    "Administrator logged out.",
                    RequestIpResolver.resolveClientIp(request)
            );
        }
        return ResponseEntity.ok(Map.of("message", "Logged out successfully."));
    }
}
