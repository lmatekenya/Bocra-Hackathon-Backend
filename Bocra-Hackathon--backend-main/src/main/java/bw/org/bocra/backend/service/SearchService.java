package bw.org.bocra.backend.service;

import bw.org.bocra.backend.model.Document;
import bw.org.bocra.backend.model.NewsArticle;
import bw.org.bocra.backend.model.Tender;
import bw.org.bocra.backend.repository.DocumentRepository;
import bw.org.bocra.backend.repository.NewsRepository;
import bw.org.bocra.backend.repository.TenderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class SearchService {

    private final NewsRepository newsRepository;
    private final TenderRepository tenderRepository;
    private final DocumentRepository documentRepository;

    public List<Map<String, Object>> search(String query, String filter) {
        String q = query == null ? "" : query.trim().toLowerCase(Locale.ROOT);
        if (q.isBlank()) {
            return List.of();
        }

        String normalizedFilter = filter == null ? "all" : filter.trim().toLowerCase(Locale.ROOT);
        List<Map<String, Object>> results = new ArrayList<>();

        if (matchesFilter(normalizedFilter, "news")) {
            for (NewsArticle article : newsRepository.findAll()) {
                if (contains(article.getTitle(), q) || contains(article.getSummary(), q)) {
                    Map<String, Object> map = new HashMap<>();
                    map.put("type", "news");
                    map.put("title", article.getTitle());
                    map.put("url", "/news");
                    map.put("description", article.getSummary());
                    results.add(map);
                }
            }
        }

        if (matchesFilter(normalizedFilter, "tender")) {
            for (Tender tender : tenderRepository.findAll()) {
                if (contains(tender.getTitle(), q) || contains(tender.getTenderNumber(), q)) {
                    Map<String, Object> map = new HashMap<>();
                    map.put("type", "tender");
                    map.put("title", tender.getTitle());
                    map.put("url", "/tenders");
                    map.put("description", tender.getTenderNumber() + " - " + tender.getStatus());
                    results.add(map);
                }
            }
        }

        if (matchesFilter(normalizedFilter, "document")) {
            for (Document document : documentRepository.findAll()) {
                if (contains(document.getName(), q) || contains(document.getCategory(), q)) {
                    Map<String, Object> map = new HashMap<>();
                    map.put("type", "document");
                    map.put("title", document.getName());
                    map.put("url", "/documents");
                    map.put("description", document.getCategory());
                    results.add(map);
                }
            }
        }

        return results;
    }

    private boolean matchesFilter(String filter, String type) {
        return "all".equals(filter) || type.equals(filter) || ("documents".equals(filter) && "document".equals(type));
    }

    private boolean contains(String value, String query) {
        return value != null && value.toLowerCase(Locale.ROOT).contains(query);
    }
}
