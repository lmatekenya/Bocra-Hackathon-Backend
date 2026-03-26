package bw.org.bocra.backend.service;

import bw.org.bocra.backend.model.Inquiry;
import bw.org.bocra.backend.repository.InquiryRepository;
import bw.org.bocra.backend.security.CaptchaVerificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InquiryService {

    private final InquiryRepository inquiryRepository;
    private final CaptchaVerificationService captchaVerificationService;
    private final InputSanitizer sanitizer;
    private final AuditLogService auditLogService;

    @Transactional
    public Inquiry saveInquiry(Inquiry inquiry, String sourceIp) {
        captchaVerificationService.verifyOrThrow(inquiry.getCaptchaToken(), sourceIp);

        inquiry.setFirstName(sanitizer.normalize(inquiry.getFirstName()));
        inquiry.setLastName(sanitizer.normalize(inquiry.getLastName()));
        inquiry.setEmail(sanitizer.normalize(inquiry.getEmail()));
        inquiry.setInquiryType(sanitizer.normalize(inquiry.getInquiryType()));
        inquiry.setMessage(sanitizer.normalizeMultiline(inquiry.getMessage()));
        inquiry.setCaptchaToken(null);
        inquiry.setStatus("NEW");

        Inquiry saved = inquiryRepository.save(inquiry);

        auditLogService.log(
                "INQUIRY_SUBMITTED",
                "PUBLIC",
                String.valueOf(saved.getId()),
                "SUCCESS",
                "Public inquiry submitted.",
                sourceIp
        );

        return saved;
    }

    @Transactional(readOnly = true)
    public List<Inquiry> getAllInquiries() {
        return inquiryRepository.findAllByOrderBySubmittedAtDesc();
    }
}
