package com.example.studentManagementSystem.repository;

import com.example.studentManagementSystem.entity.Role;
import com.example.studentManagementSystem.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

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
    void testSaveUser() {
        User user = new User("alice", "password123", "alice@test.com", studentRole);
        User saved = userRepository.save(user);

        assertNotNull(saved.getId());
        assertEquals("alice", saved.getUsername());
    }

    @Test
    void testFindByUsername() {
        userRepository.save(new User("alice", "pass", "alice@test.com", studentRole));

        Optional<User> found = userRepository.findByUsername("alice");
        assertTrue(found.isPresent());
        assertEquals("alice@test.com", found.get().getEmail());
    }

    @Test
    void testFindByUsernameNotFound() {
        Optional<User> found = userRepository.findByUsername("nonexistent");
        assertFalse(found.isPresent());
    }

    @Test
    void testFindByEmail() {
        userRepository.save(new User("alice", "pass", "alice@test.com", studentRole));

        Optional<User> found = userRepository.findByEmail("alice@test.com");
        assertTrue(found.isPresent());
        assertEquals("alice", found.get().getUsername());
    }

    @Test
    void testExistsByUsername() {
        userRepository.save(new User("alice", "pass", "alice@test.com", studentRole));

        assertTrue(userRepository.existsByUsername("alice"));
        assertFalse(userRepository.existsByUsername("bob"));
    }

    @Test
    void testExistsByEmail() {
        userRepository.save(new User("alice", "pass", "alice@test.com", studentRole));

        assertTrue(userRepository.existsByEmail("alice@test.com"));
        assertFalse(userRepository.existsByEmail("bob@test.com"));
    }
}
