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
import java.util.List;
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
    private Department eee;

    @BeforeEach
    void setUp() {
        cse = new Department("Computer Science", "CSE", "CS Department");
        cse.setId(1L);
        cse.setStudents(new ArrayList<>());
        cse.setTeachers(new ArrayList<>());

        eee = new Department("Electrical Engineering", "EEE", "EE Department");
        eee.setId(2L);
        eee.setStudents(new ArrayList<>());
        eee.setTeachers(new ArrayList<>());
    }

    @Test
    void testGetAllDepartments() {
        when(departmentRepository.findAll()).thenReturn(Arrays.asList(cse, eee));

        List<DepartmentDTO> result = departmentService.getAllDepartments();

        assertEquals(2, result.size());
        assertEquals("Computer Science", result.get(0).getName());
        verify(departmentRepository).findAll();
    }

    @Test
    void testGetDepartmentById() {
        when(departmentRepository.findById(1L)).thenReturn(Optional.of(cse));

        DepartmentDTO result = departmentService.getDepartmentById(1L);

        assertEquals("Computer Science", result.getName());
        assertEquals("CSE", result.getCode());
    }

    @Test
    void testGetDepartmentByIdNotFound() {
        when(departmentRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> departmentService.getDepartmentById(99L));
    }

    @Test
    void testCreateDepartment() {
        DepartmentDTO dto = new DepartmentDTO();
        dto.setName("Mechanical Engineering");
        dto.setCode("ME");
        dto.setDescription("ME Department");

        when(departmentRepository.existsByCode("ME")).thenReturn(false);
        when(departmentRepository.save(any(Department.class))).thenAnswer(inv -> {
            Department dept = inv.getArgument(0);
            dept.setId(3L);
            dept.setStudents(new ArrayList<>());
            dept.setTeachers(new ArrayList<>());
            return dept;
        });

        DepartmentDTO result = departmentService.createDepartment(dto);

        assertEquals("Mechanical Engineering", result.getName());
        verify(departmentRepository).save(any(Department.class));
    }

    @Test
    void testCreateDepartmentDuplicateCode() {
        DepartmentDTO dto = new DepartmentDTO();
        dto.setCode("CSE");

        when(departmentRepository.existsByCode("CSE")).thenReturn(true);

        assertThrows(RuntimeException.class, () -> departmentService.createDepartment(dto));
        verify(departmentRepository, never()).save(any());
    }

    @Test
    void testUpdateDepartment() {
        DepartmentDTO dto = new DepartmentDTO();
        dto.setName("Updated CS");
        dto.setDescription("Updated Description");

        when(departmentRepository.findById(1L)).thenReturn(Optional.of(cse));
        when(departmentRepository.save(any(Department.class))).thenReturn(cse);

        DepartmentDTO result = departmentService.updateDepartment(1L, dto);

        assertNotNull(result);
        verify(departmentRepository).save(cse);
    }

    @Test
    void testDeleteDepartment() {
        when(departmentRepository.existsById(1L)).thenReturn(true);

        departmentService.deleteDepartment(1L);

        verify(departmentRepository).deleteById(1L);
    }

    @Test
    void testDeleteDepartmentNotFound() {
        when(departmentRepository.existsById(99L)).thenReturn(false);

        assertThrows(RuntimeException.class, () -> departmentService.deleteDepartment(99L));
    }
}
