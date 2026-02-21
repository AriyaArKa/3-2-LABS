package com.example.studentManagementSystem.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Role entity
 */
class RoleTest {

    @Test
    void testNoArgsConstructor() {
        Role role = new Role();
        assertNull(role.getId());
        assertNull(role.getName());
    }

    @Test
    void testParameterizedConstructor() {
        Role role = new Role("ROLE_STUDENT");
        assertEquals("ROLE_STUDENT", role.getName());
    }

    @Test
    void testAllArgsConstructor() {
        Role role = new Role(1L, "ROLE_TEACHER");
        assertEquals(1L, role.getId());
        assertEquals("ROLE_TEACHER", role.getName());
    }

    @Test
    void testSettersAndGetters() {
        Role role = new Role();
        role.setId(2L);
        role.setName("ROLE_STUDENT");

        assertEquals(2L, role.getId());
        assertEquals("ROLE_STUDENT", role.getName());
    }

    @Test
    void testEqualsAndHashCode() {
        Role role1 = new Role(1L, "ROLE_STUDENT");
        Role role2 = new Role(1L, "ROLE_STUDENT");
        assertEquals(role1, role2);
        assertEquals(role1.hashCode(), role2.hashCode());
    }

    @Test
    void testNotEquals() {
        Role role1 = new Role(1L, "ROLE_STUDENT");
        Role role2 = new Role(2L, "ROLE_TEACHER");
        assertNotEquals(role1, role2);
    }
}
