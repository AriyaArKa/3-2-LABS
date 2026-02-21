package com.example.studentManagementSystem.repository;

import com.example.studentManagementSystem.entity.Course;
import com.example.studentManagementSystem.entity.Department;
import com.example.studentManagementSystem.entity.Teacher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Repository tests for CourseRepository using H2 in-memory database
 */
@DataJpaTest
class CourseRepositoryTest {

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private TeacherRepository teacherRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    private Teacher teacher;

    @BeforeEach
    void setUp() {
        courseRepository.deleteAll();
        teacherRepository.deleteAll();
        departmentRepository.deleteAll();

        Department dept = departmentRepository.save(new Department("CSE", "CS", "CS Dept"));
        teacher = new Teacher("John", "Doe", "john@test.com", "555-0001", "PhD", "Professor");
        teacher.setDepartment(dept);
        teacher = teacherRepository.save(teacher);
    }

    @Test
    void testSaveCourse() {
        Course course = new Course("CSE101", "Intro to CS", "Basic CS", 3);
        course.setTeacher(teacher);
        Course saved = courseRepository.save(course);

        assertNotNull(saved.getId());
        assertEquals("CSE101", saved.getCode());
    }

    @Test
    void testFindByCode() {
        Course course = new Course("CSE101", "Intro to CS", "Basic CS", 3);
        courseRepository.save(course);

        Optional<Course> found = courseRepository.findByCode("CSE101");
        assertTrue(found.isPresent());
        assertEquals("Intro to CS", found.get().getName());
    }

    @Test
    void testFindByTeacherId() {
        Course course1 = new Course("CSE101", "Intro to CS", "Basic CS", 3);
        course1.setTeacher(teacher);
        courseRepository.save(course1);

        Course course2 = new Course("CSE201", "Data Structures", "DS", 3);
        course2.setTeacher(teacher);
        courseRepository.save(course2);

        List<Course> found = courseRepository.findByTeacherId(teacher.getId());
        assertEquals(2, found.size());
    }

    @Test
    void testExistsByCode() {
        courseRepository.save(new Course("CSE101", "Intro to CS", "Basic CS", 3));

        assertTrue(courseRepository.existsByCode("CSE101"));
        assertFalse(courseRepository.existsByCode("CSE999"));
    }
}
