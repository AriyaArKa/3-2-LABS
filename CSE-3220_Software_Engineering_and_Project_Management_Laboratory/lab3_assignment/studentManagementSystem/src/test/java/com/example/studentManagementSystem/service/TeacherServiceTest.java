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
import static org.mockito.ArgumentMatchers.any;
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
    private Department cse;
    private Role teacherRole;
    private User user;

    @BeforeEach
    void setUp() {
        teacherRole = new Role("ROLE_TEACHER");
        teacherRole.setId(1L);

        user = new User("teacher1", "encodedPass", "john@test.com", teacherRole);
        user.setId(1L);

        cse = new Department("Computer Science", "CSE", "CS Dept");
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
    void testGetTeacherById() {
        when(teacherRepository.findById(1L)).thenReturn(Optional.of(teacher));

        TeacherDTO result = teacherService.getTeacherById(1L);

        assertEquals("John", result.getFirstName());
        assertEquals("Smith", result.getLastName());
    }

    @Test
    void testGetTeacherByIdNotFound() {
        when(teacherRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> teacherService.getTeacherById(99L));
    }

    @Test
    void testGetTeacherByUserId() {
        when(teacherRepository.findByUserId(1L)).thenReturn(Optional.of(teacher));

        TeacherDTO result = teacherService.getTeacherByUserId(1L);

        assertEquals("John", result.getFirstName());
    }

    @Test
    void testCreateTeacher() {
        TeacherDTO dto = new TeacherDTO();
        dto.setFirstName("Sarah");
        dto.setLastName("Johnson");
        dto.setEmail("sarah@test.com");
        dto.setPhone("555-0002");
        dto.setQualification("PhD EE");
        dto.setDesignation("Associate Professor");
        dto.setUsername("sarah");
        dto.setPassword("pass123");
        dto.setDepartmentId(1L);

        when(teacherRepository.existsByEmail("sarah@test.com")).thenReturn(false);
        when(userRepository.existsByUsername("sarah")).thenReturn(false);
        when(roleRepository.findByName("ROLE_TEACHER")).thenReturn(Optional.of(teacherRole));
        when(passwordEncoder.encode("pass123")).thenReturn("encodedPass");
        when(userRepository.save(any(User.class))).thenAnswer(inv -> {
            User u = inv.getArgument(0);
            u.setId(2L);
            return u;
        });
        when(departmentRepository.findById(1L)).thenReturn(Optional.of(cse));
        when(teacherRepository.save(any(Teacher.class))).thenAnswer(inv -> {
            Teacher t = inv.getArgument(0);
            t.setId(2L);
            return t;
        });

        TeacherDTO result = teacherService.createTeacher(dto);

        assertEquals("Sarah", result.getFirstName());
        verify(userRepository).save(any(User.class));
        verify(teacherRepository).save(any(Teacher.class));
    }

    @Test
    void testCreateTeacherDuplicateEmail() {
        TeacherDTO dto = new TeacherDTO();
        dto.setEmail("john@test.com");

        when(teacherRepository.existsByEmail("john@test.com")).thenReturn(true);

        assertThrows(RuntimeException.class, () -> teacherService.createTeacher(dto));
    }

    @Test
    void testUpdateTeacher() {
        TeacherDTO dto = new TeacherDTO();
        dto.setFirstName("John Updated");
        dto.setLastName("Smith");
        dto.setPhone("555-9999");
        dto.setQualification("PhD CS Updated");
        dto.setDesignation("Full Professor");
        dto.setDepartmentId(1L);

        when(teacherRepository.findById(1L)).thenReturn(Optional.of(teacher));
        when(departmentRepository.findById(1L)).thenReturn(Optional.of(cse));
        when(teacherRepository.save(any(Teacher.class))).thenReturn(teacher);

        TeacherDTO result = teacherService.updateTeacher(1L, dto);

        assertNotNull(result);
        verify(teacherRepository).save(teacher);
    }

    @Test
    void testDeleteTeacher() {
        when(teacherRepository.findById(1L)).thenReturn(Optional.of(teacher));

        teacherService.deleteTeacher(1L);

        verify(userRepository).delete(user);
        verify(teacherRepository).deleteById(1L);
    }

    @Test
    void testDeleteTeacherNotFound() {
        when(teacherRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> teacherService.deleteTeacher(99L));
    }
}
