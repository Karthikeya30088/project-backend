package com.fsad.assignment.repository;

import com.fsad.assignment.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StudentRepository extends JpaRepository<Student, String> {
    Optional<Student> findByEmailAndPassword(String email, String password);

    boolean existsByEmail(String email);
}
