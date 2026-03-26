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
@Table(name = "news")
public class NewsArticle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Title is required")
    @Size(max = 220, message = "Title is too long")
    private String title;

    @NotBlank(message = "Summary is required")
    @Column(columnDefinition = "TEXT")
    private String summary;

    @NotBlank(message = "Content is required")
    @Column(columnDefinition = "TEXT")
    private String content;

    @NotBlank(message = "Category is required")
    @Size(max = 120, message = "Category is too long")
    private String category;

    @Size(max = 500, message = "Image URL is too long")
    private String imageUrl;

    @Column(nullable = false, unique = true, length = 220)
    private String slug;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime publishedAt;
}
