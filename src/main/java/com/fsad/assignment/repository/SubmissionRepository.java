package com.fsad.assignment.repository;

import com.fsad.assignment.model.Submission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SubmissionRepository extends JpaRepository<Submission, String> {
    Optional<Submission> findByTask_IdAndStudent_Id(String taskId, String studentId);

    List<Submission> findAllByOrderBySubmittedAtDesc();

    List<Submission> findByStudent_IdOrderBySubmittedAtDesc(String studentId);
}
