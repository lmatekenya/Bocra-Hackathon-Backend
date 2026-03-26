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
@Table(name = "cyber_incidents")
public class CyberIncident {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 64)
    private String incidentId;

    @NotBlank(message = "Reporter type is required")
    @Size(max = 120, message = "Reporter type is too long")
    private String reporterType;

    @Size(max = 160, message = "Organization name is too long")
    private String organizationName;

    @NotBlank(message = "Incident type is required")
    @Size(max = 120, message = "Incident type is too long")
    private String incidentType;

    @NotBlank(message = "Date of incident is required")
    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "Date of incident must be in YYYY-MM-DD format")
    private String dateOfIncident;

    @Email(message = "Valid email is required")
    @NotBlank(message = "Email is required")
    @Size(max = 255, message = "Email is too long")
    @Convert(converter = SensitiveStringEncryptor.class)
    @Column(columnDefinition = "TEXT")
    private String email;

    @NotBlank(message = "Description of the incident is required")
    @Size(min = 20, max = 5000, message = "Description must be between 20 and 5000 characters")
    @Column(columnDefinition = "TEXT")
    private String description;

    @Transient
    private String captchaToken;

    @Column(nullable = false, length = 30)
    private String status = "INVESTIGATING";

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime reportedAt;
}
