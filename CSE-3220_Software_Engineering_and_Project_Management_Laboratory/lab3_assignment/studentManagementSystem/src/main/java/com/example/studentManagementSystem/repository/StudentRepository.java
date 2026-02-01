package com.example.studentManagementSystem.repository;

import com.example.studentManagementSystem.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for Student entity
 */
@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
    
    Optional<Student> findByStudentId(String studentId);
    
    Optional<Student> findByEmail(String email);
    
    Optional<Student> findByUserId(Long userId);
    
    List<Student> findByDepartmentId(Long departmentId);
    
    boolean existsByStudentId(String studentId);
    
    boolean existsByEmail(String email);
}
