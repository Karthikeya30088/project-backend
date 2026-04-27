package com.fsad.assignment.service;

import com.fsad.assignment.dto.AuthDtos;
import com.fsad.assignment.dto.PortalDtos;
import com.fsad.assignment.exception.ApiException;
import com.fsad.assignment.model.AssignmentTask;
import com.fsad.assignment.model.Student;
import com.fsad.assignment.model.Submission;
import com.fsad.assignment.model.Teacher;
import com.fsad.assignment.repository.AssignmentTaskRepository;
import com.fsad.assignment.repository.StudentRepository;
import com.fsad.assignment.repository.SubmissionRepository;
import com.fsad.assignment.repository.TeacherRepository;
import com.fsad.assignment.util.IdGenerator;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Service
public class PortalService {

    private final TeacherRepository teacherRepository;
    private final StudentRepository studentRepository;
    private final AssignmentTaskRepository taskRepository;
    private final SubmissionRepository submissionRepository;
    private final Path uploadPath;

    public PortalService(
            TeacherRepository teacherRepository,
            StudentRepository studentRepository,
            AssignmentTaskRepository taskRepository,
            SubmissionRepository submissionRepository,
            @Value("${app.upload-dir}") String uploadDir
    ) throws IOException {
        this.teacherRepository = teacherRepository;
        this.studentRepository = studentRepository;
        this.taskRepository = taskRepository;
        this.submissionRepository = submissionRepository;
        this.uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
        Files.createDirectories(this.uploadPath);
    }

    // ================= AUTH =================

    public AuthDtos.AuthResponse loginTeacher(AuthDtos.LoginRequest request) {
        Teacher teacher = teacherRepository.findByEmailAndPassword(request.email(), request.password())
                .orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "Invalid teacher credentials."));
        return new AuthDtos.AuthResponse(toTeacherUser(teacher));
    }

    public AuthDtos.AuthResponse loginStudent(AuthDtos.LoginRequest request) {
        Student student = studentRepository.findByEmailAndPassword(request.email(), request.password())
                .orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "Invalid student credentials."));
        return new AuthDtos.AuthResponse(toStudentUser(student));
    }

    @Transactional
    public AuthDtos.AuthResponse registerStudent(PortalDtos.RegisterStudentRequest request) {
        if (studentRepository.existsByEmail(request.email())) {
            throw new ApiException(HttpStatus.CONFLICT, "Student email already exists.");
        }

        Student student = new Student();
        student.setId(IdGenerator.next("student"));
        student.setName(request.name());
        student.setEmail(request.email());
        student.setPassword(request.password());
        student.setGradeLevel(request.gradeLevel());

        return new AuthDtos.AuthResponse(toStudentUser(studentRepository.save(student)));
    }

    // ================= DASHBOARDS =================

    public PortalDtos.TeacherDashboardResponse getTeacherDashboard() {
        Teacher teacher = teacherRepository.findAll().stream()
                .sorted(Comparator.comparing(Teacher::getName))
                .findFirst()
                .orElse(null);

        List<PortalDtos.StudentSummary> students = studentRepository.findAll().stream()
                .sorted(Comparator.comparing(Student::getName))
                .map(this::toStudentSummary)
                .toList();

        List<PortalDtos.TaskResponse> tasks = taskRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(this::toTaskResponse)
                .toList();

        List<PortalDtos.SubmissionResponse> submissions = submissionRepository.findAllByOrderBySubmittedAtDesc().stream()
                .map(this::toSubmissionResponse)
                .toList();

        return new PortalDtos.TeacherDashboardResponse(
                teacher != null ? toTeacherUser(teacher) : null,
                students,
                tasks,
                submissions
        );
    }

    public PortalDtos.StudentDashboardResponse getStudentDashboard(String studentId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Student not found."));

        List<PortalDtos.TaskResponse> tasks = taskRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(this::toTaskResponse)
                .toList();

        List<PortalDtos.SubmissionResponse> submissions =
                submissionRepository.findByStudent_IdOrderBySubmittedAtDesc(studentId)
                        .stream()
                        .map(this::toSubmissionResponse)
                        .toList();

        return new PortalDtos.StudentDashboardResponse(
                toStudentSummary(student),
                tasks,
                submissions
        );
    }

    // ================= TASK =================

    @Transactional
    public PortalDtos.TaskResponse createTask(PortalDtos.CreateTaskRequest request) {
        Teacher teacher = teacherRepository.findById("teacher-1")
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Teacher not found."));

        AssignmentTask task = new AssignmentTask();
        task.setId(IdGenerator.next("task"));
        task.setTeacher(teacher);
        task.setTitle(request.title());
        task.setDescription(request.description());
        task.setDueDate(request.dueDate());
        task.setCreatedAt(LocalDateTime.now());

        return toTaskResponse(taskRepository.save(task));
    }

    // ================= SUBMISSION =================

    @Transactional
    public PortalDtos.SubmissionResponse createOrUpdateSubmission(
            String taskId,
            String studentId,
            String comment,
            MultipartFile file
    ) throws IOException {

        AssignmentTask task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Task or student not found."));

        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Task or student not found."));

        String safeName = file.getOriginalFilename() == null
                ? "submission-file"
                : file.getOriginalFilename().replaceAll("\\s+", "-");

        String storedFileName = System.currentTimeMillis() + "-" + safeName;

        Path targetPath = uploadPath.resolve(storedFileName);
        Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

        boolean existingSubmission = false;

        Submission submission = submissionRepository
                .findByTask_IdAndStudent_Id(taskId, studentId)
                .orElseGet(Submission::new);

        if (submission.getId() == null) {
            submission.setId(IdGenerator.next("submission"));
            submission.setTask(task);
            submission.setStudent(student);
            submission.setGrade("");
            submission.setFeedback("");
        } else {
            existingSubmission = true;
        }

        submission.setComment(comment == null ? "" : comment);
        submission.setFileName(file.getOriginalFilename());
        submission.setFileUrl("/uploads/" + storedFileName);
        submission.setSubmittedAt(LocalDateTime.now());
        submission.setStatus(existingSubmission ? "Resubmitted" : "Submitted");
        submission.setVerified(false);

        return toSubmissionResponse(submissionRepository.save(submission));
    }

    @Transactional
    public PortalDtos.SubmissionResponse gradeSubmission(String submissionId,
                                                         PortalDtos.GradeSubmissionRequest request) {

        Submission submission = submissionRepository.findById(submissionId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Submission not found."));

        boolean verified = request.verified() != null && request.verified();

        submission.setGrade(request.grade() == null ? "" : request.grade());
        submission.setFeedback(request.feedback() == null ? "" : request.feedback());
        submission.setVerified(verified);
        submission.setStatus(verified ? "Reviewed" : "Pending review");
        submission.setReviewedAt(LocalDateTime.now());

        return toSubmissionResponse(submissionRepository.save(submission));
    }

    // ================= DELETE SUBMISSION (NEW) =================

    @Transactional
    public void deleteSubmission(String id) {
        Submission submission = submissionRepository.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Submission not found."));

        submissionRepository.delete(submission);
    }

    // ================= UTIL =================

    public PortalDtos.HealthResponse health() {
        return new PortalDtos.HealthResponse(true, "assignment_submission_system");
    }

    public void seedIfNeeded() {
        if (teacherRepository.count() == 0) {
            Teacher teacher = new Teacher();
            teacher.setId("teacher-1");
            teacher.setName("Anita Rao");
            teacher.setEmail("teacher@school.com");
            teacher.setPassword("teacher123");
            teacherRepository.save(teacher);
        }
    }

    private AuthDtos.UserResponse toTeacherUser(Teacher teacher) {
        return new AuthDtos.UserResponse(
                teacher.getId(), "teacher",
                teacher.getName(),
                teacher.getEmail(),
                null
        );
    }

    private AuthDtos.UserResponse toStudentUser(Student student) {
        return new AuthDtos.UserResponse(
                student.getId(), "student",
                student.getName(),
                student.getEmail(),
                student.getGradeLevel()
        );
    }

    private PortalDtos.StudentSummary toStudentSummary(Student student) {
        return new PortalDtos.StudentSummary(
                student.getId(),
                student.getName(),
                student.getEmail(),
                student.getGradeLevel()
        );
    }

    private PortalDtos.TaskResponse toTaskResponse(AssignmentTask task) {
        return new PortalDtos.TaskResponse(
                task.getId(),
                task.getTeacher().getId(),
                task.getTitle(),
                task.getDescription(),
                task.getDueDate(),
                task.getCreatedAt()
        );
    }

    private PortalDtos.SubmissionResponse toSubmissionResponse(Submission submission) {
        return new PortalDtos.SubmissionResponse(
                submission.getId(),
                submission.getTask().getId(),
                submission.getStudent().getId(),
                submission.getComment(),
                submission.getFileName(),
                submission.getFileUrl(),
                submission.getSubmittedAt(),
                submission.getStatus(),
                submission.isVerified(),
                submission.getGrade(),
                submission.getFeedback(),
                submission.getReviewedAt(),
                submission.getStudent().getName(),
                submission.getStudent().getEmail(),
                submission.getTask().getTitle()
        );
    }
}