package com.example.studentManagementSystem.controller;

import com.example.studentManagementSystem.entity.Student;
import com.example.studentManagementSystem.entity.Teacher;
import com.example.studentManagementSystem.entity.User;
import com.example.studentManagementSystem.service.CustomUserDetailsService;
import com.example.studentManagementSystem.service.StudentService;
import com.example.studentManagementSystem.service.TeacherService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Home Controller - Handles main navigation and dashboard
 */
@Controller
@RequiredArgsConstructor
public class HomeController {

    private final CustomUserDetailsService userDetailsService;
    private final StudentService studentService;
    private final TeacherService teacherService;

    /**
     * Landing page - redirects to login or dashboard
     */
    @GetMapping("/")
    public String home(Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            return "redirect:/dashboard";
        }
        return "redirect:/login";
    }

    /**
     * Dashboard - shows appropriate view based on user role
     */
    @GetMapping("/dashboard")
    public String dashboard(Authentication authentication, Model model) {
        String username = authentication.getName();
        User user = userDetailsService.getUserByUsername(username);
        String role = user.getRole().getName();
        
        model.addAttribute("username", username);
        model.addAttribute("role", role);
        
        if (role.equals("ROLE_STUDENT")) {
            Student student = studentService.getStudentEntityByUserId(user.getId());
            model.addAttribute("student", student);
            return "student/dashboard";
        } else if (role.equals("ROLE_TEACHER")) {
            Teacher teacher = teacherService.getTeacherEntityByUserId(user.getId());
            model.addAttribute("teacher", teacher);
            return "teacher/dashboard";
        }
        
        return "redirect:/login";
    }

    /**
     * Login page
     */
    @GetMapping("/login")
    public String login() {
        return "login";
    }

    /**
     * Access denied page
     */
    @GetMapping("/access-denied")
    public String accessDenied() {
        return "access-denied";
    }

    /**
     * About page - explains Authentication vs Authorization
     */
    @GetMapping("/about")
    public String about() {
        return "about";
    }
}
