package com.example.studentManagementSystem.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.HashSet;
import java.util.Set;

/**
 * Student Entity - Represents students in the system
 * Belongs to a Department (M:1) and enrolled in many Courses (M:M)
 */
@Entity
@Table(name = "students")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = {"enrolledCourses", "department", "user"})
@ToString(exclude = {"enrolledCourses", "department", "user"})
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String studentId; // e.g., 2021-1-60-001

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(nullable = false, unique = true)
    private String email;

    private String phone;

    private String address;

    private int semester;

    // Many students belong to one department (M:1)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id")
    private Department department;

    // Many students can enroll in many courses (M:M)
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "student_courses",
        joinColumns = @JoinColumn(name = "student_id"),
        inverseJoinColumns = @JoinColumn(name = "course_id")
    )
    private Set<Course> enrolledCourses = new HashSet<>();

    // Link to user account for authentication
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    public Student(String studentId, String firstName, String lastName, String email, 
                   String phone, String address, int semester) {
        this.studentId = studentId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.semester = semester;
    }

    // Helper method to get full name
    public String getFullName() {
        return firstName + " " + lastName;
    }

    // Helper method to enroll in a course
    public void enrollInCourse(Course course) {
        this.enrolledCourses.add(course);
        course.getEnrolledStudents().add(this);
    }

    // Helper method to drop a course
    public void dropCourse(Course course) {
        this.enrolledCourses.remove(course);
        course.getEnrolledStudents().remove(this);
    }
}
