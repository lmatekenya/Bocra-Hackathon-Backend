package bw.org.bocra.backend.controller;

import bw.org.bocra.backend.model.Inquiry;
import bw.org.bocra.backend.security.RequestIpResolver;
import bw.org.bocra.backend.service.InquiryService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/inquiries")
@RequiredArgsConstructor
public class InquiryController {

    private final InquiryService inquiryService;

    @PostMapping
    public ResponseEntity<Map<String, Object>> submitInquiry(
            @Valid @RequestBody Inquiry inquiry,
            HttpServletRequest request
    ) {
        String sourceIp = RequestIpResolver.resolveClientIp(request);
        inquiryService.saveInquiry(inquiry, sourceIp);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Inquiry sent successfully.");

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}
