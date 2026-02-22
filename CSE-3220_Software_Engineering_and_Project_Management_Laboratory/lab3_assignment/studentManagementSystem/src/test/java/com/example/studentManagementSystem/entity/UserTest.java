package com.example.studentManagementSystem.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for User entity
 */
class UserTest {

    @Test
    void testConstructorAndDefaults() {
        Role role = new Role(1L, "ROLE_STUDENT");
        User user = new User("alice", "pass123", "alice@test.com", role);

        assertEquals("alice", user.getUsername());
        assertEquals("pass123", user.getPassword());
        assertEquals("alice@test.com", user.getEmail());
        assertEquals(role, user.getRole());
        assertTrue(user.isEnabled());
    }
}
