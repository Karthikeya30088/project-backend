package com.fsad.assignment.controller;

import com.fsad.assignment.dto.AuthDtos;
import com.fsad.assignment.service.PortalService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final PortalService portalService;

    public AuthController(PortalService portalService) {
        this.portalService = portalService;
    }

    @PostMapping("/teacher/login")
    public AuthDtos.AuthResponse teacherLogin(@Valid @RequestBody AuthDtos.LoginRequest request) {
        return portalService.loginTeacher(request);
    }

    @PostMapping("/student/login")
    public AuthDtos.AuthResponse studentLogin(@Valid @RequestBody AuthDtos.LoginRequest request) {
        return portalService.loginStudent(request);
    }
}
