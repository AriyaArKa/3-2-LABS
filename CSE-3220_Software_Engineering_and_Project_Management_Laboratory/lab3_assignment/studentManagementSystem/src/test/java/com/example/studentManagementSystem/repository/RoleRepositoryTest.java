package com.example.studentManagementSystem.repository;

import com.example.studentManagementSystem.entity.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

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
    void testFindByNameAndExistsByName() {
        roleRepository.save(new Role("ROLE_STUDENT"));

        assertTrue(roleRepository.findByName("ROLE_STUDENT").isPresent());
        assertFalse(roleRepository.findByName("ROLE_ADMIN").isPresent());
        assertTrue(roleRepository.existsByName("ROLE_STUDENT"));
        assertFalse(roleRepository.existsByName("ROLE_ADMIN"));
    }
}
