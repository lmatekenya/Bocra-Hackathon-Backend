package bw.org.bocra.backend.service;

import org.springframework.stereotype.Component;

@Component
public class InputSanitizer {

    public String normalize(String value) {
        if (value == null) {
            return null;
        }

        return value
                .replaceAll("[\\p{Cntrl}&&[^\\r\\n\\t]]", "")
                .trim();
    }

    public String normalizeMultiline(String value) {
        if (value == null) {
            return null;
        }

        String sanitized = value
                .replaceAll("[\\p{Cntrl}&&[^\\r\\n\\t]]", "")
                .trim();

        return sanitized.replaceAll("\\n{3,}", "\\n\\n");
    }
}
