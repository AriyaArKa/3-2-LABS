package com.example.studentManagementSystem.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for Course data transfer
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CourseDTO {
    
    private Long id;
    private String code;
    private String name;
    private String description;
    private int credits;
    private Long teacherId;
    private String teacherName;
    private int enrolledStudentCount;
}
