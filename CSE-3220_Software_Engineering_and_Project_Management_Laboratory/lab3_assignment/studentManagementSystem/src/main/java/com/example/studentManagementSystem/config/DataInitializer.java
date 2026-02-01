package com.example.studentManagementSystem.config;

import com.example.studentManagementSystem.entity.*;
import com.example.studentManagementSystem.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Data Initializer - Loads sample data on application startup
 * Creates default roles, users, departments, teachers, students, and courses
 */
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final DepartmentRepository departmentRepository;
    private final TeacherRepository teacherRepository;
    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // Only initialize if roles don't exist
        if (roleRepository.count() == 0) {
            initializeData();
        }
    }

    private void initializeData() {
        // Create Roles
        Role teacherRole = new Role("ROLE_TEACHER");
        Role studentRole = new Role("ROLE_STUDENT");
        roleRepository.save(teacherRole);
        roleRepository.save(studentRole);

        // Create Departments
        Department cse = new Department("Computer Science and Engineering", "CSE", 
                "Department of Computer Science and Engineering");
        Department eee = new Department("Electrical and Electronic Engineering", "EEE", 
                "Department of Electrical and Electronic Engineering");
        Department me = new Department("Mechanical Engineering", "ME", 
                "Department of Mechanical Engineering");
        departmentRepository.save(cse);
        departmentRepository.save(eee);
        departmentRepository.save(me);

        // Create Teacher Users
        User teacherUser1 = new User("teacher1", passwordEncoder.encode("teacher123"), 
                "john.smith@university.edu", teacherRole);
        User teacherUser2 = new User("teacher2", passwordEncoder.encode("teacher123"), 
                "sarah.johnson@university.edu", teacherRole);
        userRepository.save(teacherUser1);
        userRepository.save(teacherUser2);

        // Create Teachers
        Teacher teacher1 = new Teacher("John", "Smith", "john.smith@university.edu", 
                "123-456-7890", "PhD in Computer Science", "Professor");
        teacher1.setDepartment(cse);
        teacher1.setUser(teacherUser1);
        teacherRepository.save(teacher1);

        Teacher teacher2 = new Teacher("Sarah", "Johnson", "sarah.johnson@university.edu", 
                "123-456-7891", "PhD in Electrical Engineering", "Associate Professor");
        teacher2.setDepartment(eee);
        teacher2.setUser(teacherUser2);
        teacherRepository.save(teacher2);

        // Create Courses
        Course course1 = new Course("CSE101", "Introduction to Programming", 
                "Basic programming concepts using Java", 3);
        course1.setTeacher(teacher1);
        courseRepository.save(course1);

        Course course2 = new Course("CSE201", "Data Structures", 
                "Fundamental data structures and algorithms", 3);
        course2.setTeacher(teacher1);
        courseRepository.save(course2);

        Course course3 = new Course("CSE301", "Database Systems", 
                "Relational database design and SQL", 3);
        course3.setTeacher(teacher1);
        courseRepository.save(course3);

        Course course4 = new Course("EEE101", "Circuit Analysis", 
                "Basic electrical circuit analysis", 3);
        course4.setTeacher(teacher2);
        courseRepository.save(course4);

        // Create Student Users
        User studentUser1 = new User("student1", passwordEncoder.encode("student123"), 
                "alice.williams@student.edu", studentRole);
        User studentUser2 = new User("student2", passwordEncoder.encode("student123"), 
                "bob.brown@student.edu", studentRole);
        User studentUser3 = new User("student3", passwordEncoder.encode("student123"), 
                "charlie.davis@student.edu", studentRole);
        userRepository.save(studentUser1);
        userRepository.save(studentUser2);
        userRepository.save(studentUser3);

        // Create Students
        Student student1 = new Student("2024-1-60-001", "Alice", "Williams", 
                "alice.williams@student.edu", "555-0001", "123 Main St", 3);
        student1.setDepartment(cse);
        student1.setUser(studentUser1);
        student1.enrollInCourse(course1);
        student1.enrollInCourse(course2);
        studentRepository.save(student1);

        Student student2 = new Student("2024-1-60-002", "Bob", "Brown", 
                "bob.brown@student.edu", "555-0002", "456 Oak Ave", 5);
        student2.setDepartment(cse);
        student2.setUser(studentUser2);
        student2.enrollInCourse(course2);
        student2.enrollInCourse(course3);
        studentRepository.save(student2);

        Student student3 = new Student("2024-2-70-001", "Charlie", "Davis", 
                "charlie.davis@student.edu", "555-0003", "789 Pine Rd", 2);
        student3.setDepartment(eee);
        student3.setUser(studentUser3);
        student3.enrollInCourse(course4);
        studentRepository.save(student3);

        System.out.println("===========================================");
        System.out.println("Sample data initialized successfully!");
        System.out.println("===========================================");
        System.out.println("Teacher Accounts:");
        System.out.println("  Username: teacher1, Password: teacher123");
        System.out.println("  Username: teacher2, Password: teacher123");
        System.out.println("-------------------------------------------");
        System.out.println("Student Accounts:");
        System.out.println("  Username: student1, Password: student123");
        System.out.println("  Username: student2, Password: student123");
        System.out.println("  Username: student3, Password: student123");
        System.out.println("===========================================");
    }
}
