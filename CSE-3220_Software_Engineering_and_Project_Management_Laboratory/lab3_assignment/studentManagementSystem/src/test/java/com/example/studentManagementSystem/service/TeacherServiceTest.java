package com.example.studentManagementSystem.service;

import com.example.studentManagementSystem.dto.TeacherDTO;
import com.example.studentManagementSystem.entity.*;
import com.example.studentManagementSystem.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for TeacherService using Mockito
 */
@ExtendWith(MockitoExtension.class)
class TeacherServiceTest {

    @Mock private TeacherRepository teacherRepository;
    @Mock private DepartmentRepository departmentRepository;
    @Mock private UserRepository userRepository;
    @Mock private RoleRepository roleRepository;
    @Mock private PasswordEncoder passwordEncoder;

    @InjectMocks
    private TeacherService teacherService;

    private Teacher teacher;
    private User user;

    @BeforeEach
    void setUp() {
        Role teacherRole = new Role("ROLE_TEACHER");
        teacherRole.setId(1L);

        user = new User("teacher1", "encodedPass", "john@test.com", teacherRole);
        user.setId(1L);

        Department cse = new Department("Computer Science", "CSE", "CS Dept");
        cse.setId(1L);
        cse.setStudents(new ArrayList<>());
        cse.setTeachers(new ArrayList<>());

        teacher = new Teacher("John", "Smith", "john@test.com", "555-0001", "PhD", "Professor");
        teacher.setId(1L);
        teacher.setDepartment(cse);
        teacher.setUser(user);
    }

    @Test
    void testGetAllTeachers() {
        when(teacherRepository.findAll()).thenReturn(List.of(teacher));

        List<TeacherDTO> result = teacherService.getAllTeachers();

        assertEquals(1, result.size());
        assertEquals("John", result.get(0).getFirstName());
    }

    @Test
    void testDeleteTeacher() {
        when(teacherRepository.findById(1L)).thenReturn(Optional.of(teacher));

        teacherService.deleteTeacher(1L);

        verify(userRepository).delete(user);
        verify(teacherRepository).deleteById(1L);
    }

    @Test
    void testDeleteTeacherNotFoundThrows() {
        when(teacherRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> teacherService.deleteTeacher(99L));
    }
}
