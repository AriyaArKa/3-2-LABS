package com.example.studentManagementSystem.repository;

import com.example.studentManagementSystem.entity.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for Teacher entity
 */
@Repository
public interface TeacherRepository extends JpaRepository<Teacher, Long> {
    
    Optional<Teacher> findByEmail(String email);
    
    Optional<Teacher> findByUserId(Long userId);
    
    List<Teacher> findByDepartmentId(Long departmentId);
    
    boolean existsByEmail(String email);
}
