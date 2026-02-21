package com.example.studentManagementSystem.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Teacher entity
 */
class TeacherTest {

    @Test
    void testNoArgsConstructor() {
        Teacher teacher = new Teacher();
        assertNull(teacher.getId());
        assertNotNull(teacher.getCourses());
        assertTrue(teacher.getCourses().isEmpty());
    }

    @Test
    void testParameterizedConstructor() {
        Teacher teacher = new Teacher("John", "Smith", "john@test.com",
                "555-0001", "PhD in CS", "Professor");

        assertEquals("John", teacher.getFirstName());
        assertEquals("Smith", teacher.getLastName());
        assertEquals("john@test.com", teacher.getEmail());
        assertEquals("555-0001", teacher.getPhone());
        assertEquals("PhD in CS", teacher.getQualification());
        assertEquals("Professor", teacher.getDesignation());
    }

    @Test
    void testGetFullName() {
        Teacher teacher = new Teacher("John", "Smith", "john@test.com",
                "555-0001", "PhD", "Professor");
        assertEquals("John Smith", teacher.getFullName());
    }

    @Test
    void testSetDepartment() {
        Teacher teacher = new Teacher();
        Department dept = new Department("CSE", "CS", "Computer Science");
        teacher.setDepartment(dept);
        assertEquals(dept, teacher.getDepartment());
    }

    @Test
    void testSetUser() {
        Teacher teacher = new Teacher();
        Role role = new Role(1L, "ROLE_TEACHER");
        User user = new User("john", "pass", "john@test.com", role);
        teacher.setUser(user);
        assertEquals(user, teacher.getUser());
    }
}
