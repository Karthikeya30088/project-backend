package com.fsad.assignment.repository;

import com.fsad.assignment.model.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TeacherRepository extends JpaRepository<Teacher, String> {
    Optional<Teacher> findByEmailAndPassword(String email, String password);
}
