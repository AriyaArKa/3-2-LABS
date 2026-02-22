package com.example.studentManagementSystem.service;

import com.example.studentManagementSystem.dto.CourseDTO;
import com.example.studentManagementSystem.entity.Course;
import com.example.studentManagementSystem.entity.Teacher;
import com.example.studentManagementSystem.repository.CourseRepository;
import com.example.studentManagementSystem.repository.TeacherRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for CourseService using Mockito
 */
@ExtendWith(MockitoExtension.class)
class CourseServiceTest {

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private TeacherRepository teacherRepository;

    @InjectMocks
    private CourseService courseService;

    private Course course;
    private Teacher teacher;

    @BeforeEach
    void setUp() {
        teacher = new Teacher("John", "Smith", "john@test.com", "555-0001", "PhD", "Professor");
        teacher.setId(1L);

        course = new Course("CSE101", "Intro to CS", "Basic CS", 3);
        course.setId(1L);
        course.setTeacher(teacher);
        course.setEnrolledStudents(new HashSet<>());
    }

    @Test
    void testGetAllCourses() {
        when(courseRepository.findAll()).thenReturn(List.of(course));

        List<CourseDTO> result = courseService.getAllCourses();

        assertEquals(1, result.size());
        assertEquals("CSE101", result.get(0).getCode());
        assertEquals("John Smith", result.get(0).getTeacherName());
    }

    @Test
    void testCreateCourseDuplicateCodeThrows() {
        CourseDTO dto = new CourseDTO();
        dto.setCode("CSE101");

        when(courseRepository.existsByCode("CSE101")).thenReturn(true);

        assertThrows(RuntimeException.class, () -> courseService.createCourse(dto));
    }

    @Test
    void testDeleteCourseNotFoundThrows() {
        when(courseRepository.existsById(99L)).thenReturn(false);

        assertThrows(RuntimeException.class, () -> courseService.deleteCourse(99L));
    }
}
