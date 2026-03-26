package bw.org.bocra.backend.repository;

import bw.org.bocra.backend.model.AdminUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AdminUserRepository extends JpaRepository<AdminUser, Long> {
    Optional<AdminUser> findByUsernameIgnoreCase(String username);
    Optional<AdminUser> findByUsernameIgnoreCaseAndActiveTrue(String username);
}
