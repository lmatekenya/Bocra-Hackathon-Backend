package bw.org.bocra.backend.controller;

import bw.org.bocra.backend.model.AuditLog;
import bw.org.bocra.backend.model.Complaint;
import bw.org.bocra.backend.model.CyberIncident;
import bw.org.bocra.backend.model.Inquiry;
import bw.org.bocra.backend.security.RequestIpResolver;
import bw.org.bocra.backend.service.AuditLogService;
import bw.org.bocra.backend.service.ComplaintService;
import bw.org.bocra.backend.service.CyberIncidentService;
import bw.org.bocra.backend.service.InquiryService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AdminController {

    private final ComplaintService complaintService;
    private final InquiryService inquiryService;
    private final CyberIncidentService cyberIncidentService;
    private final AuditLogService auditLogService;

    @GetMapping("/complaints")
    public ResponseEntity<List<Complaint>> getAllComplaints(
            Authentication authentication,
            HttpServletRequest request
    ) {
        logReadEvent(authentication, request, "Admin viewed complaints queue.");
        return ResponseEntity.ok(complaintService.getAllComplaints());
    }

    @GetMapping("/inquiries")
    public ResponseEntity<List<Inquiry>> getAllInquiries(
            Authentication authentication,
            HttpServletRequest request
    ) {
        logReadEvent(authentication, request, "Admin viewed inquiries queue.");
        return ResponseEntity.ok(inquiryService.getAllInquiries());
    }

    @GetMapping("/cyber-incidents")
    public ResponseEntity<List<CyberIncident>> getAllIncidents(
            Authentication authentication,
            HttpServletRequest request
    ) {
        logReadEvent(authentication, request, "Admin viewed cyber incidents queue.");
        return ResponseEntity.ok(cyberIncidentService.getAllIncidents());
    }

    @GetMapping("/audit-logs")
    public ResponseEntity<List<AuditLog>> getAuditLogs() {
        return ResponseEntity.ok(auditLogService.recentLogs());
    }

    private void logReadEvent(Authentication authentication, HttpServletRequest request, String details) {
        String actor = authentication == null ? "UNKNOWN" : authentication.getName();
        auditLogService.log(
                "ADMIN_VIEW",
                actor,
                null,
                "SUCCESS",
                details,
                RequestIpResolver.resolveClientIp(request)
        );
    }
}
