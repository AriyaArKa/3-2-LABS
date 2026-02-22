package com.example.studentManagementSystem.repository;

import com.example.studentManagementSystem.entity.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Repository tests for StudentRepository using H2 in-memory database
 */
@DataJpaTest
class StudentRepositoryTest {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    private Department cse;
    private Role studentRole;

    @BeforeEach
    void setUp() {
        studentRepository.deleteAll();
        userRepository.deleteAll();
        departmentRepository.deleteAll();
        roleRepository.deleteAll();

        studentRole = roleRepository.save(new Role("ROLE_STUDENT"));
        cse = departmentRepository.save(new Department("Computer Science", "CSE", "CS Dept"));
    }

    private Student createStudent(String studentId, String email, String username) {
        User user = userRepository.save(new User(username, "pass", email, studentRole));
        Student student = new Student(studentId, "Test", "Student", email, "555-0001", "Address", 3);
        student.setDepartment(cse);
        student.setUser(user);
        return studentRepository.save(student);
    }

    @Test
    void testFindByStudentIdAndEmail() {
        createStudent("2024-001", "test@test.com", "test1");

        assertTrue(studentRepository.findByStudentId("2024-001").isPresent());
        assertTrue(studentRepository.findByEmail("test@test.com").isPresent());
        assertTrue(studentRepository.existsByStudentId("2024-001"));
        assertFalse(studentRepository.existsByStudentId("2024-999"));
    }

    @Test
    void testFindByUserIdAndDepartmentId() {
        Student saved = createStudent("2024-001", "test1@test.com", "test1");
        createStudent("2024-002", "test2@test.com", "test2");

        assertTrue(studentRepository.findByUserId(saved.getUser().getId()).isPresent());
        assertEquals(2, studentRepository.findByDepartmentId(cse.getId()).size());
    }
}
