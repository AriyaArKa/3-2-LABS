package com.example.studentManagementSystem.controller;

import com.example.studentManagementSystem.config.SecurityConfig;
import com.example.studentManagementSystem.dto.DepartmentDTO;
import com.example.studentManagementSystem.service.CustomUserDetailsService;
import com.example.studentManagementSystem.service.DepartmentService;
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
 * Integration tests for DepartmentController with Spring Security
 */
@WebMvcTest(DepartmentController.class)
@Import(SecurityConfig.class)
class DepartmentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DepartmentService departmentService;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    @Test
    void testListDepartmentsUnauthenticated() throws Exception {
        mockMvc.perform(get("/departments"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    @WithMockUser(username = "teacher1", roles = {"TEACHER"})
    void testTeacherCanAccessAndCreateDepartment() throws Exception {
        DepartmentDTO dept = new DepartmentDTO(1L, "CSE", "CS", "CS Dept", 10, 3);
        when(departmentService.getAllDepartments()).thenReturn(Arrays.asList(dept));

        mockMvc.perform(get("/departments"))
                .andExpect(status().isOk())
                .andExpect(view().name("department/list"));

        mockMvc.perform(get("/departments/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("department/form"));

        DepartmentDTO saved = new DepartmentDTO(1L, "ME", "ME", "ME Dept", 0, 0);
        when(departmentService.createDepartment(any())).thenReturn(saved);

        mockMvc.perform(post("/departments/create")
                        .with(csrf())
                        .param("name", "Mechanical Engineering")
                        .param("code", "ME")
                        .param("description", "ME Department"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/departments"));
    }

    @Test
    @WithMockUser(username = "student1", roles = {"STUDENT"})
    void testStudentCannotCreateDepartment() throws Exception {
        mockMvc.perform(get("/departments/new"))
                .andExpect(status().isForbidden());

        mockMvc.perform(post("/departments/create")
                        .with(csrf())
                        .param("name", "Test")
                        .param("code", "TEST"))
                .andExpect(status().isForbidden());
    }
}
