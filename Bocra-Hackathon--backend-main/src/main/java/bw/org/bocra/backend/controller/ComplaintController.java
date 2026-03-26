package bw.org.bocra.backend.controller;

import bw.org.bocra.backend.model.Complaint;
import bw.org.bocra.backend.security.RequestIpResolver;
import bw.org.bocra.backend.service.ComplaintService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/complaints")
@RequiredArgsConstructor
public class ComplaintController {

    private final ComplaintService complaintService;

    @PostMapping
    public ResponseEntity<Map<String, Object>> submitComplaint(
            @Valid @RequestBody Complaint complaint,
            HttpServletRequest request
    ) {
        String sourceIp = RequestIpResolver.resolveClientIp(request);
        Complaint savedComplaint = complaintService.submitComplaint(complaint, sourceIp);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("ticketId", savedComplaint.getTicketId());
        response.put("message", "Complaint submitted successfully.");

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{ticketId}")
    public ResponseEntity<Complaint> getComplaintStatus(@PathVariable String ticketId) {
        return ResponseEntity.ok(complaintService.getByTicketId(ticketId));
    }
}
