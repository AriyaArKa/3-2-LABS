package com.example.studentManagementSystem.controller;

import com.example.studentManagementSystem.config.SecurityConfig;
import com.example.studentManagementSystem.entity.*;
import com.example.studentManagementSystem.service.CustomUserDetailsService;
import com.example.studentManagementSystem.service.StudentService;
import com.example.studentManagementSystem.service.TeacherService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for HomeController with Spring Security
 */
@WebMvcTest(HomeController.class)
@Import(SecurityConfig.class)
class HomeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    @MockBean
    private StudentService studentService;

    @MockBean
    private TeacherService teacherService;

    @Test
    void testHomeRedirectsToLogin() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }

    @Test
    void testLoginPage() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("login"));
    }

    @Test
    void testAboutPage() throws Exception {
        mockMvc.perform(get("/about"))
                .andExpect(status().isOk())
                .andExpect(view().name("about"));
    }

    @Test
    @WithMockUser(username = "teacher1", roles = {"TEACHER"})
    void testDashboardAsTeacher() throws Exception {
        Role role = new Role(1L, "ROLE_TEACHER");
        User user = new User("teacher1", "pass", "t@test.com", role);
        user.setId(1L);
        Teacher teacher = new Teacher("John", "Smith", "t@test.com", "555", "PhD", "Prof");
        teacher.setId(1L);

        when(customUserDetailsService.getUserByUsername("teacher1")).thenReturn(user);
        when(teacherService.getTeacherEntityByUserId(1L)).thenReturn(teacher);

        mockMvc.perform(get("/dashboard"))
                .andExpect(status().isOk())
                .andExpect(view().name("teacher/dashboard"))
                .andExpect(model().attributeExists("teacher"));
    }

    @Test
    @WithMockUser(username = "student1", roles = {"STUDENT"})
    void testDashboardAsStudent() throws Exception {
        Role role = new Role(2L, "ROLE_STUDENT");
        User user = new User("student1", "pass", "s@test.com", role);
        user.setId(2L);
        Student student = new Student("2024-001", "Alice", "W", "s@test.com", "555", "Addr", 3);
        student.setId(1L);

        when(customUserDetailsService.getUserByUsername("student1")).thenReturn(user);
        when(studentService.getStudentEntityByUserId(2L)).thenReturn(student);

        mockMvc.perform(get("/dashboard"))
                .andExpect(status().isOk())
                .andExpect(view().name("student/dashboard"))
                .andExpect(model().attributeExists("student"));
    }

    @Test
    void testDashboardUnauthenticatedRedirects() throws Exception {
        mockMvc.perform(get("/dashboard"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    @WithMockUser(username = "teacher1", roles = {"TEACHER"})
    void testAccessDeniedPage() throws Exception {
        mockMvc.perform(get("/access-denied"))
                .andExpect(status().isOk())
                .andExpect(view().name("access-denied"));
    }
}
