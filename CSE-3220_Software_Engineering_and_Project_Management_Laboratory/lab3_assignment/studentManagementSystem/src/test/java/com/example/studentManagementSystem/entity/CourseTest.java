package com.example.studentManagementSystem.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Course entity
 */
class CourseTest {

    @Test
    void testConstructorAndTeacher() {
        Course course = new Course("CSE101", "Intro to CS", "Basic CS", 3);
        assertEquals("CSE101", course.getCode());
        assertEquals("Intro to CS", course.getName());
        assertEquals(3, course.getCredits());
        assertNotNull(course.getEnrolledStudents());
        assertTrue(course.getEnrolledStudents().isEmpty());

        Teacher teacher = new Teacher("John", "Doe", "john@test.com",
                "555-0001", "PhD", "Professor");
        course.setTeacher(teacher);
        assertEquals(teacher, course.getTeacher());
    }
}
