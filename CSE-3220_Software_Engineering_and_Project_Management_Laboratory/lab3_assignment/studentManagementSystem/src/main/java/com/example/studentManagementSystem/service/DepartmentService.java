package com.example.studentManagementSystem.service;

import com.example.studentManagementSystem.dto.DepartmentDTO;
import com.example.studentManagementSystem.entity.Department;
import com.example.studentManagementSystem.repository.DepartmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service class for Department operations
 * Handles all business logic for departments
 */
@Service
@RequiredArgsConstructor
@Transactional
public class DepartmentService {

    private final DepartmentRepository departmentRepository;

    /**
     * Get all departments
     */
    public List<DepartmentDTO> getAllDepartments() {
        return departmentRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get department by ID
     */
    public DepartmentDTO getDepartmentById(Long id) {
        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Department not found with id: " + id));
        return convertToDTO(department);
    }

    /**
     * Get department entity by ID
     */
    public Department getDepartmentEntityById(Long id) {
        return departmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Department not found with id: " + id));
    }

    /**
     * Create new department
     */
    public DepartmentDTO createDepartment(DepartmentDTO dto) {
        if (departmentRepository.existsByCode(dto.getCode())) {
            throw new RuntimeException("Department code already exists: " + dto.getCode());
        }
        
        Department department = new Department();
        department.setName(dto.getName());
        department.setCode(dto.getCode());
        department.setDescription(dto.getDescription());
        
        Department saved = departmentRepository.save(department);
        return convertToDTO(saved);
    }

    /**
     * Update existing department
     */
    public DepartmentDTO updateDepartment(Long id, DepartmentDTO dto) {
        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Department not found with id: " + id));
        
        department.setName(dto.getName());
        department.setDescription(dto.getDescription());
        // Code cannot be changed
        
        Department saved = departmentRepository.save(department);
        return convertToDTO(saved);
    }

    /**
     * Delete department
     */
    public void deleteDepartment(Long id) {
        if (!departmentRepository.existsById(id)) {
            throw new RuntimeException("Department not found with id: " + id);
        }
        departmentRepository.deleteById(id);
    }

    /**
     * Convert entity to DTO
     */
    private DepartmentDTO convertToDTO(Department department) {
        DepartmentDTO dto = new DepartmentDTO();
        dto.setId(department.getId());
        dto.setName(department.getName());
        dto.setCode(department.getCode());
        dto.setDescription(department.getDescription());
        dto.setStudentCount(department.getStudents() != null ? department.getStudents().size() : 0);
        dto.setTeacherCount(department.getTeachers() != null ? department.getTeachers().size() : 0);
        return dto;
    }
}
