package com.example.studentManagementSystem.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Department entity
 */
class DepartmentTest {

    @Test
    void testNoArgsConstructor() {
        Department dept = new Department();
        assertNull(dept.getId());
        assertNotNull(dept.getStudents());
        assertNotNull(dept.getTeachers());
    }

    @Test
    void testParameterizedConstructor() {
        Department dept = new Department("Computer Science", "CSE", "CS Department");

        assertEquals("Computer Science", dept.getName());
        assertEquals("CSE", dept.getCode());
        assertEquals("CS Department", dept.getDescription());
    }

    @Test
    void testSettersAndGetters() {
        Department dept = new Department();
        dept.setId(1L);
        dept.setName("Electrical Engineering");
        dept.setCode("EEE");
        dept.setDescription("EE Department");

        assertEquals(1L, dept.getId());
        assertEquals("Electrical Engineering", dept.getName());
        assertEquals("EEE", dept.getCode());
        assertEquals("EE Department", dept.getDescription());
    }
}
