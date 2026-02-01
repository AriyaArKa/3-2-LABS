package com.example.studentManagementSystem.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for Department data transfer
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DepartmentDTO {
    
    private Long id;
    private String name;
    private String code;
    private String description;
    private int studentCount;
    private int teacherCount;
}
