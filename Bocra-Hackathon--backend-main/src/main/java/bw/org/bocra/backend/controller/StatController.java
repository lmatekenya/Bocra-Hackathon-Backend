package bw.org.bocra.backend.controller;

import bw.org.bocra.backend.model.Stat;
import bw.org.bocra.backend.repository.StatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/stats")
@RequiredArgsConstructor
public class StatController {

    private final StatRepository statRepository;

    @GetMapping
    public ResponseEntity<List<Stat>> getAllStats() {
        return ResponseEntity.ok(statRepository.findAll());
    }
}
