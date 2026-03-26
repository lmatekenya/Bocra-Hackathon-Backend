package bw.org.bocra.backend.controller;

import bw.org.bocra.backend.model.Document;
import bw.org.bocra.backend.repository.DocumentRepository;
import bw.org.bocra.backend.security.RequestIpResolver;
import bw.org.bocra.backend.service.AuditLogService;
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
@RequestMapping("/api/v1/documents")
@RequiredArgsConstructor
public class DocumentController {

    private final DocumentRepository documentRepository;
    private final AuditLogService auditLogService;

    @GetMapping
    public ResponseEntity<List<Document>> getAllDocuments(@RequestParam(required = false) String category) {
        if (category != null && !category.isBlank()) {
            return ResponseEntity.ok(documentRepository.findByCategory(category));
        }
        return ResponseEntity.ok(documentRepository.findAll());
    }

    @PostMapping
    public ResponseEntity<Document> createDocument(
            @Valid @RequestBody Document document,
            Authentication authentication,
            HttpServletRequest request
    ) {
        Document created = documentRepository.save(document);

        String actor = authentication == null ? "UNKNOWN" : authentication.getName();
        auditLogService.log(
                "DOCUMENT_CREATED",
                actor,
                created.getName(),
                "SUCCESS",
                "Admin published a document.",
                RequestIpResolver.resolveClientIp(request)
        );

        return ResponseEntity.ok(created);
    }
}
