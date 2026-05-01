package com.fsad.assignment.controller;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {

    // 🔥 This handles the ROOT URL "/"
    @GetMapping("/")
    public Map<String, Object> home() {
        return Map.of(
            "ok", true,
            "message", "Backend is running"
        );
    }

    // Existing API
    @GetMapping("/api/health")
    public Map<String, Object> health() {
        return Map.of(
            "ok", true,
            "message", "Backend is running"
        );
    }
}
