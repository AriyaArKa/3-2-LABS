package com.example.studentManagementSystem.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

/**
 * Teacher Entity - Represents teachers in the system
 * Belongs to a Department (M:1) and has many Courses (1:M)
 */
@Entity
@Table(name = "teachers")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = {"courses", "department", "user"})
@ToString(exclude = {"courses", "department", "user"})
public class Teacher {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(nullable = false, unique = true)
    private String email;

    private String phone;

    private String qualification;

    private String designation; // e.g., Professor, Associate Professor, Lecturer

    // Many teachers belong to one department (M:1)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id")
    private Department department;

    // One teacher can teach many courses (1:M)
    @OneToMany(mappedBy = "teacher", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Course> courses = new ArrayList<>();

    // Link to user account for authentication
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    public Teacher(String firstName, String lastName, String email, String phone, 
                   String qualification, String designation) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
        this.qualification = qualification;
        this.designation = designation;
    }

    // Helper method to get full name
    public String getFullName() {
        return firstName + " " + lastName;
    }
}
