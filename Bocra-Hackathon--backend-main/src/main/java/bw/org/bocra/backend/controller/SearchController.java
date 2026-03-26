package bw.org.bocra.backend.controller;

import bw.org.bocra.backend.service.SearchService;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@Validated
@RestController
@RequestMapping("/api/v1/search")
@RequiredArgsConstructor
public class SearchController {

    private final SearchService searchService;

    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> search(
            @RequestParam @Size(min = 2, max = 100, message = "Search query must be between 2 and 100 characters") String q,
            @RequestParam(required = false) @Size(max = 40, message = "Filter value is too long") String filter) {
        return ResponseEntity.ok(searchService.search(q, filter));
    }
}
