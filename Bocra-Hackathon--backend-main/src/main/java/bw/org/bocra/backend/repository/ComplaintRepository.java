package bw.org.bocra.backend.repository;

import bw.org.bocra.backend.model.Complaint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ComplaintRepository extends JpaRepository<Complaint, Long> {
    Optional<Complaint> findByTicketId(String ticketId);
    List<Complaint> findAllByOrderByCreatedAtDesc();
}
