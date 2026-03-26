package bw.org.bocra.backend.controller;

import bw.org.bocra.backend.model.Tender;
import bw.org.bocra.backend.repository.TenderRepository;
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
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/tenders")
@RequiredArgsConstructor
public class TenderController {

    private final TenderRepository tenderRepository;
    private final AuditLogService auditLogService;

    @GetMapping
    public ResponseEntity<List<Tender>> getAllTenders() {
        return ResponseEntity.ok(tenderRepository.findAllByOrderByCreatedAtDesc());
    }

    @PostMapping
    public ResponseEntity<Tender> createTender(
            @Valid @RequestBody Tender tender,
            Authentication authentication,
            HttpServletRequest request
    ) {
        Tender created = tenderRepository.save(tender);

        String actor = authentication == null ? "UNKNOWN" : authentication.getName();
        auditLogService.log(
                "TENDER_CREATED",
                actor,
                created.getTenderNumber(),
                "SUCCESS",
                "Admin created a tender notice.",
                RequestIpResolver.resolveClientIp(request)
        );

        return ResponseEntity.ok(created);
    }
}
