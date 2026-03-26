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
@Table(name = "documents")
public class Document {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Name is required")
    @Size(max = 220, message = "Name is too long")
    private String name;

    @NotBlank(message = "Category is required")
    @Size(max = 120, message = "Category is too long")
    private String category;

    @Size(max = 40, message = "Size value is too long")
    private String size;

    @Size(max = 40, message = "Date value is too long")
    private String date;

    @NotBlank(message = "URL is required")
    @Size(max = 500, message = "URL is too long")
    private String url;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;
}
