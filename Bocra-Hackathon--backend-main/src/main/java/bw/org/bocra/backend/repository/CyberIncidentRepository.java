package bw.org.bocra.backend.repository;

import bw.org.bocra.backend.model.CyberIncident;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CyberIncidentRepository extends JpaRepository<CyberIncident, Long> {
    Optional<CyberIncident> findByIncidentId(String incidentId);
    List<CyberIncident> findAllByOrderByReportedAtDesc();
}
