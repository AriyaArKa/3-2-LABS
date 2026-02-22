package com.example.studentManagementSystem.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Teacher entity
 */
class TeacherTest {

    @Test
    void testConstructorAndFullName() {
        Teacher teacher = new Teacher("John", "Smith", "john@test.com",
                "555-0001", "PhD in CS", "Professor");
        assertEquals("John Smith", teacher.getFullName());
        assertEquals("john@test.com", teacher.getEmail());
        assertNotNull(teacher.getCourses());
        assertTrue(teacher.getCourses().isEmpty());
    }
}
