package bw.org.bocra.backend.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "audit_logs")
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 80)
    private String eventType;

    @Column(nullable = false, length = 120)
    private String actor;

    @Column(length = 160)
    private String target;

    @Column(length = 20, nullable = false)
    private String outcome;

    @Column(columnDefinition = "TEXT")
    private String details;

    @Column(length = 120)
    private String sourceIp;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;
}
