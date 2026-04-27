package com.fsad.assignment.controller;

import com.fsad.assignment.dto.PortalDtos;
import com.fsad.assignment.exception.ApiException;
import com.fsad.assignment.service.PortalService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/submissions")
public class SubmissionController {

    private final PortalService portalService;

    public SubmissionController(PortalService portalService) {
        this.portalService = portalService;
    }

    // ✅ CREATE SUBMISSION
    @PostMapping
    public PortalDtos.SubmissionResponse createSubmission(
            @RequestParam String taskId,
            @RequestParam String studentId,
            @RequestParam(required = false) String comment,
            @RequestPart("file") MultipartFile file
    ) {
        if (file.isEmpty()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Task, student, and file are required.");
        }

        try {
            return portalService.createOrUpdateSubmission(taskId, studentId, comment, file);
        } catch (IOException exception) {
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "Unable to save uploaded file.");
        }
    }

    // ✅ GRADE SUBMISSION
    @PatchMapping("/{submissionId}/grade")
    public PortalDtos.SubmissionResponse gradeSubmission(
            @PathVariable String submissionId,
            @Valid @RequestBody PortalDtos.GradeSubmissionRequest request
    ) {
        return portalService.gradeSubmission(submissionId, request);
    }

    // ✅ DELETE SUBMISSION (NEW)
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteSubmission(@PathVariable String id) {
        portalService.deleteSubmission(id);
        return ResponseEntity.ok("Submission deleted successfully");
    }
}