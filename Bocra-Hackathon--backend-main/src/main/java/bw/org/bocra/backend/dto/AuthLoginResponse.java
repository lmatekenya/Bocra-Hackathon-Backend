package bw.org.bocra.backend.dto;

public record AuthLoginResponse(
        String username,
        String role,
        String token
) {
}
