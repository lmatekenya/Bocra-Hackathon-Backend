package bw.org.bocra.backend.controller;

import bw.org.bocra.backend.model.CyberIncident;
import bw.org.bocra.backend.security.RequestIpResolver;
import bw.org.bocra.backend.service.CyberIncidentService;
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
@RequestMapping("/api/v1/cyber-incidents")
@RequiredArgsConstructor
public class CyberIncidentController {

    private final CyberIncidentService cyberIncidentService;

    @PostMapping
    public ResponseEntity<Map<String, Object>> logIncident(
            @Valid @RequestBody CyberIncident incident,
            HttpServletRequest request
    ) {
        String sourceIp = RequestIpResolver.resolveClientIp(request);
        CyberIncident saved = cyberIncidentService.reportIncident(incident, sourceIp);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("incidentId", saved.getIncidentId());
        response.put("message", "Incident reported successfully. A CSIRT analyst will contact you.");

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{incidentId}")
    public ResponseEntity<CyberIncident> getIncidentStatus(@PathVariable String incidentId) {
        return ResponseEntity.ok(cyberIncidentService.getByIncidentId(incidentId));
    }
}
