package com.example.studentManagementSystem.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Role entity
 */
class RoleTest {

    @Test
    void testConstructorAndGetters() {
        Role role = new Role(1L, "ROLE_TEACHER");
        assertEquals(1L, role.getId());
        assertEquals("ROLE_TEACHER", role.getName());
    }

    @Test
    void testEqualsAndHashCode() {
        Role role1 = new Role(1L, "ROLE_STUDENT");
        Role role2 = new Role(1L, "ROLE_STUDENT");
        assertEquals(role1, role2);
        assertEquals(role1.hashCode(), role2.hashCode());
        assertNotEquals(role1, new Role(2L, "ROLE_TEACHER"));
    }
}
