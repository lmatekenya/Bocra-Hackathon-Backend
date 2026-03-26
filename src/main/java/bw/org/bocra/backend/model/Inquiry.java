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
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "inquiries")
public class Inquiry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "First name is required")
    @Size(min = 2, max = 120, message = "First name must be between 2 and 120 characters")
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(min = 2, max = 120, message = "Last name must be between 2 and 120 characters")
    private String lastName;

    @Email(message = "Valid email is required")
    @NotBlank(message = "Email is required")
    @Size(max = 255, message = "Email is too long")
    @Convert(converter = SensitiveStringEncryptor.class)
    @Column(columnDefinition = "TEXT")
    private String email;

    @NotBlank(message = "Inquiry type is required")
    @Size(max = 120, message = "Inquiry type is too long")
    private String inquiryType;

    @NotBlank(message = "Message cannot be empty")
    @Size(min = 15, max = 4000, message = "Message must be between 15 and 4000 characters")
    @Column(columnDefinition = "TEXT")
    private String message;

    @Transient
    private String captchaToken;

    @Column(nullable = false, length = 30)
    private String status = "NEW";

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime submittedAt;
}
