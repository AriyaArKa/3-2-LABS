package com.example.studentManagementSystem.repository;

import com.example.studentManagementSystem.entity.Department;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Repository tests for DepartmentRepository using H2 in-memory database
 */
@DataJpaTest
class DepartmentRepositoryTest {

    @Autowired
    private DepartmentRepository departmentRepository;

    @BeforeEach
    void setUp() {
        departmentRepository.deleteAll();
    }

    @Test
    void testFindAndExistsByCodeAndName() {
        departmentRepository.save(new Department("Computer Science", "CSE", "CS Dept"));

        assertTrue(departmentRepository.findByCode("CSE").isPresent());
        assertTrue(departmentRepository.findByName("Computer Science").isPresent());
        assertTrue(departmentRepository.existsByCode("CSE"));
        assertFalse(departmentRepository.existsByCode("EEE"));
        assertTrue(departmentRepository.existsByName("Computer Science"));
        assertFalse(departmentRepository.existsByName("Electrical Engineering"));
    }
}
