package bw.org.bocra.backend.repository;

import bw.org.bocra.backend.model.Stat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StatRepository extends JpaRepository<Stat, String> {
}
