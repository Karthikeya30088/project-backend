package com.fsad.assignment.repository;

import com.fsad.assignment.model.AssignmentTask;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AssignmentTaskRepository extends JpaRepository<AssignmentTask, String> {
    List<AssignmentTask> findAllByOrderByCreatedAtDesc();
}
