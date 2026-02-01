package com.example.studentManagementSystem.controller;

import com.example.studentManagementSystem.dto.DepartmentDTO;
import com.example.studentManagementSystem.service.DepartmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Department Controller - Handles all department CRUD operations
 * Only accessible by teachers (ROLE_TEACHER)
 */
@Controller
@RequestMapping("/departments")
@RequiredArgsConstructor
public class DepartmentController {

    private final DepartmentService departmentService;

    /**
     * List all departments
     */
    @GetMapping
    public String listDepartments(Model model) {
        model.addAttribute("departments", departmentService.getAllDepartments());
        return "department/list";
    }

    /**
     * Show create department form
     */
    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("department", new DepartmentDTO());
        return "department/form";
    }

    /**
     * Show edit department form
     */
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        model.addAttribute("department", departmentService.getDepartmentById(id));
        return "department/form";
    }

    /**
     * Create new department (POST only)
     */
    @PostMapping("/create")
    public String createDepartment(@ModelAttribute DepartmentDTO departmentDTO,
                                   RedirectAttributes redirectAttributes) {
        try {
            departmentService.createDepartment(departmentDTO);
            redirectAttributes.addFlashAttribute("successMessage", "Department created successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/departments/new";
        }
        return "redirect:/departments";
    }

    /**
     * Update existing department (POST only)
     */
    @PostMapping("/update/{id}")
    public String updateDepartment(@PathVariable Long id,
                                   @ModelAttribute DepartmentDTO departmentDTO,
                                   RedirectAttributes redirectAttributes) {
        try {
            departmentService.updateDepartment(id, departmentDTO);
            redirectAttributes.addFlashAttribute("successMessage", "Department updated successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/departments/edit/" + id;
        }
        return "redirect:/departments";
    }

    /**
     * Delete department (POST only)
     */
    @PostMapping("/delete/{id}")
    public String deleteDepartment(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            departmentService.deleteDepartment(id);
            redirectAttributes.addFlashAttribute("successMessage", "Department deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/departments";
    }

    /**
     * View department details
     */
    @GetMapping("/view/{id}")
    public String viewDepartment(@PathVariable Long id, Model model) {
        model.addAttribute("department", departmentService.getDepartmentById(id));
        return "department/view";
    }
}
