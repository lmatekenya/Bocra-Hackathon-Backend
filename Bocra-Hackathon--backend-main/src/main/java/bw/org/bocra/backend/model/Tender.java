package bw.org.bocra.backend.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "tenders")
public class Tender {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Tender number is required")
    @Size(max = 80, message = "Tender number is too long")
    @Column(unique = true, nullable = false, length = 80)
    private String tenderNumber;

    @NotBlank(message = "Title is required")
    @Size(max = 220, message = "Title is too long")
    private String title;

    @NotBlank(message = "Type is required")
    @Size(max = 100, message = "Type is too long")
    private String type;

    @Size(max = 30, message = "Publish date value is too long")
    private String publishDate;

    @Size(max = 30, message = "Closing date value is too long")
    private String closingDate;

    @Column(nullable = false, length = 30)
    private String status = "OPEN";

    @Column(columnDefinition = "TEXT")
    private String description;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;
}
