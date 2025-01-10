package ru.practicum.admin.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.actuate.health.HealthComponent;
import org.springframework.boot.actuate.health.HealthEndpoint;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/admin")
public class AdminGeneralController {
    private final HealthEndpoint healthEndpoint;

    @GetMapping("/health")
    public ResponseEntity<HealthComponent> getHeathStatus() {
        HealthComponent healthComponent = healthEndpoint.health();
        return ResponseEntity.ok(healthComponent);
    }
}