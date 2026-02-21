package com.example.studentManagementSystem.repository;

import com.example.studentManagementSystem.entity.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Repository tests for RoleRepository using H2 in-memory database
 */
@DataJpaTest
class RoleRepositoryTest {

    @Autowired
    private RoleRepository roleRepository;

    @BeforeEach
    void setUp() {
        roleRepository.deleteAll();
    }

    @Test
    void testSaveRole() {
        Role role = new Role("ROLE_STUDENT");
        Role saved = roleRepository.save(role);

        assertNotNull(saved.getId());
        assertEquals("ROLE_STUDENT", saved.getName());
    }

    @Test
    void testFindByName() {
        roleRepository.save(new Role("ROLE_TEACHER"));

        Optional<Role> found = roleRepository.findByName("ROLE_TEACHER");
        assertTrue(found.isPresent());
        assertEquals("ROLE_TEACHER", found.get().getName());
    }

    @Test
    void testFindByNameNotFound() {
        Optional<Role> found = roleRepository.findByName("ROLE_ADMIN");
        assertFalse(found.isPresent());
    }

    @Test
    void testExistsByName() {
        roleRepository.save(new Role("ROLE_STUDENT"));

        assertTrue(roleRepository.existsByName("ROLE_STUDENT"));
        assertFalse(roleRepository.existsByName("ROLE_ADMIN"));
    }
}
