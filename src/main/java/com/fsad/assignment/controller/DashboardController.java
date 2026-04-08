package com.fsad.assignment.controller;

import com.fsad.assignment.dto.PortalDtos;
import com.fsad.assignment.service.PortalService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final PortalService portalService;

    public DashboardController(PortalService portalService) {
        this.portalService = portalService;
    }

    @GetMapping("/teacher")
    public PortalDtos.TeacherDashboardResponse teacherDashboard() {
        return portalService.getTeacherDashboard();
    }

    @GetMapping("/student/{studentId}")
    public PortalDtos.StudentDashboardResponse studentDashboard(@PathVariable String studentId) {
        return portalService.getStudentDashboard(studentId);
    }
}
