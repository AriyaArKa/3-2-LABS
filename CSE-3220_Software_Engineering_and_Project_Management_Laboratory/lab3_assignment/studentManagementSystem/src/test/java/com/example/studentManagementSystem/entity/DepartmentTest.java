package com.example.studentManagementSystem.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Department entity
 */
class DepartmentTest {

    @Test
    void testConstructorAndListsInitialized() {
        Department dept = new Department("Computer Science", "CSE", "CS Department");
        assertEquals("Computer Science", dept.getName());
        assertEquals("CSE", dept.getCode());
        assertEquals("CS Department", dept.getDescription());
        assertNotNull(dept.getStudents());
        assertNotNull(dept.getTeachers());
    }
}
