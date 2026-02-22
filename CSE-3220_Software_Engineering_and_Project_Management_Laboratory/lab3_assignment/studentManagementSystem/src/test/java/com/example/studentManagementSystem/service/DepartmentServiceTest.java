package com.example.studentManagementSystem.service;

import com.example.studentManagementSystem.dto.DepartmentDTO;
import com.example.studentManagementSystem.entity.Department;
import com.example.studentManagementSystem.repository.DepartmentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for DepartmentService using Mockito
 */
@ExtendWith(MockitoExtension.class)
class DepartmentServiceTest {

    @Mock
    private DepartmentRepository departmentRepository;

    @InjectMocks
    private DepartmentService departmentService;

    private Department cse;

    @BeforeEach
    void setUp() {
        cse = new Department("Computer Science", "CSE", "CS Department");
        cse.setId(1L);
        cse.setStudents(new ArrayList<>());
        cse.setTeachers(new ArrayList<>());
    }

    @Test
    void testGetAllDepartments() {
        Department eee = new Department("Electrical Engineering", "EEE", "EE Department");
        eee.setId(2L);
        eee.setStudents(new ArrayList<>());
        eee.setTeachers(new ArrayList<>());

        when(departmentRepository.findAll()).thenReturn(Arrays.asList(cse, eee));

        assertEquals(2, departmentService.getAllDepartments().size());
        verify(departmentRepository).findAll();
    }

    @Test
    void testCreateDepartmentDuplicateCodeThrows() {
        DepartmentDTO dto = new DepartmentDTO();
        dto.setCode("CSE");

        when(departmentRepository.existsByCode("CSE")).thenReturn(true);

        assertThrows(RuntimeException.class, () -> departmentService.createDepartment(dto));
        verify(departmentRepository, never()).save(any());
    }

    @Test
    void testDeleteDepartmentNotFoundThrows() {
        when(departmentRepository.existsById(99L)).thenReturn(false);

        assertThrows(RuntimeException.class, () -> departmentService.deleteDepartment(99L));
    }
}
