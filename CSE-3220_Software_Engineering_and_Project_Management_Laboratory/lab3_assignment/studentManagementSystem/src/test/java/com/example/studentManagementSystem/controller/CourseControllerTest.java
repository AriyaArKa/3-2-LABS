package com.example.studentManagementSystem.controller;

import com.example.studentManagementSystem.config.SecurityConfig;
import com.example.studentManagementSystem.dto.CourseDTO;
import com.example.studentManagementSystem.dto.TeacherDTO;
import com.example.studentManagementSystem.service.CourseService;
import com.example.studentManagementSystem.service.CustomUserDetailsService;
import com.example.studentManagementSystem.service.TeacherService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for CourseController with Spring Security
 */
@WebMvcTest(CourseController.class)
@Import(SecurityConfig.class)
class CourseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CourseService courseService;

    @MockBean
    private TeacherService teacherService;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    @Test
    void testListCoursesUnauthenticated() throws Exception {
        mockMvc.perform(get("/courses"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    @WithMockUser(username = "teacher1", roles = {"TEACHER"})
    void testListCoursesAsTeacher() throws Exception {
        CourseDTO course = new CourseDTO(1L, "CSE101", "Intro to CS", "Basic CS", 3, 1L, "John Smith", 5);
        when(courseService.getAllCourses()).thenReturn(Arrays.asList(course));

        mockMvc.perform(get("/courses"))
                .andExpect(status().isOk())
                .andExpect(view().name("course/list"))
                .andExpect(model().attributeExists("courses"));
    }

    @Test
    @WithMockUser(username = "student1", roles = {"STUDENT"})
    void testListCoursesAsStudentAllowed() throws Exception {
        when(courseService.getAllCourses()).thenReturn(Arrays.asList());

        mockMvc.perform(get("/courses"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "teacher1", roles = {"TEACHER"})
    void testShowCreateCourseFormAsTeacher() throws Exception {
        when(teacherService.getAllTeachers()).thenReturn(Arrays.asList());

        mockMvc.perform(get("/courses/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("course/form"));
    }

    @Test
    @WithMockUser(username = "student1", roles = {"STUDENT"})
    void testShowCreateCourseFormAsStudentForbidden() throws Exception {
        mockMvc.perform(get("/courses/new"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "teacher1", roles = {"TEACHER"})
    void testCreateCourseAsTeacher() throws Exception {
        CourseDTO saved = new CourseDTO(1L, "CSE201", "Data Structures", "DS", 3, 1L, "John", 0);
        when(courseService.createCourse(any())).thenReturn(saved);

        mockMvc.perform(post("/courses/create")
                        .with(csrf())
                        .param("code", "CSE201")
                        .param("name", "Data Structures")
                        .param("credits", "3"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/courses"));
    }

    @Test
    @WithMockUser(username = "student1", roles = {"STUDENT"})
    void testCreateCourseAsStudentForbidden() throws Exception {
        mockMvc.perform(post("/courses/create")
                        .with(csrf())
                        .param("code", "TEST101")
                        .param("name", "Test"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "teacher1", roles = {"TEACHER"})
    void testViewCourseAsTeacher() throws Exception {
        CourseDTO course = new CourseDTO(1L, "CSE101", "Intro to CS", "Basic CS", 3, 1L, "John Smith", 5);
        when(courseService.getCourseById(1L)).thenReturn(course);

        mockMvc.perform(get("/courses/view/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("course/view"));
    }

    @Test
    @WithMockUser(username = "student1", roles = {"STUDENT"})
    void testViewCourseAsStudentAllowed() throws Exception {
        CourseDTO course = new CourseDTO(1L, "CSE101", "Intro to CS", "Basic CS", 3, 1L, "John Smith", 5);
        when(courseService.getCourseById(1L)).thenReturn(course);

        mockMvc.perform(get("/courses/view/1"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "teacher1", roles = {"TEACHER"})
    void testDeleteCourseAsTeacher() throws Exception {
        doNothing().when(courseService).deleteCourse(1L);

        mockMvc.perform(post("/courses/delete/1").with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/courses"));
    }

    @Test
    @WithMockUser(username = "student1", roles = {"STUDENT"})
    void testDeleteCourseAsStudentForbidden() throws Exception {
        mockMvc.perform(post("/courses/delete/1").with(csrf()))
                .andExpect(status().isForbidden());
    }
}
