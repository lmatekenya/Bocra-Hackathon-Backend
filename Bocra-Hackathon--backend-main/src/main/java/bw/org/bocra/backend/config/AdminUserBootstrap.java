package bw.org.bocra.backend.config;

import bw.org.bocra.backend.model.AdminUser;
import bw.org.bocra.backend.repository.AdminUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Slf4j
@Component
@RequiredArgsConstructor
public class AdminUserBootstrap implements ApplicationRunner {

    private final AdminUserRepository adminUserRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.admin.bootstrap.username:admin}")
    private String bootstrapUsername;

    @Value("${app.admin.bootstrap.password:Admin@12345}")
    private String bootstrapPassword;

    @Override
    public void run(ApplicationArguments args) {
        if (adminUserRepository.count() > 0) {
            return;
        }

        AdminUser adminUser = new AdminUser();
        adminUser.setUsername(bootstrapUsername.trim().toLowerCase(Locale.ROOT));
        adminUser.setPasswordHash(passwordEncoder.encode(bootstrapPassword));
        adminUser.setRole("ADMIN");
        adminUser.setActive(true);

        adminUserRepository.save(adminUser);

        log.warn("Bootstrapped admin user '{}' from environment configuration. Change credentials immediately.", bootstrapUsername);
    }
}
