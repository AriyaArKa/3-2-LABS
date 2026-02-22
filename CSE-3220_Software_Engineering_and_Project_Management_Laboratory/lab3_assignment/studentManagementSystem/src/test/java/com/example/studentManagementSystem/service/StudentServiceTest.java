package com.example.studentManagementSystem.service;

import com.example.studentManagementSystem.dto.StudentDTO;
import com.example.studentManagementSystem.entity.*;
import com.example.studentManagementSystem.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Unit tests for StudentService using Mockito
 */
@ExtendWith(MockitoExtension.class)
class StudentServiceTest {

    @Mock private StudentRepository studentRepository;
    @Mock private DepartmentRepository departmentRepository;
    @Mock private CourseRepository courseRepository;
    @Mock private UserRepository userRepository;
    @Mock private RoleRepository roleRepository;
    @Mock private PasswordEncoder passwordEncoder;

    @InjectMocks
    private StudentService studentService;

    private Student student;
    private Department cse;
    private Role studentRole;
    private User user;

    @BeforeEach
    void setUp() {
        studentRole = new Role("ROLE_STUDENT");
        studentRole.setId(1L);

        user = new User("alice", "encodedPass", "alice@test.com", studentRole);
        user.setId(1L);

        cse = new Department("Computer Science", "CSE", "CS Dept");
        cse.setId(1L);
        cse.setStudents(new ArrayList<>());
        cse.setTeachers(new ArrayList<>());

        student = new Student("2024-001", "Alice", "Williams", "alice@test.com", "555-0001", "Address", 3);
        student.setId(1L);
        student.setDepartment(cse);
        student.setUser(user);
    }

    @Test
    void testGetAllStudents() {
        when(studentRepository.findAll()).thenReturn(List.of(student));

        List<StudentDTO> result = studentService.getAllStudents();

        assertEquals(1, result.size());
        assertEquals("Alice", result.get(0).getFirstName());
        assertEquals("Computer Science", result.get(0).getDepartmentName());
    }

    @Test
    void testGetStudentByIdNotFound() {
        when(studentRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> studentService.getStudentById(99L));
    }

    @Test
    void testDeleteStudent() {
        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));

        studentService.deleteStudent(1L);

        verify(userRepository).delete(user);
        verify(studentRepository).deleteById(1L);
    }

    @Test
    void testEnrollInCourse() {
        Course course = new Course("CSE101", "Intro to CS", "Basic CS", 3);
        course.setId(1L);

        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));
        when(studentRepository.save(any(Student.class))).thenReturn(student);

        studentService.enrollInCourse(1L, 1L);

        assertTrue(student.getEnrolledCourses().contains(course));
        verify(studentRepository).save(student);
    }
}
