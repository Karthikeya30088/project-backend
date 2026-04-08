package com.fsad.assignment.controller;

import com.fsad.assignment.dto.PortalDtos;
import com.fsad.assignment.service.PortalService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final PortalService portalService;

    public TaskController(PortalService portalService) {
        this.portalService = portalService;
    }

    @PostMapping
    public PortalDtos.TaskResponse createTask(@Valid @RequestBody PortalDtos.CreateTaskRequest request) {
        return portalService.createTask(request);
    }
}
