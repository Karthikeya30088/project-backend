package com.fsad.assignment.controller;

import com.fsad.assignment.dto.PortalDtos;
import com.fsad.assignment.service.PortalService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class HealthController {

    private final PortalService portalService;

    public HealthController(PortalService portalService) {
        this.portalService = portalService;
    }

    @GetMapping("/health")
    public PortalDtos.HealthResponse health() {
        return portalService.health();
    }
}
