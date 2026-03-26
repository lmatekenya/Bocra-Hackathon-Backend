package bw.org.bocra.backend.service;

import bw.org.bocra.backend.model.Complaint;
import bw.org.bocra.backend.repository.ComplaintRepository;
import bw.org.bocra.backend.security.CaptchaVerificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ComplaintService {

    private final ComplaintRepository complaintRepository;
    private final CaptchaVerificationService captchaVerificationService;
    private final InputSanitizer sanitizer;
    private final AuditLogService auditLogService;

    @Transactional
    public Complaint submitComplaint(Complaint complaint, String sourceIp) {
        captchaVerificationService.verifyOrThrow(complaint.getCaptchaToken(), sourceIp);

        complaint.setFullName(sanitizer.normalize(complaint.getFullName()));
        complaint.setContactNumber(sanitizer.normalize(complaint.getContactNumber()));
        complaint.setServiceProvider(sanitizer.normalize(complaint.getServiceProvider()));
        complaint.setProviderReference(sanitizer.normalize(complaint.getProviderReference()));
        complaint.setEmail(sanitizer.normalize(complaint.getEmail()));
        complaint.setComplaintDetails(sanitizer.normalizeMultiline(complaint.getComplaintDetails()));
        complaint.setCaptchaToken(null);

        complaint.setTicketId(generateTicketId());
        complaint.setStatus("PENDING");
        Complaint saved = complaintRepository.save(complaint);

        auditLogService.log(
                "COMPLAINT_SUBMITTED",
                "PUBLIC",
                saved.getTicketId(),
                "SUCCESS",
                "Consumer complaint submitted.",
                sourceIp
        );

        return saved;
    }

    @Transactional(readOnly = true)
    public List<Complaint> getAllComplaints() {
        return complaintRepository.findAllByOrderByCreatedAtDesc();
    }

    @Transactional(readOnly = true)
    public Complaint getByTicketId(String ticketId) {
        return complaintRepository.findByTicketId(ticketId.toUpperCase(Locale.ROOT))
                .orElseThrow(() -> new IllegalArgumentException("Complaint not found with ID: " + ticketId));
    }

    private String generateTicketId() {
        String ticketId;
        do {
            String uniqueHash = UUID.randomUUID().toString().replace("-", "").substring(0, 10).toUpperCase(Locale.ROOT);
            ticketId = "BOCRA-" + uniqueHash;
        } while (complaintRepository.findByTicketId(ticketId).isPresent());

        return ticketId;
    }
}
