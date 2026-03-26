package bw.org.bocra.backend.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "stats")
public class Stat {

    @Id
    private String id; // e.g., "complaints", "licenses"

    private Double value;
    private String suffix;
    private String label;
    private String description;
}
