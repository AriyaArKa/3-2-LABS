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
    void testGetCourseById() {
        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));

        CourseDTO result = courseService.getCourseById(1L);

        assertEquals("CSE101", result.getCode());
        assertEquals("Intro to CS", result.getName());
    }

    @Test
    void testGetCourseByIdNotFound() {
        when(courseRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> courseService.getCourseById(99L));
    }

    @Test
    void testGetCoursesByTeacherId() {
        when(courseRepository.findByTeacherId(1L)).thenReturn(List.of(course));

        List<CourseDTO> result = courseService.getCoursesByTeacherId(1L);

        assertEquals(1, result.size());
        assertEquals("CSE101", result.get(0).getCode());
    }

    @Test
    void testCreateCourse() {
        CourseDTO dto = new CourseDTO();
        dto.setCode("CSE201");
        dto.setName("Data Structures");
        dto.setDescription("DS course");
        dto.setCredits(3);
        dto.setTeacherId(1L);

        when(courseRepository.existsByCode("CSE201")).thenReturn(false);
        when(teacherRepository.findById(1L)).thenReturn(Optional.of(teacher));
        when(courseRepository.save(any(Course.class))).thenAnswer(inv -> {
            Course c = inv.getArgument(0);
            c.setId(2L);
            c.setEnrolledStudents(new HashSet<>());
            return c;
        });

        CourseDTO result = courseService.createCourse(dto);

        assertEquals("CSE201", result.getCode());
        verify(courseRepository).save(any(Course.class));
    }

    @Test
    void testCreateCourseDuplicateCode() {
        CourseDTO dto = new CourseDTO();
        dto.setCode("CSE101");

        when(courseRepository.existsByCode("CSE101")).thenReturn(true);

        assertThrows(RuntimeException.class, () -> courseService.createCourse(dto));
    }

    @Test
    void testUpdateCourse() {
        CourseDTO dto = new CourseDTO();
        dto.setName("Updated Name");
        dto.setDescription("Updated Desc");
        dto.setCredits(4);
        dto.setTeacherId(1L);

        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));
        when(teacherRepository.findById(1L)).thenReturn(Optional.of(teacher));
        when(courseRepository.save(any(Course.class))).thenReturn(course);

        CourseDTO result = courseService.updateCourse(1L, dto);

        assertNotNull(result);
        verify(courseRepository).save(course);
    }

    @Test
    void testDeleteCourse() {
        when(courseRepository.existsById(1L)).thenReturn(true);

        courseService.deleteCourse(1L);

        verify(courseRepository).deleteById(1L);
    }

    @Test
    void testDeleteCourseNotFound() {
        when(courseRepository.existsById(99L)).thenReturn(false);

        assertThrows(RuntimeException.class, () -> courseService.deleteCourse(99L));
    }
}
