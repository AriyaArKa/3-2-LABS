package com.example.studentManagementSystem.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for Teacher data transfer
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TeacherDTO {
    
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String qualification;
    private String designation;
    private Long departmentId;
    private String departmentName;
    
    // For registration
    private String username;
    private String password;
}
