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
    void testGetStudentById() {
        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));

        StudentDTO result = studentService.getStudentById(1L);

        assertEquals("2024-001", result.getStudentId());
        assertEquals("Alice", result.getFirstName());
    }

    @Test
    void testGetStudentByIdNotFound() {
        when(studentRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> studentService.getStudentById(99L));
    }

    @Test
    void testGetStudentByUserId() {
        when(studentRepository.findByUserId(1L)).thenReturn(Optional.of(student));

        StudentDTO result = studentService.getStudentByUserId(1L);

        assertEquals("Alice", result.getFirstName());
    }

    @Test
    void testCreateStudent() {
        StudentDTO dto = new StudentDTO();
        dto.setStudentId("2024-002");
        dto.setFirstName("Bob");
        dto.setLastName("Brown");
        dto.setEmail("bob@test.com");
        dto.setPhone("555-0002");
        dto.setAddress("456 Oak Ave");
        dto.setSemester(2);
        dto.setUsername("bob");
        dto.setPassword("pass123");
        dto.setDepartmentId(1L);

        when(studentRepository.existsByStudentId("2024-002")).thenReturn(false);
        when(studentRepository.existsByEmail("bob@test.com")).thenReturn(false);
        when(userRepository.existsByUsername("bob")).thenReturn(false);
        when(roleRepository.findByName("ROLE_STUDENT")).thenReturn(Optional.of(studentRole));
        when(passwordEncoder.encode("pass123")).thenReturn("encodedPass");
        when(userRepository.save(any(User.class))).thenAnswer(inv -> {
            User u = inv.getArgument(0);
            u.setId(2L);
            return u;
        });
        when(departmentRepository.findById(1L)).thenReturn(Optional.of(cse));
        when(studentRepository.save(any(Student.class))).thenAnswer(inv -> {
            Student s = inv.getArgument(0);
            s.setId(2L);
            return s;
        });

        StudentDTO result = studentService.createStudent(dto);

        assertEquals("Bob", result.getFirstName());
        verify(userRepository).save(any(User.class));
        verify(studentRepository).save(any(Student.class));
    }

    @Test
    void testCreateStudentDuplicateStudentId() {
        StudentDTO dto = new StudentDTO();
        dto.setStudentId("2024-001");

        when(studentRepository.existsByStudentId("2024-001")).thenReturn(true);

        assertThrows(RuntimeException.class, () -> studentService.createStudent(dto));
    }

    @Test
    void testCreateStudentDuplicateEmail() {
        StudentDTO dto = new StudentDTO();
        dto.setStudentId("2024-002");
        dto.setEmail("alice@test.com");

        when(studentRepository.existsByStudentId("2024-002")).thenReturn(false);
        when(studentRepository.existsByEmail("alice@test.com")).thenReturn(true);

        assertThrows(RuntimeException.class, () -> studentService.createStudent(dto));
    }

    @Test
    void testUpdateStudent() {
        StudentDTO dto = new StudentDTO();
        dto.setFirstName("Alice Updated");
        dto.setLastName("Williams");
        dto.setPhone("555-9999");
        dto.setAddress("New Address");
        dto.setSemester(4);
        dto.setDepartmentId(1L);

        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
        when(departmentRepository.findById(1L)).thenReturn(Optional.of(cse));
        when(studentRepository.save(any(Student.class))).thenReturn(student);

        StudentDTO result = studentService.updateStudent(1L, dto);

        assertNotNull(result);
        verify(studentRepository).save(student);
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

    @Test
    void testDropCourse() {
        Course course = new Course("CSE101", "Intro to CS", "Basic CS", 3);
        course.setId(1L);
        student.enrollInCourse(course);

        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));
        when(studentRepository.save(any(Student.class))).thenReturn(student);

        studentService.dropCourse(1L, 1L);

        assertFalse(student.getEnrolledCourses().contains(course));
        verify(studentRepository).save(student);
    }

    @Test
    void testGetEnrolledCourses() {
        Course course = new Course("CSE101", "Intro to CS", "Basic CS", 3);
        student.enrollInCourse(course);

        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));

        Set<Course> result = studentService.getEnrolledCourses(1L);

        assertEquals(1, result.size());
        assertTrue(result.contains(course));
    }
}
