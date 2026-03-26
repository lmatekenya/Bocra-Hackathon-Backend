package bw.org.bocra.backend.repository;

import bw.org.bocra.backend.model.NewsArticle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NewsRepository extends JpaRepository<NewsArticle, Long> {
    List<NewsArticle> findAllByOrderByPublishedAtDesc();
    List<NewsArticle> findByCategoryIgnoreCaseOrderByPublishedAtDesc(String category);
    boolean existsBySlug(String slug);
}
