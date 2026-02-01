package com.example.studentManagementSystem.service;

import com.example.studentManagementSystem.dto.TeacherDTO;
import com.example.studentManagementSystem.entity.Department;
import com.example.studentManagementSystem.entity.Role;
import com.example.studentManagementSystem.entity.Teacher;
import com.example.studentManagementSystem.entity.User;
import com.example.studentManagementSystem.repository.DepartmentRepository;
import com.example.studentManagementSystem.repository.RoleRepository;
import com.example.studentManagementSystem.repository.TeacherRepository;
import com.example.studentManagementSystem.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service class for Teacher operations
 * Handles all business logic for teachers
 */
@Service
@RequiredArgsConstructor
@Transactional
public class TeacherService {

    private final TeacherRepository teacherRepository;
    private final DepartmentRepository departmentRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Get all teachers
     */
    public List<TeacherDTO> getAllTeachers() {
        return teacherRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get teacher by ID
     */
    public TeacherDTO getTeacherById(Long id) {
        Teacher teacher = teacherRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Teacher not found with id: " + id));
        return convertToDTO(teacher);
    }

    /**
     * Get teacher by user ID
     */
    public TeacherDTO getTeacherByUserId(Long userId) {
        Teacher teacher = teacherRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Teacher not found for user id: " + userId));
        return convertToDTO(teacher);
    }

    /**
     * Get teacher entity by user ID
     */
    public Teacher getTeacherEntityByUserId(Long userId) {
        return teacherRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Teacher not found for user id: " + userId));
    }

    /**
     * Create new teacher with user account
     */
    public TeacherDTO createTeacher(TeacherDTO dto) {
        // Validate email uniqueness
        if (teacherRepository.existsByEmail(dto.getEmail())) {
            throw new RuntimeException("Email already exists: " + dto.getEmail());
        }
        
        // Validate username uniqueness
        if (userRepository.existsByUsername(dto.getUsername())) {
            throw new RuntimeException("Username already exists: " + dto.getUsername());
        }
        
        // Get or create teacher role
        Role teacherRole = roleRepository.findByName("ROLE_TEACHER")
                .orElseThrow(() -> new RuntimeException("Teacher role not found"));
        
        // Create user account
        User user = new User();
        user.setUsername(dto.getUsername());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setEmail(dto.getEmail());
        user.setRole(teacherRole);
        user.setEnabled(true);
        User savedUser = userRepository.save(user);
        
        // Create teacher
        Teacher teacher = new Teacher();
        teacher.setFirstName(dto.getFirstName());
        teacher.setLastName(dto.getLastName());
        teacher.setEmail(dto.getEmail());
        teacher.setPhone(dto.getPhone());
        teacher.setQualification(dto.getQualification());
        teacher.setDesignation(dto.getDesignation());
        teacher.setUser(savedUser);
        
        if (dto.getDepartmentId() != null) {
            Department department = departmentRepository.findById(dto.getDepartmentId())
                    .orElseThrow(() -> new RuntimeException("Department not found"));
            teacher.setDepartment(department);
        }
        
        Teacher saved = teacherRepository.save(teacher);
        return convertToDTO(saved);
    }

    /**
     * Update existing teacher
     */
    public TeacherDTO updateTeacher(Long id, TeacherDTO dto) {
        Teacher teacher = teacherRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Teacher not found with id: " + id));
        
        teacher.setFirstName(dto.getFirstName());
        teacher.setLastName(dto.getLastName());
        teacher.setPhone(dto.getPhone());
        teacher.setQualification(dto.getQualification());
        teacher.setDesignation(dto.getDesignation());
        
        if (dto.getDepartmentId() != null) {
            Department department = departmentRepository.findById(dto.getDepartmentId())
                    .orElseThrow(() -> new RuntimeException("Department not found"));
            teacher.setDepartment(department);
        }
        
        Teacher saved = teacherRepository.save(teacher);
        return convertToDTO(saved);
    }

    /**
     * Delete teacher
     */
    public void deleteTeacher(Long id) {
        Teacher teacher = teacherRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Teacher not found with id: " + id));
        
        // Delete associated user account
        if (teacher.getUser() != null) {
            userRepository.delete(teacher.getUser());
        }
        
        teacherRepository.deleteById(id);
    }

    /**
     * Convert entity to DTO
     */
    private TeacherDTO convertToDTO(Teacher teacher) {
        TeacherDTO dto = new TeacherDTO();
        dto.setId(teacher.getId());
        dto.setFirstName(teacher.getFirstName());
        dto.setLastName(teacher.getLastName());
        dto.setEmail(teacher.getEmail());
        dto.setPhone(teacher.getPhone());
        dto.setQualification(teacher.getQualification());
        dto.setDesignation(teacher.getDesignation());
        
        if (teacher.getDepartment() != null) {
            dto.setDepartmentId(teacher.getDepartment().getId());
            dto.setDepartmentName(teacher.getDepartment().getName());
        }
        
        return dto;
    }
}
