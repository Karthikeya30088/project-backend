package com.fsad.assignment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class PortalDtos {

    public record RegisterStudentRequest(
            @NotBlank String name,
            @NotBlank String email,
            @NotBlank String password,
            @NotBlank String gradeLevel
    ) {
    }

    public record CreateTaskRequest(
            @NotBlank String title,
            @NotBlank String description,
            @NotNull LocalDate dueDate
    ) {
    }

    public record GradeSubmissionRequest(
            String grade,
            String feedback,
            Boolean verified
    ) {
    }

    public record StudentSummary(
            String id,
            String name,
            String email,
            String gradeLevel
    ) {
    }

    public record TaskResponse(
            String id,
            String teacherId,
            String title,
            String description,
            LocalDate dueDate,
            LocalDateTime createdAt
    ) {
    }

    public record SubmissionResponse(
            String id,
            String taskId,
            String studentId,
            String comment,
            String fileName,
            String fileUrl,
            LocalDateTime submittedAt,
            String status,
            boolean verified,
            String grade,
            String feedback,
            LocalDateTime reviewedAt,
            String studentName,
            String studentEmail,
            String taskTitle
    ) {
    }

    public record TeacherDashboardResponse(
            AuthDtos.UserResponse teacher,
            List<StudentSummary> students,
            List<TaskResponse> tasks,
            List<SubmissionResponse> submissions
    ) {
    }

    public record StudentDashboardResponse(
            StudentSummary student,
            List<TaskResponse> tasks,
            List<SubmissionResponse> submissions
    ) {
    }

    public record HealthResponse(boolean ok, String database) {
    }
}
