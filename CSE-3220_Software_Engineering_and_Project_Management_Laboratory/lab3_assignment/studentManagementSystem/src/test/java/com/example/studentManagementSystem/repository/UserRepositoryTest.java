package com.example.studentManagementSystem.repository;

import com.example.studentManagementSystem.entity.Role;
import com.example.studentManagementSystem.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Repository tests for UserRepository using H2 in-memory database
 */
@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    private Role studentRole;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        roleRepository.deleteAll();
        studentRole = roleRepository.save(new Role("ROLE_STUDENT"));
    }

    @Test
    void testFindAndExistsByUsernameAndEmail() {
        userRepository.save(new User("alice", "pass", "alice@test.com", studentRole));

        assertTrue(userRepository.findByUsername("alice").isPresent());
        assertFalse(userRepository.findByUsername("nonexistent").isPresent());
        assertTrue(userRepository.findByEmail("alice@test.com").isPresent());
        assertTrue(userRepository.existsByUsername("alice"));
        assertFalse(userRepository.existsByUsername("bob"));
        assertTrue(userRepository.existsByEmail("alice@test.com"));
        assertFalse(userRepository.existsByEmail("bob@test.com"));
    }
}
