package com.example.studentManagementSystem.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for User entity
 */
class UserTest {

    @Test
    void testNoArgsConstructor() {
        User user = new User();
        assertNull(user.getId());
        assertNull(user.getUsername());
        assertTrue(user.isEnabled());
    }

    @Test
    void testParameterizedConstructor() {
        Role role = new Role(1L, "ROLE_STUDENT");
        User user = new User("alice", "pass123", "alice@test.com", role);

        assertEquals("alice", user.getUsername());
        assertEquals("pass123", user.getPassword());
        assertEquals("alice@test.com", user.getEmail());
        assertEquals(role, user.getRole());
        assertTrue(user.isEnabled());
    }

    @Test
    void testSettersAndGetters() {
        User user = new User();
        Role role = new Role(1L, "ROLE_TEACHER");

        user.setId(10L);
        user.setUsername("john");
        user.setPassword("secret");
        user.setEmail("john@test.com");
        user.setEnabled(false);
        user.setRole(role);

        assertEquals(10L, user.getId());
        assertEquals("john", user.getUsername());
        assertEquals("secret", user.getPassword());
        assertEquals("john@test.com", user.getEmail());
        assertFalse(user.isEnabled());
        assertEquals(role, user.getRole());
    }

    @Test
    void testDefaultEnabled() {
        User user = new User();
        assertTrue(user.isEnabled());
    }
}
