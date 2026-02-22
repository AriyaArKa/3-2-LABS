package com.example.studentManagementSystem.integration;

import com.example.studentManagementSystem.dto.CourseDTO;
import com.example.studentManagementSystem.dto.DepartmentDTO;
import com.example.studentManagementSystem.dto.StudentDTO;
import com.example.studentManagementSystem.dto.TeacherDTO;
import com.example.studentManagementSystem.entity.*;
import com.example.studentManagementSystem.repository.*;
import com.example.studentManagementSystem.service.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration Tests for Student Management System
 * Uses full Spring Boot context with H2 in-memory database.
 * DataInitializer seeds demo data on startup.
 */
@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class IntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private TeacherRepository teacherRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private StudentService studentService;

    @Autowired
    private CourseService courseService;

    @Autowired
    private DepartmentService departmentService;

    @Autowired
    private TeacherService teacherService;

    // ==================== Repository Integration Tests ====================

    /**
     * Test 1: Verify DataInitializer seeds roles, departments, teachers,
     * students, and courses into the H2 database on startup.
     */
    @Test
    void dataInitializer_shouldSeedDatabase() {
        assertEquals(2, roleRepository.count(), "Should have 2 roles");
        assertEquals(3, departmentRepository.count(), "Should have 3 departments");
        assertEquals(2, teacherRepository.count(), "Should have 2 teachers");
        assertEquals(3, studentRepository.count(), "Should have 3 students");
        assertEquals(4, courseRepository.count(), "Should have 4 courses");
        assertEquals(5, userRepository.count(), "Should have 5 users (2 teachers + 3 students)");
    }

    /**
     * Test 2: Verify student-course many-to-many relationship is persisted.
     */
    @Test
    @Transactional
    void studentCourseRelationship_shouldBePersisted() {
        Student alice = studentRepository.findByStudentId("2024-1-60-001").orElseThrow();
        Set<Course> courses = alice.getEnrolledCourses();

        assertEquals(2, courses.size(), "Alice should be enrolled in 2 courses");
        assertTrue(courses.stream().anyMatch(c -> c.getCode().equals("CSE101")));
        assertTrue(courses.stream().anyMatch(c -> c.getCode().equals("CSE201")));
    }

    /**
     * Test 3: Verify department-student one-to-many relationship via repository query.
     */
    @Test
    void departmentStudentRelationship_shouldWork() {
        Department cse = departmentRepository.findByCode("CSE").orElseThrow();
        List<Student> cseStudents = studentRepository.findByDepartmentId(cse.getId());

        assertEquals(2, cseStudents.size(), "CSE department should have 2 students");
    }

    /**
     * Test 4: Custom repository finder methods work correctly.
     */
    @Test
    void repositoryFinders_shouldReturnCorrectResults() {
        assertTrue(studentRepository.existsByEmail("alice.williams@student.edu"));
        assertFalse(studentRepository.existsByEmail("nonexistent@test.edu"));

        Optional<Course> course = courseRepository.findByCode("CSE301");
        assertTrue(course.isPresent());
        assertEquals("Database Systems", course.get().getName());

        Optional<User> user = userRepository.findByUsername("teacher1");
        assertTrue(user.isPresent());
        assertEquals("ROLE_TEACHER", user.get().getRole().getName());
    }

    // ==================== Service Integration Tests ====================

    /**
     * Test 5: DepartmentService creates and retrieves a department end-to-end.
     */
    @Test
    void departmentService_createAndRetrieve() {
        DepartmentDTO dto = new DepartmentDTO();
        dto.setName("Civil Engineering");
        dto.setCode("CE");
        dto.setDescription("Department of Civil Engineering");

        DepartmentDTO created = departmentService.createDepartment(dto);
        assertNotNull(created.getId());

        DepartmentDTO fetched = departmentService.getDepartmentById(created.getId());
        assertEquals("Civil Engineering", fetched.getName());
        assertEquals("CE", fetched.getCode());
    }

    /**
     * Test 6: CourseService creates a course assigned to an existing teacher.
     */
    @Test
    @Transactional
    void courseService_createCourseWithTeacher() {
        Teacher teacher = teacherRepository.findByEmail("john.smith@university.edu").orElseThrow();

        CourseDTO dto = new CourseDTO();
        dto.setCode("CSE401");
        dto.setName("Software Engineering");
        dto.setDescription("Software development lifecycle");
        dto.setCredits(3);
        dto.setTeacherId(teacher.getId());

        CourseDTO created = courseService.createCourse(dto);
        assertNotNull(created.getId());
        assertEquals("John Smith", created.getTeacherName());
        assertEquals(5, courseRepository.count());
    }

    /**
     * Test 7: StudentService enroll and drop course operations persist correctly.
     */
    @Test
    @Transactional
    void studentService_enrollAndDropCourse() {
        Student charlie = studentRepository.findByStudentId("2024-2-70-001").orElseThrow();
        Course cse101 = courseRepository.findByCode("CSE101").orElseThrow();

        // Charlie starts with 1 course (EEE101), enroll in CSE101
        studentService.enrollInCourse(charlie.getId(), cse101.getId());
        Set<Course> courses = studentService.getEnrolledCourses(charlie.getId());
        assertEquals(2, courses.size());

        // Drop CSE101
        studentService.dropCourse(charlie.getId(), cse101.getId());
        courses = studentService.getEnrolledCourses(charlie.getId());
        assertEquals(1, courses.size());
    }

    /**
     * Test 8: StudentService rejects duplicate student ID on creation.
     */
    @Test
    void studentService_shouldRejectDuplicateStudentId() {
        StudentDTO dto = new StudentDTO();
        dto.setStudentId("2024-1-60-001"); // Already exists (Alice)
        dto.setFirstName("Duplicate");
        dto.setLastName("Student");
        dto.setEmail("dup@test.edu");
        dto.setUsername("dupuser");
        dto.setPassword("pass123");

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> studentService.createStudent(dto));
        assertTrue(ex.getMessage().contains("Student ID already exists"));
    }

    /**
     * Test 9: TeacherService creates a teacher with a linked user account.
     */
    @Test
    void teacherService_createTeacherWithUserAccount() {
        Department dept = departmentRepository.findByCode("ME").orElseThrow();

        TeacherDTO dto = new TeacherDTO();
        dto.setFirstName("David");
        dto.setLastName("Lee");
        dto.setEmail("david.lee@university.edu");
        dto.setPhone("555-9999");
        dto.setQualification("PhD in Mechanical Engineering");
        dto.setDesignation("Lecturer");
        dto.setDepartmentId(dept.getId());
        dto.setUsername("teacher3");
        dto.setPassword("teacher123");

        TeacherDTO created = teacherService.createTeacher(dto);
        assertNotNull(created.getId());
        assertEquals("Mechanical Engineering", created.getDepartmentName());

        // Verify user was also created
        assertTrue(userRepository.existsByUsername("teacher3"));
    }

    /**
     * Test 10: DepartmentService delete cascades correctly.
     */
    @Test
    void departmentService_deleteRemovesDepartment() {
        DepartmentDTO dto = new DepartmentDTO();
        dto.setName("Temp Dept");
        dto.setCode("TEMP");
        dto.setDescription("Temporary");

        DepartmentDTO created = departmentService.createDepartment(dto);
        departmentService.deleteDepartment(created.getId());

        assertFalse(departmentRepository.existsByCode("TEMP"));
    }

    // ==================== Controller Integration Tests (MockMvc) ====================

    /**
     * Test 11: Unauthenticated user is redirected to login page.
     */
    @Test
    void unauthenticatedAccess_shouldRedirectToLogin() throws Exception {
        mockMvc.perform(get("/dashboard"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    /**
     * Test 12: Teacher can access the student list page.
     */
    @Test
    @WithMockUser(username = "teacher1", roles = {"TEACHER"})
    void teacher_canAccessStudentList() throws Exception {
        mockMvc.perform(get("/students"))
                .andExpect(status().isOk())
                .andExpect(view().name("student/list"))
                .andExpect(model().attributeExists("students"));
    }

    /**
     * Test 13: Student role cannot access create-student form (teacher-only).
     */
    @Test
    @WithMockUser(username = "student1", roles = {"STUDENT"})
    void student_cannotAccessCreateStudentForm() throws Exception {
        mockMvc.perform(get("/students/new"))
                .andExpect(status().isForbidden());
    }

    /**
     * Test 14: Teacher can create a new department via POST.
     */
    @Test
    @WithMockUser(username = "teacher1", roles = {"TEACHER"})
    void teacher_canCreateDepartment() throws Exception {
        mockMvc.perform(post("/departments/create")
                        .param("name", "Architecture")
                        .param("code", "ARCH")
                        .param("description", "Department of Architecture"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/departments"));

        assertTrue(departmentRepository.existsByCode("ARCH"));
    }

    /**
     * Test 15: Teacher can delete a course (with no enrolled students) via POST.
     */
    @Test
    @WithMockUser(username = "teacher1", roles = {"TEACHER"})
    void teacher_canDeleteCourse() throws Exception {
        // Create a course with no enrolled students so deletion succeeds
        Course tempCourse = new Course("TEMP100", "Temp Course", "Temporary", 1);
        tempCourse = courseRepository.save(tempCourse);

        mockMvc.perform(post("/courses/delete/" + tempCourse.getId()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/courses"));

        assertFalse(courseRepository.existsByCode("TEMP100"));
    }
}
