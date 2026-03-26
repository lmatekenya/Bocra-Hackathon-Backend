package bw.org.bocra.backend.controller;

import bw.org.bocra.backend.model.NewsArticle;
import bw.org.bocra.backend.security.RequestIpResolver;
import bw.org.bocra.backend.service.AuditLogService;
import bw.org.bocra.backend.service.NewsService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/news")
@RequiredArgsConstructor
public class NewsArticleController {

    private final NewsService newsService;
    private final AuditLogService auditLogService;

    @GetMapping
    public ResponseEntity<List<NewsArticle>> getAllNews(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Integer limit) {

        if (category != null && !category.isBlank()) {
            return ResponseEntity.ok(newsService.getNewsByCategory(category, limit));
        }

        return ResponseEntity.ok(newsService.getAllNews(limit));
    }

    @PostMapping
    public ResponseEntity<NewsArticle> createNews(
            @Valid @RequestBody NewsArticle article,
            Authentication authentication,
            HttpServletRequest request
    ) {
        NewsArticle created = newsService.createNews(article);

        String actor = authentication == null ? "UNKNOWN" : authentication.getName();
        auditLogService.log(
                "NEWS_CREATED",
                actor,
                created.getSlug(),
                "SUCCESS",
                "Admin published a news article.",
                RequestIpResolver.resolveClientIp(request)
        );

        return ResponseEntity.ok(created);
    }
}
