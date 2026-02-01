package com.example.studentManagementSystem.controller;

import com.example.studentManagementSystem.dto.TeacherDTO;
import com.example.studentManagementSystem.entity.Teacher;
import com.example.studentManagementSystem.entity.User;
import com.example.studentManagementSystem.service.CustomUserDetailsService;
import com.example.studentManagementSystem.service.DepartmentService;
import com.example.studentManagementSystem.service.TeacherService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Teacher Controller - Handles all teacher CRUD operations
 * Only accessible by teachers (ROLE_TEACHER)
 */
@Controller
@RequestMapping("/teachers")
@RequiredArgsConstructor
public class TeacherController {

    private final TeacherService teacherService;
    private final DepartmentService departmentService;
    private final CustomUserDetailsService userDetailsService;

    /**
     * List all teachers
     */
    @GetMapping
    public String listTeachers(Model model) {
        model.addAttribute("teachers", teacherService.getAllTeachers());
        return "teacher/list";
    }

    /**
     * Show create teacher form
     */
    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("teacher", new TeacherDTO());
        model.addAttribute("departments", departmentService.getAllDepartments());
        return "teacher/form";
    }

    /**
     * Show edit teacher form
     */
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        model.addAttribute("teacher", teacherService.getTeacherById(id));
        model.addAttribute("departments", departmentService.getAllDepartments());
        return "teacher/form";
    }

    /**
     * Create new teacher (POST only)
     */
    @PostMapping("/create")
    public String createTeacher(@ModelAttribute TeacherDTO teacherDTO,
                                RedirectAttributes redirectAttributes) {
        try {
            teacherService.createTeacher(teacherDTO);
            redirectAttributes.addFlashAttribute("successMessage", "Teacher created successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/teachers/new";
        }
        return "redirect:/teachers";
    }

    /**
     * Update existing teacher (POST only)
     */
    @PostMapping("/update/{id}")
    public String updateTeacher(@PathVariable Long id,
                                @ModelAttribute TeacherDTO teacherDTO,
                                RedirectAttributes redirectAttributes) {
        try {
            teacherService.updateTeacher(id, teacherDTO);
            redirectAttributes.addFlashAttribute("successMessage", "Teacher updated successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/teachers/edit/" + id;
        }
        return "redirect:/teachers";
    }

    /**
     * Delete teacher (POST only)
     */
    @PostMapping("/delete/{id}")
    public String deleteTeacher(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            teacherService.deleteTeacher(id);
            redirectAttributes.addFlashAttribute("successMessage", "Teacher deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/teachers";
    }

    /**
     * View teacher details
     */
    @GetMapping("/view/{id}")
    public String viewTeacher(@PathVariable Long id, Model model) {
        model.addAttribute("teacher", teacherService.getTeacherById(id));
        return "teacher/view";
    }

    /**
     * View own profile (for logged-in teacher)
     */
    @GetMapping("/profile")
    public String viewProfile(Authentication authentication, Model model) {
        User user = userDetailsService.getUserByUsername(authentication.getName());
        Teacher teacher = teacherService.getTeacherEntityByUserId(user.getId());
        model.addAttribute("teacher", teacher);
        model.addAttribute("departments", departmentService.getAllDepartments());
        return "teacher/profile";
    }

    /**
     * Update own profile (POST only)
     */
    @PostMapping("/profile/update")
    public String updateProfile(Authentication authentication,
                                @ModelAttribute TeacherDTO teacherDTO,
                                RedirectAttributes redirectAttributes) {
        try {
            User user = userDetailsService.getUserByUsername(authentication.getName());
            Teacher teacher = teacherService.getTeacherEntityByUserId(user.getId());
            teacherService.updateTeacher(teacher.getId(), teacherDTO);
            redirectAttributes.addFlashAttribute("successMessage", "Profile updated successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/teachers/profile";
    }
}
