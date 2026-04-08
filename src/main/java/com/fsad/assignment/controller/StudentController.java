package com.fsad.assignment.controller;

import com.fsad.assignment.dto.AuthDtos;
import com.fsad.assignment.dto.PortalDtos;
import com.fsad.assignment.service.PortalService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/students")
public class StudentController {

    private final PortalService portalService;

    public StudentController(PortalService portalService) {
        this.portalService = portalService;
    }

    @PostMapping("/register")
    public AuthDtos.AuthResponse registerStudent(@Valid @RequestBody PortalDtos.RegisterStudentRequest request) {
        return portalService.registerStudent(request);
    }
}
