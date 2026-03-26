package bw.org.bocra.backend.service;

import bw.org.bocra.backend.model.AuditLog;
import bw.org.bocra.backend.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;

    @Transactional
    public void log(String eventType, String actor, String target, String outcome, String details, String sourceIp) {
        AuditLog log = new AuditLog();
        log.setEventType(limit(eventType, 80));
        log.setActor(limit(actor, 120));
        log.setTarget(limit(target, 160));
        log.setOutcome(limit(outcome, 20));
        log.setDetails(details);
        log.setSourceIp(limit(sourceIp, 120));
        auditLogRepository.save(log);
    }

    @Transactional(readOnly = true)
    public List<AuditLog> recentLogs() {
        return auditLogRepository.findTop100ByOrderByCreatedAtDesc();
    }

    private String limit(String value, int maxLength) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        if (trimmed.length() <= maxLength) {
            return trimmed;
        }
        return trimmed.substring(0, maxLength);
    }
}
