package bw.org.bocra.backend.service;

import bw.org.bocra.backend.model.CyberIncident;
import bw.org.bocra.backend.repository.CyberIncidentRepository;
import bw.org.bocra.backend.security.CaptchaVerificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CyberIncidentService {

    private final CyberIncidentRepository cyberIncidentRepository;
    private final CaptchaVerificationService captchaVerificationService;
    private final InputSanitizer sanitizer;
    private final AuditLogService auditLogService;

    @Transactional
    public CyberIncident reportIncident(CyberIncident incident, String sourceIp) {
        captchaVerificationService.verifyOrThrow(incident.getCaptchaToken(), sourceIp);

        incident.setReporterType(sanitizer.normalize(incident.getReporterType()));
        incident.setOrganizationName(sanitizer.normalize(incident.getOrganizationName()));
        incident.setIncidentType(sanitizer.normalize(incident.getIncidentType()));
        incident.setDateOfIncident(sanitizer.normalize(incident.getDateOfIncident()));
        incident.setEmail(sanitizer.normalize(incident.getEmail()));
        incident.setDescription(sanitizer.normalizeMultiline(incident.getDescription()));
        incident.setCaptchaToken(null);

        incident.setIncidentId(generateIncidentId());
        incident.setStatus("INVESTIGATING");
        CyberIncident saved = cyberIncidentRepository.save(incident);

        auditLogService.log(
                "CYBER_INCIDENT_REPORTED",
                "PUBLIC",
                saved.getIncidentId(),
                "SUCCESS",
                "Cyber incident reported by public user.",
                sourceIp
        );

        return saved;
    }

    @Transactional(readOnly = true)
    public List<CyberIncident> getAllIncidents() {
        return cyberIncidentRepository.findAllByOrderByReportedAtDesc();
    }

    @Transactional(readOnly = true)
    public CyberIncident getByIncidentId(String incidentId) {
        return cyberIncidentRepository.findByIncidentId(incidentId.toUpperCase(Locale.ROOT))
                .orElseThrow(() -> new IllegalArgumentException("Incident not found with ID: " + incidentId));
    }

    private String generateIncidentId() {
        String candidate;
        do {
            candidate = "INC-" + UUID.randomUUID().toString().replace("-", "").substring(0, 10).toUpperCase(Locale.ROOT);
        } while (cyberIncidentRepository.findByIncidentId(candidate).isPresent());
        return candidate;
    }
}
