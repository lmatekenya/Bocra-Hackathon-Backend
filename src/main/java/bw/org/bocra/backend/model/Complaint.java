package bw.org.bocra.backend.model;

import bw.org.bocra.backend.security.SensitiveStringEncryptor;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "complaints")
public class Complaint {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 64)
    private String ticketId;

    @NotBlank(message = "Full name is required")
    @Size(min = 3, max = 160, message = "Full name must be between 3 and 160 characters")
    private String fullName;

    @NotBlank(message = "Contact number is required")
    @Pattern(regexp = "^[+0-9 ()-]{7,30}$", message = "Contact number format is invalid")
    @Convert(converter = SensitiveStringEncryptor.class)
    @Column(columnDefinition = "TEXT")
    private String contactNumber;

    @NotBlank(message = "Service provider is required")
    @Size(max = 120, message = "Service provider is too long")
    private String serviceProvider;

    @Size(max = 120, message = "Provider reference is too long")
    private String providerReference;

    @Email(message = "Valid email is required")
    @NotBlank(message = "Email is required")
    @Size(max = 255, message = "Email is too long")
    @Convert(converter = SensitiveStringEncryptor.class)
    @Column(columnDefinition = "TEXT")
    private String email;

    @NotBlank(message = "Complaint details are required")
    @Size(min = 20, max = 5000, message = "Complaint details must be between 20 and 5000 characters")
    @Column(columnDefinition = "TEXT")
    private String complaintDetails;

    @Transient
    private String captchaToken;

    @Column(nullable = false, length = 30)
    private String status = "PENDING";

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;
}
