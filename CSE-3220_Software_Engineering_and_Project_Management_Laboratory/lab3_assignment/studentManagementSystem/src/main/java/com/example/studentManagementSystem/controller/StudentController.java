package com.example.studentManagementSystem.controller;

import com.example.studentManagementSystem.dto.StudentDTO;
import com.example.studentManagementSystem.entity.Course;
import com.example.studentManagementSystem.entity.Student;
import com.example.studentManagementSystem.entity.User;
import com.example.studentManagementSystem.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Set;

/**
 * Student Controller - Handles all student CRUD operations
 * Teachers can manage all students
 * Students can only view their own profile
 */
@Controller
@RequestMapping("/students")
@RequiredArgsConstructor
public class StudentController {

    private final StudentService studentService;
    private final DepartmentService departmentService;
    private final CourseService courseService;
    private final CustomUserDetailsService userDetailsService;

    /**
     * List all students (Teacher only)
     */
    @GetMapping
    public String listStudents(Model model) {
        model.addAttribute("students", studentService.getAllStudents());
        return "student/list";
    }

    /**
     * Show create student form (Teacher only)
     */
    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("student", new StudentDTO());
        model.addAttribute("departments", departmentService.getAllDepartments());
        return "student/form";
    }

    /**
     * Show edit student form (Teacher only)
     */
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        model.addAttribute("student", studentService.getStudentById(id));
        model.addAttribute("departments", departmentService.getAllDepartments());
        return "student/form";
    }

    /**
     * Create new student (POST only, Teacher only)
     */
    @PostMapping("/create")
    public String createStudent(@ModelAttribute StudentDTO studentDTO,
                                RedirectAttributes redirectAttributes) {
        try {
            studentService.createStudent(studentDTO);
            redirectAttributes.addFlashAttribute("successMessage", "Student created successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/students/new";
        }
        return "redirect:/students";
    }

    /**
     * Update existing student (POST only, Teacher only)
     */
    @PostMapping("/update/{id}")
    public String updateStudent(@PathVariable Long id,
                                @ModelAttribute StudentDTO studentDTO,
                                RedirectAttributes redirectAttributes) {
        try {
            studentService.updateStudent(id, studentDTO);
            redirectAttributes.addFlashAttribute("successMessage", "Student updated successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/students/edit/" + id;
        }
        return "redirect:/students";
    }

    /**
     * Delete student (POST only, Teacher only)
     */
    @PostMapping("/delete/{id}")
    public String deleteStudent(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            studentService.deleteStudent(id);
            redirectAttributes.addFlashAttribute("successMessage", "Student deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/students";
    }

    /**
     * View student details (Teacher only)
     */
    @GetMapping("/view/{id}")
    public String viewStudent(@PathVariable Long id, Model model) {
        model.addAttribute("student", studentService.getStudentById(id));
        return "student/view";
    }

    /**
     * View own profile (for logged-in student)
     */
    @GetMapping("/profile")
    public String viewProfile(Authentication authentication, Model model) {
        User user = userDetailsService.getUserByUsername(authentication.getName());
        Student student = studentService.getStudentEntityByUserId(user.getId());
        Set<Course> enrolledCourses = studentService.getEnrolledCourses(student.getId());
        
        model.addAttribute("student", student);
        model.addAttribute("enrolledCourses", enrolledCourses);
        return "student/profile";
    }

    /**
     * View enrolled courses (for logged-in student)
     */
    @GetMapping("/my-courses")
    public String viewMyCourses(Authentication authentication, Model model) {
        User user = userDetailsService.getUserByUsername(authentication.getName());
        Student student = studentService.getStudentEntityByUserId(user.getId());
        Set<Course> enrolledCourses = studentService.getEnrolledCourses(student.getId());
        
        model.addAttribute("courses", enrolledCourses);
        model.addAttribute("allCourses", courseService.getAllCourses());
        model.addAttribute("studentId", student.getId());
        return "student/my-courses";
    }

    /**
     * Enroll in a course (POST only)
     */
    @PostMapping("/enroll/{courseId}")
    public String enrollInCourse(@PathVariable Long courseId,
                                 Authentication authentication,
                                 RedirectAttributes redirectAttributes) {
        try {
            User user = userDetailsService.getUserByUsername(authentication.getName());
            Student student = studentService.getStudentEntityByUserId(user.getId());
            studentService.enrollInCourse(student.getId(), courseId);
            redirectAttributes.addFlashAttribute("successMessage", "Enrolled in course successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/students/my-courses";
    }

    /**
     * Drop a course (POST only)
     */
    @PostMapping("/drop/{courseId}")
    public String dropCourse(@PathVariable Long courseId,
                             Authentication authentication,
                             RedirectAttributes redirectAttributes) {
        try {
            User user = userDetailsService.getUserByUsername(authentication.getName());
            Student student = studentService.getStudentEntityByUserId(user.getId());
            studentService.dropCourse(student.getId(), courseId);
            redirectAttributes.addFlashAttribute("successMessage", "Dropped course successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/students/my-courses";
    }

    /**
     * View department info (for logged-in student)
     */
    @GetMapping("/my-department")
    public String viewMyDepartment(Authentication authentication, Model model) {
        User user = userDetailsService.getUserByUsername(authentication.getName());
        Student student = studentService.getStudentEntityByUserId(user.getId());
        
        if (student.getDepartment() != null) {
            model.addAttribute("department", departmentService.getDepartmentById(student.getDepartment().getId()));
        }
        return "student/my-department";
    }
}
