package com.example.studentManagementSystem.controller;

import com.example.studentManagementSystem.config.SecurityConfig;
import com.example.studentManagementSystem.dto.CourseDTO;
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
    void testTeacherCanListAndDeleteCourses() throws Exception {
        CourseDTO course = new CourseDTO(1L, "CSE101", "Intro to CS", "Basic CS", 3, 1L, "John Smith", 5);
        when(courseService.getAllCourses()).thenReturn(Arrays.asList(course));

        mockMvc.perform(get("/courses"))
                .andExpect(status().isOk())
                .andExpect(view().name("course/list"))
                .andExpect(model().attributeExists("courses"));

        doNothing().when(courseService).deleteCourse(1L);

        mockMvc.perform(post("/courses/delete/1").with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/courses"));
    }

    @Test
    @WithMockUser(username = "student1", roles = {"STUDENT"})
    void testStudentCannotCreateOrDeleteCourses() throws Exception {
        mockMvc.perform(get("/courses/new"))
                .andExpect(status().isForbidden());

        mockMvc.perform(post("/courses/create")
                        .with(csrf())
                        .param("code", "TEST101")
                        .param("name", "Test"))
                .andExpect(status().isForbidden());

        mockMvc.perform(post("/courses/delete/1").with(csrf()))
                .andExpect(status().isForbidden());
    }
}
