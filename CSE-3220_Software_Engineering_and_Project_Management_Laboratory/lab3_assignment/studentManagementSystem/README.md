# Student Management System

A Spring Boot web application with **Spring Security** for role-based authentication & authorization. Teachers can create/edit/delete students, courses, departments, and other teachers. Students can only view data and manage their own course enrollments.

## Tech Stack

- **Java 21**, **Spring Boot 3.2.5**
- **Spring Security** - BCrypt password encoding, form-based login, role-based access
- **Spring Data JPA** - Hibernate ORM
- **H2 Database** - file-based (dev) / in-memory (test)
- **PostgreSQL** - for Docker production
- **Thymeleaf** - server-side HTML templates
- **Lombok** - boilerplate reduction
- **JUnit 5 + Mockito** - unit and integration testing
- **Docker & Docker Compose** - containerization
- **GitHub Actions** - CI pipeline

---

## Authentication & Authorization

The application uses **Spring Security** with role-based access control.

| Feature             | ROLE_TEACHER | ROLE_STUDENT |
| ------------------- | :----------: | :----------: |
| View students       |     Yes      |     Yes      |
| View courses        |     Yes      |     Yes      |
| View departments    |     Yes      |     Yes      |
| View teachers       |     Yes      |     Yes      |
| Create/Edit/Delete  |     Yes      |      No      |
| Enroll/Drop courses |      No      |     Yes      |
| View own profile    |     Yes      |     Yes      |
| Access dashboard    |     Yes      |     Yes      |

Login page: `/login`

---

## Running Locally

### Prerequisites

- Java 21
- Maven (or use the included Maven wrapper `mvnw`)

### Steps

1. Clone the repository:

```bash
git clone <repository-url>
cd studentManagementSystem
```

2. Run the application:

```bash
./mvnw spring-boot:run
```

On Windows:

```cmd
mvnw.cmd spring-boot:run
```

3. Open your browser at [http://localhost:8080](http://localhost:8080)

The app uses an **H2 file-based database** by default (`./data/student_management`). The H2 console is available at [http://localhost:8080/h2-console](http://localhost:8080/h2-console).

---

## Running Tests

Tests use an **H2 in-memory database** (`jdbc:h2:mem:testdb`), configured in `src/test/resources/application.properties`.

### Run all tests:

```bash
./mvnw clean test
```

On Windows:

```cmd
mvnw.cmd clean test
```

### Test Structure

| Layer      | Type             | Annotation     | Description                             |
| ---------- | ---------------- | -------------- | --------------------------------------- |
| Entity     | Unit test        | (plain JUnit)  | Constructors, getters, setters, methods |
| Repository | Integration test | `@DataJpaTest` | CRUD operations with H2 in-memory DB    |
| Service    | Unit test        | `@MockBean`    | Business logic with mocked repositories |
| Controller | Integration test | `@WebMvcTest`  | HTTP endpoints with Spring Security     |

### Test Files

```
src/test/java/com/example/studentManagementSystem/
  entity/
    RoleTest.java
    UserTest.java
    StudentTest.java
    TeacherTest.java
    CourseTest.java
    DepartmentTest.java
  repository/
    RoleRepositoryTest.java
    UserRepositoryTest.java
    StudentRepositoryTest.java
    CourseRepositoryTest.java
    DepartmentRepositoryTest.java
  service/
    DepartmentServiceTest.java
    CourseServiceTest.java
    StudentServiceTest.java
    TeacherServiceTest.java
  controller/
    HomeControllerTest.java
    DepartmentControllerTest.java
    CourseControllerTest.java
```

---

## Running with Docker

### Prerequisites

- Docker and Docker Compose

### Steps

1. Build and start:

```bash
docker compose up --build
```

2. Open your browser at [http://localhost:8080](http://localhost:8080)

3. Stop:

```bash
docker compose down
```

The Docker setup uses **PostgreSQL 16** as the database. The compose file starts both the application and the database containers.

---

## GitHub Actions CI

A CI pipeline is configured in `.github/workflows/ci.yml` at the repository root. It runs automatically on:

- **Push** to the `main` branch (when project files change)
- **Pull requests** to the `main` branch (when project files change)

### What the pipeline does:

1. Checks out the code
2. Sets up Java 21 (Temurin) with Maven caching
3. Compiles the project
4. Runs all tests (using H2 in-memory database)
5. Packages the application

---

## Project Structure

```
studentManagementSystem/
  src/
    main/
      java/com/example/studentManagementSystem/
        StudentManagementSystemApplication.java
        config/
          SecurityConfig.java
          DataInitializer.java
        controller/
          HomeController.java
          StudentController.java
          TeacherController.java
          CourseController.java
          DepartmentController.java
        dto/
          StudentDTO.java
          TeacherDTO.java
          CourseDTO.java
          DepartmentDTO.java
        entity/
          User.java
          Role.java
          Student.java
          Teacher.java
          Course.java
          Department.java
        repository/
          UserRepository.java
          RoleRepository.java
          StudentRepository.java
          TeacherRepository.java
          CourseRepository.java
          DepartmentRepository.java
        service/
          CustomUserDetailsService.java
          StudentService.java
          TeacherService.java
          CourseService.java
          DepartmentService.java
      resources/
        application.properties
        templates/       (Thymeleaf HTML templates)
        static/css/      (Stylesheets)
    test/
      java/com/example/studentManagementSystem/
        entity/          (Unit tests)
        repository/      (Integration tests)
        service/         (Unit tests)
        controller/      (Integration tests)
      resources/
        application.properties  (H2 in-memory config)
  Dockerfile
  compose.yaml
  pom.xml
```
