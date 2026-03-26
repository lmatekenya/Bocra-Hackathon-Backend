package bw.org.bocra.backend.service;

import bw.org.bocra.backend.model.NewsArticle;
import bw.org.bocra.backend.repository.NewsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NewsService {

    private final NewsRepository newsRepository;
    private final InputSanitizer sanitizer;

    @Transactional(readOnly = true)
    public List<NewsArticle> getAllNews(Integer limit) {
        List<NewsArticle> allNews = newsRepository.findAllByOrderByPublishedAtDesc();
        if (limit == null || limit <= 0 || limit >= allNews.size()) {
            return allNews;
        }
        return allNews.stream().limit(limit).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<NewsArticle> getNewsByCategory(String category, Integer limit) {
        List<NewsArticle> allNews = newsRepository.findByCategoryIgnoreCaseOrderByPublishedAtDesc(category);
        if (limit == null || limit <= 0 || limit >= allNews.size()) {
            return allNews;
        }
        return allNews.stream().limit(limit).collect(Collectors.toList());
    }

    @Transactional
    public NewsArticle createNews(NewsArticle article) {
        article.setTitle(sanitizer.normalize(article.getTitle()));
        article.setSummary(sanitizer.normalizeMultiline(article.getSummary()));
        article.setContent(sanitizer.normalizeMultiline(article.getContent()));
        article.setCategory(sanitizer.normalize(article.getCategory()));
        article.setImageUrl(sanitizer.normalize(article.getImageUrl()));

        String requestedSlug = article.getSlug() == null || article.getSlug().isBlank() ? article.getTitle() : article.getSlug();
        String base = slugify(requestedSlug);
        if (base.isBlank()) {
            base = "news-update";
        }

        article.setSlug(resolveUniqueSlug(base));
        return newsRepository.save(article);
    }

    private String resolveUniqueSlug(String base) {
        String candidate = base;
        int suffix = 2;
        while (newsRepository.existsBySlug(candidate)) {
            candidate = base + "-" + suffix++;
        }
        return candidate;
    }

    private String slugify(String input) {
        if (input == null) {
            return "";
        }

        return input
                .toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("^-+", "")
                .replaceAll("-+$", "");
    }
}
