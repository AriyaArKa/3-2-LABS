package com.example.studentManagementSystem.controller;

import com.example.studentManagementSystem.dto.CourseDTO;
import com.example.studentManagementSystem.service.CourseService;
import com.example.studentManagementSystem.service.TeacherService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Course Controller - Handles all course CRUD operations
 * Only accessible by teachers (ROLE_TEACHER)
 */
@Controller
@RequestMapping("/courses")
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;
    private final TeacherService teacherService;

    /**
     * List all courses
     */
    @GetMapping
    public String listCourses(Model model) {
        model.addAttribute("courses", courseService.getAllCourses());
        return "course/list";
    }

    /**
     * Show create course form
     */
    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("course", new CourseDTO());
        model.addAttribute("teachers", teacherService.getAllTeachers());
        return "course/form";
    }

    /**
     * Show edit course form
     */
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        model.addAttribute("course", courseService.getCourseById(id));
        model.addAttribute("teachers", teacherService.getAllTeachers());
        return "course/form";
    }

    /**
     * Create new course (POST only)
     */
    @PostMapping("/create")
    public String createCourse(@ModelAttribute CourseDTO courseDTO,
                               RedirectAttributes redirectAttributes) {
        try {
            courseService.createCourse(courseDTO);
            redirectAttributes.addFlashAttribute("successMessage", "Course created successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/courses/new";
        }
        return "redirect:/courses";
    }

    /**
     * Update existing course (POST only)
     */
    @PostMapping("/update/{id}")
    public String updateCourse(@PathVariable Long id,
                               @ModelAttribute CourseDTO courseDTO,
                               RedirectAttributes redirectAttributes) {
        try {
            courseService.updateCourse(id, courseDTO);
            redirectAttributes.addFlashAttribute("successMessage", "Course updated successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/courses/edit/" + id;
        }
        return "redirect:/courses";
    }

    /**
     * Delete course (POST only)
     */
    @PostMapping("/delete/{id}")
    public String deleteCourse(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            courseService.deleteCourse(id);
            redirectAttributes.addFlashAttribute("successMessage", "Course deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/courses";
    }

    /**
     * View course details
     */
    @GetMapping("/view/{id}")
    public String viewCourse(@PathVariable Long id, Model model) {
        model.addAttribute("course", courseService.getCourseById(id));
        return "course/view";
    }
}
