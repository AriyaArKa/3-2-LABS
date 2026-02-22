package com.example.studentManagementSystem.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Student entity
 */
class StudentTest {

    @Test
    void testConstructorAndFullName() {
        Student student = new Student("2024-001", "Alice", "Smith",
                "alice@test.com", "555-0001", "123 Main St", 3);
        assertEquals("Alice Smith", student.getFullName());
        assertEquals("2024-001", student.getStudentId());
        assertNotNull(student.getEnrolledCourses());
    }

    @Test
    void testEnrollAndDropCourse() {
        Student student = new Student();
        Course course = new Course("CSE101", "Intro to CS", "Basic CS", 3);

        student.enrollInCourse(course);
        assertTrue(student.getEnrolledCourses().contains(course));
        assertTrue(course.getEnrolledStudents().contains(student));

        student.dropCourse(course);
        assertFalse(student.getEnrolledCourses().contains(course));
        assertFalse(course.getEnrolledStudents().contains(student));
    }
}
