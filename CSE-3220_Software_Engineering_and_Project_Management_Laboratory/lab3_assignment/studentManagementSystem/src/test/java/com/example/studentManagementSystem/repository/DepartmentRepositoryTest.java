package com.example.studentManagementSystem.repository;

import com.example.studentManagementSystem.entity.Department;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

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
    void testSaveDepartment() {
        Department dept = new Department("Computer Science", "CSE", "CS Department");
        Department saved = departmentRepository.save(dept);

        assertNotNull(saved.getId());
        assertEquals("Computer Science", saved.getName());
    }

    @Test
    void testFindByCode() {
        departmentRepository.save(new Department("Computer Science", "CSE", "CS Dept"));

        Optional<Department> found = departmentRepository.findByCode("CSE");
        assertTrue(found.isPresent());
        assertEquals("Computer Science", found.get().getName());
    }

    @Test
    void testFindByName() {
        departmentRepository.save(new Department("Computer Science", "CSE", "CS Dept"));

        Optional<Department> found = departmentRepository.findByName("Computer Science");
        assertTrue(found.isPresent());
        assertEquals("CSE", found.get().getCode());
    }

    @Test
    void testExistsByCode() {
        departmentRepository.save(new Department("Computer Science", "CSE", "CS Dept"));

        assertTrue(departmentRepository.existsByCode("CSE"));
        assertFalse(departmentRepository.existsByCode("EEE"));
    }

    @Test
    void testExistsByName() {
        departmentRepository.save(new Department("Computer Science", "CSE", "CS Dept"));

        assertTrue(departmentRepository.existsByName("Computer Science"));
        assertFalse(departmentRepository.existsByName("Electrical Engineering"));
    }
}
