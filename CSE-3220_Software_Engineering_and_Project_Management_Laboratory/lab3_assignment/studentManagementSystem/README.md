# Student Management System

A comprehensive Student Management System built with Spring Boot, following MVC architecture, clean code practices, and beginner-friendly design.

## 📋 Table of Contents

- [Features](#-features)
- [Technology Stack](#-technology-stack)
- [Architecture](#-architecture)
- [Authentication vs Authorization](#-authentication-vs-authorization)
- [Project Structure](#-project-structure)
- [Database Schema](#-database-schema)
- [Getting Started](#-getting-started)
- [User Roles & Permissions](#-user-roles--permissions)
- [Demo Credentials](#-demo-credentials)
- [API Endpoints](#-api-endpoints)

## ✨ Features

### 🔐 Security Features

- Spring Security implementation for authentication and authorization
- Role-based access control (ROLE_STUDENT, ROLE_TEACHER)
- BCrypt password encoding
- Session management

### 👨‍🎓 Student Features

- View personal profile
- View enrolled courses
- Enroll/Drop courses
- View department information

### 👨‍🏫 Teacher Features

- Full CRUD operations on Students
- Full CRUD operations on Teachers
- Full CRUD operations on Courses
- Full CRUD operations on Departments
- Update own profile

## 🛠 Technology Stack

| Technology        | Purpose                        |
| ----------------- | ------------------------------ |
| Spring Boot 4.0.2 | Backend Framework              |
| Spring Security   | Authentication & Authorization |
| Spring Data JPA   | Data Persistence               |
| PostgreSQL        | Database                       |
| Thymeleaf         | Template Engine                |
| Lombok            | Boilerplate Code Reduction     |
| Docker Compose    | Container Management           |
| Maven             | Build Tool                     |

## 🏗 Architecture

This project follows the **MVC (Model-View-Controller)** architecture pattern:

```
┌─────────────────────────────────────────────────────────────┐
│                      PRESENTATION LAYER                      │
│                   (Thymeleaf Templates)                      │
└─────────────────────────────────────────────────────────────┘
                              ↓ ↑
┌─────────────────────────────────────────────────────────────┐
│                      CONTROLLER LAYER                        │
│  (HomeController, StudentController, TeacherController,     │
│   CourseController, DepartmentController)                    │
└─────────────────────────────────────────────────────────────┘
                              ↓ ↑
┌─────────────────────────────────────────────────────────────┐
│                       SERVICE LAYER                          │
│  (StudentService, TeacherService, CourseService,            │
│   DepartmentService, CustomUserDetailsService)               │
└─────────────────────────────────────────────────────────────┘
                              ↓ ↑
┌─────────────────────────────────────────────────────────────┐
│                      REPOSITORY LAYER                        │
│  (StudentRepository, TeacherRepository, CourseRepository,   │
│   DepartmentRepository, UserRepository, RoleRepository)      │
└─────────────────────────────────────────────────────────────┘
                              ↓ ↑
┌─────────────────────────────────────────────────────────────┐
│                       DATABASE LAYER                         │
│                      (PostgreSQL)                            │
└─────────────────────────────────────────────────────────────┘
```

## 🔐 Authentication vs Authorization

### Authentication: "Who are you?"

**Authentication** is the process of verifying the identity of a user, device, or system.

- User provides credentials (username/password)
- System validates these credentials against stored data
- If valid, the user is authenticated

**In this application:**

- Users login with username and password
- `CustomUserDetailsService` loads user from database
- Spring Security verifies password using BCrypt encoder

### Authorization: "What are you allowed to do?"

**Authorization** is the process of determining what actions an authenticated user is permitted to perform.

- Happens AFTER authentication
- Based on user's role or permissions

**In this application:**

- Users have roles: `ROLE_TEACHER` or `ROLE_STUDENT`
- Teachers can access all CRUD operations
- Students can only view their own profile and courses

### Comparison Table

| Aspect         | Authentication                | Authorization                |
| -------------- | ----------------------------- | ---------------------------- |
| Purpose        | Verify identity               | Grant/deny access            |
| Question       | Who are you?                  | What can you do?             |
| Order          | Happens first                 | Happens after authentication |
| Method         | Username/Password, Biometrics | Roles, Permissions, Policies |
| Failure Result | 401 Unauthorized              | 403 Forbidden                |

## 📁 Project Structure

```
src/main/java/com/example/studentManagementSystem/
├── config/
│   ├── SecurityConfig.java         # Spring Security configuration
│   └── DataInitializer.java        # Sample data loader
├── controller/
│   ├── HomeController.java         # Dashboard and login
│   ├── StudentController.java      # Student CRUD
│   ├── TeacherController.java      # Teacher CRUD
│   ├── CourseController.java       # Course CRUD
│   └── DepartmentController.java   # Department CRUD
├── dto/
│   ├── StudentDTO.java
│   ├── TeacherDTO.java
│   ├── CourseDTO.java
│   └── DepartmentDTO.java
├── entity/
│   ├── User.java                   # User entity for authentication
│   ├── Role.java                   # Role entity
│   ├── Student.java
│   ├── Teacher.java
│   ├── Course.java
│   └── Department.java
├── repository/
│   ├── UserRepository.java
│   ├── RoleRepository.java
│   ├── StudentRepository.java
│   ├── TeacherRepository.java
│   ├── CourseRepository.java
│   └── DepartmentRepository.java
├── service/
│   ├── CustomUserDetailsService.java
│   ├── StudentService.java
│   ├── TeacherService.java
│   ├── CourseService.java
│   └── DepartmentService.java
└── StudentManagementSystemApplication.java

src/main/resources/
├── templates/
│   ├── login.html
│   ├── access-denied.html
│   ├── about.html
│   ├── student/
│   │   ├── dashboard.html
│   │   ├── list.html
│   │   ├── form.html
│   │   ├── view.html
│   │   ├── profile.html
│   │   ├── my-courses.html
│   │   └── my-department.html
│   ├── teacher/
│   │   ├── dashboard.html
│   │   ├── list.html
│   │   ├── form.html
│   │   ├── view.html
│   │   └── profile.html
│   ├── course/
│   │   ├── list.html
│   │   ├── form.html
│   │   └── view.html
│   └── department/
│       ├── list.html
│       ├── form.html
│       └── view.html
├── static/css/
│   └── style.css
└── application.properties
```

## 🗄 Database Schema

### Entity Relationships

```
┌──────────────┐     1:M     ┌──────────────┐
│  Department  │────────────▶│   Student    │
└──────────────┘             └──────────────┘
       │                            │
       │ 1:M                        │ M:M
       ▼                            ▼
┌──────────────┐     1:M     ┌──────────────┐
│   Teacher    │────────────▶│    Course    │
└──────────────┘             └──────────────┘

┌──────────────┐     M:1     ┌──────────────┐
│     User     │────────────▶│     Role     │
└──────────────┘             └──────────────┘
```

### Tables

1. **users** - System users for authentication
2. **roles** - User roles (ROLE_STUDENT, ROLE_TEACHER)
3. **departments** - Academic departments
4. **teachers** - Teacher information
5. **students** - Student information
6. **courses** - Course information
7. **student_courses** - Many-to-many relationship table

## 🚀 Getting Started

### Prerequisites

- Java 21 or higher
- Maven 3.6+
- Docker & Docker Compose (for PostgreSQL)
- PostgreSQL (if not using Docker)

### Option 1: Using Docker Compose (Recommended)

1. **Start PostgreSQL container:**

   ```bash
   docker-compose up -d
   ```

2. **Run the application:**

   ```bash
   ./mvnw spring-boot:run
   ```

3. **Access the application:**
   Open http://localhost:8080 in your browser

### Option 2: Local PostgreSQL

1. **Create database:**

   ```sql
   CREATE DATABASE student_management;
   CREATE USER arka WITH PASSWORD 'arka';
   GRANT ALL PRIVILEGES ON DATABASE student_management TO arka;
   ```

2. **Run the application:**
   ```bash
   ./mvnw spring-boot:run
   ```

## 👥 User Roles & Permissions

### ROLE_STUDENT

| Permission             | Allowed |
| ---------------------- | ------- |
| View own profile       | ✅      |
| View department        | ✅      |
| View enrolled courses  | ✅      |
| Enroll/Drop courses    | ✅      |
| Modify teacher info    | ❌      |
| Access admin endpoints | ❌      |
| CRUD operations        | ❌      |

### ROLE_TEACHER

| Permission                | Allowed |
| ------------------------- | ------- |
| Manage students (CRUD)    | ✅      |
| Manage teachers (CRUD)    | ✅      |
| Manage courses (CRUD)     | ✅      |
| Manage departments (CRUD) | ✅      |
| Update own profile        | ✅      |
| Access all endpoints      | ✅      |

## 🔑 Demo Credentials

| Role    | Username | Password   |
| ------- | -------- | ---------- |
| Teacher | teacher1 | teacher123 |
| Teacher | teacher2 | teacher123 |
| Student | student1 | student123 |
| Student | student2 | student123 |
| Student | student3 | student123 |

## 🌐 API Endpoints

All CRUD operations use **POST** requests as specified.

### Public Endpoints

| Method | URL      | Description                |
| ------ | -------- | -------------------------- |
| GET    | `/`      | Home page (redirects)      |
| GET    | `/login` | Login page                 |
| GET    | `/about` | About page (Auth vs Authz) |

### Student Endpoints (Student Role)

| Method | URL                       | Description           |
| ------ | ------------------------- | --------------------- |
| GET    | `/students/profile`       | View own profile      |
| GET    | `/students/my-courses`    | View enrolled courses |
| GET    | `/students/my-department` | View department info  |
| POST   | `/students/enroll/{id}`   | Enroll in course      |
| POST   | `/students/drop/{id}`     | Drop course           |

### Admin Endpoints (Teacher Role)

#### Students

| Method | URL                     | Description       |
| ------ | ----------------------- | ----------------- |
| GET    | `/students`             | List all students |
| GET    | `/students/new`         | Create form       |
| GET    | `/students/edit/{id}`   | Edit form         |
| GET    | `/students/view/{id}`   | View details      |
| POST   | `/students/create`      | Create student    |
| POST   | `/students/update/{id}` | Update student    |
| POST   | `/students/delete/{id}` | Delete student    |

#### Teachers

| Method | URL                        | Description        |
| ------ | -------------------------- | ------------------ |
| GET    | `/teachers`                | List all teachers  |
| GET    | `/teachers/new`            | Create form        |
| GET    | `/teachers/edit/{id}`      | Edit form          |
| GET    | `/teachers/view/{id}`      | View details       |
| GET    | `/teachers/profile`        | Own profile        |
| POST   | `/teachers/create`         | Create teacher     |
| POST   | `/teachers/update/{id}`    | Update teacher     |
| POST   | `/teachers/delete/{id}`    | Delete teacher     |
| POST   | `/teachers/profile/update` | Update own profile |

#### Courses

| Method | URL                    | Description      |
| ------ | ---------------------- | ---------------- |
| GET    | `/courses`             | List all courses |
| GET    | `/courses/new`         | Create form      |
| GET    | `/courses/edit/{id}`   | Edit form        |
| GET    | `/courses/view/{id}`   | View details     |
| POST   | `/courses/create`      | Create course    |
| POST   | `/courses/update/{id}` | Update course    |
| POST   | `/courses/delete/{id}` | Delete course    |

#### Departments

| Method | URL                        | Description          |
| ------ | -------------------------- | -------------------- |
| GET    | `/departments`             | List all departments |
| GET    | `/departments/new`         | Create form          |
| GET    | `/departments/edit/{id}`   | Edit form            |
| GET    | `/departments/view/{id}`   | View details         |
| POST   | `/departments/create`      | Create department    |
| POST   | `/departments/update/{id}` | Update department    |
| POST   | `/departments/delete/{id}` | Delete department    |

---

## 📝 License

This project is created for educational purposes as part of a college-level Spring Boot project.

## 👨‍💻 Author

Created with ❤️ using Spring Boot
