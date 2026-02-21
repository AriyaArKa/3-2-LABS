package com.example.studentManagementSystem.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Student entity
 */
class StudentTest {

    @Test
    void testNoArgsConstructor() {
        Student student = new Student();
        assertNull(student.getId());
        assertNotNull(student.getEnrolledCourses());
        assertTrue(student.getEnrolledCourses().isEmpty());
    }

    @Test
    void testParameterizedConstructor() {
        Student student = new Student("2024-001", "Alice", "Smith",
                "alice@test.com", "555-0001", "123 Main St", 3);

        assertEquals("2024-001", student.getStudentId());
        assertEquals("Alice", student.getFirstName());
        assertEquals("Smith", student.getLastName());
        assertEquals("alice@test.com", student.getEmail());
        assertEquals("555-0001", student.getPhone());
        assertEquals("123 Main St", student.getAddress());
        assertEquals(3, student.getSemester());
    }

    @Test
    void testGetFullName() {
        Student student = new Student("2024-001", "Alice", "Smith",
                "alice@test.com", "555-0001", "123 Main St", 3);
        assertEquals("Alice Smith", student.getFullName());
    }

    @Test
    void testEnrollInCourse() {
        Student student = new Student();
        Course course = new Course("CSE101", "Intro to CS", "Basic CS", 3);

        student.enrollInCourse(course);

        assertTrue(student.getEnrolledCourses().contains(course));
        assertTrue(course.getEnrolledStudents().contains(student));
    }

    @Test
    void testDropCourse() {
        Student student = new Student();
        Course course = new Course("CSE101", "Intro to CS", "Basic CS", 3);

        student.enrollInCourse(course);
        assertTrue(student.getEnrolledCourses().contains(course));

        student.dropCourse(course);
        assertFalse(student.getEnrolledCourses().contains(course));
        assertFalse(course.getEnrolledStudents().contains(student));
    }

    @Test
    void testSetDepartment() {
        Student student = new Student();
        Department dept = new Department("CSE", "CS", "Computer Science");
        student.setDepartment(dept);
        assertEquals(dept, student.getDepartment());
    }

    @Test
    void testSetUser() {
        Student student = new Student();
        Role role = new Role(1L, "ROLE_STUDENT");
        User user = new User("alice", "pass", "alice@test.com", role);
        student.setUser(user);
        assertEquals(user, student.getUser());
    }
}
