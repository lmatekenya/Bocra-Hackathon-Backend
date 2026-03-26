package bw.org.bocra.backend.repository;

import bw.org.bocra.backend.model.Tender;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TenderRepository extends JpaRepository<Tender, Long> {
    List<Tender> findByStatus(String status);
    List<Tender> findAllByOrderByCreatedAtDesc();
}
