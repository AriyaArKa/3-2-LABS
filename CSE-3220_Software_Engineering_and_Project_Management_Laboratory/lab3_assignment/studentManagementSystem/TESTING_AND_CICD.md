# 🧪 Testing & CI/CD — Complete Explanation
### Student Management System · Spring Boot 3.2.5 · Java 21

> This document explains **every test file**, **how testing works**, and **how GitHub Actions CI/CD would automate it** — written in simple, easy-to-understand language.

---

## 📁 Project Structure Overview

```
src/
├── main/java/com/example/studentManagementSystem/
│   ├── config/          → SecurityConfig (login rules, roles)
│   ├── controller/      → HTTP endpoints (URLs like /courses, /students)
│   ├── dto/             → Data Transfer Objects (CourseDTO, StudentDTO …)
│   ├── entity/          → JPA database models (Course, Student, Teacher …)
│   ├── repository/      → Database query interfaces
│   └── service/         → Business logic (rules for creating/deleting data)
│
└── test/java/com/example/studentManagementSystem/
    ├── entity/          → Tests for plain Java classes
    ├── repository/      → Tests for database queries (using H2 in-memory DB)
    ├── service/         → Tests for business logic (using Mockito fake DB)
    ├── controller/      → Tests for HTTP routes + Spring Security
    └── StudentManagementSystemApplicationTests.java   → App startup test
```

---

## 🏗️ The Testing Pyramid — 4 Layers

```
           ┌─────────────────────────┐
           │   Controller Tests (3)  │  ← HTTP + Security rules
           ├─────────────────────────┤
           │    Service Tests (4)    │  ← Business logic (Mockito)
           ├─────────────────────────┤
           │  Repository Tests (5)   │  ← Database queries (H2)
           ├─────────────────────────┤
           │    Entity Tests (6)     │  ← Pure Java classes
           └─────────────────────────┘
```

The **bottom** layers are simple and fast.
The **top** layers are more complex and test multiple things together.

---

## 🧱 LAYER 1 — Entity Tests (Pure Java, No Spring, No Database)

These tests check that your **data model classes** work correctly. No database, no Spring framework — just plain Java objects.

### 📄 `entity/RoleTest.java`

Tests the `Role` class (represents ROLE_STUDENT or ROLE_TEACHER).

```java
@Test
void testNoArgsConstructor() {
    Role role = new Role();
    assertNull(role.getId());       // id should be null when just created
    assertNull(role.getName());     // name should be null
}

@Test
void testParameterizedConstructor() {
    Role role = new Role("ROLE_STUDENT");
    assertEquals("ROLE_STUDENT", role.getName());  // name set correctly?
}

@Test
void testAllArgsConstructor() {
    Role role = new Role(1L, "ROLE_TEACHER");
    assertEquals(1L, role.getId());
    assertEquals("ROLE_TEACHER", role.getName());
}

@Test
void testEqualsAndHashCode() {
    Role role1 = new Role(1L, "ROLE_STUDENT");
    Role role2 = new Role(1L, "ROLE_STUDENT");
    assertEquals(role1, role2);                      // same data = equal objects?
    assertEquals(role1.hashCode(), role2.hashCode()); // same hash too?
}

@Test
void testNotEquals() {
    Role role1 = new Role(1L, "ROLE_STUDENT");
    Role role2 = new Role(2L, "ROLE_TEACHER");
    assertNotEquals(role1, role2);  // different id/name = not equal
}
```

**Tests:** 6 tests | **What is verified:** constructors, getters/setters, equals, hashCode

---

### 📄 `entity/UserTest.java`

Tests the `User` class — the login account attached to every teacher/student.

```java
@Test
void testDefaultEnabled() {
    User user = new User();
    assertTrue(user.isEnabled());  // new users are enabled by default
}

@Test
void testParameterizedConstructor() {
    Role role = new Role(1L, "ROLE_STUDENT");
    User user = new User("alice", "pass123", "alice@test.com", role);

    assertEquals("alice", user.getUsername());
    assertEquals("pass123", user.getPassword());
    assertEquals("alice@test.com", user.getEmail());
    assertEquals(role, user.getRole());
    assertTrue(user.isEnabled());  // enabled by default
}

@Test
void testSettersAndGetters() {
    User user = new User();
    user.setEnabled(false);        // disable the account
    assertFalse(user.isEnabled()); // should now be false
}
```

**Tests:** 4 tests | **What is verified:** default state, constructor, setters/getters

---

### 📄 `entity/DepartmentTest.java`

Tests the `Department` class (Computer Science, EEE, etc.).

```java
@Test
void testNoArgsConstructor() {
    Department dept = new Department();
    assertNull(dept.getId());
    assertNotNull(dept.getStudents()); // empty list, but NOT null
    assertNotNull(dept.getTeachers()); // empty list, but NOT null
}

@Test
void testParameterizedConstructor() {
    Department dept = new Department("Computer Science", "CSE", "CS Department");
    assertEquals("Computer Science", dept.getName());
    assertEquals("CSE", dept.getCode());
    assertEquals("CS Department", dept.getDescription());
}
```

**Tests:** 3 tests | **What is verified:** constructors, lists are initialized (not null)

---

### 📄 `entity/TeacherTest.java`

Tests the `Teacher` class.

```java
@Test
void testNoArgsConstructor() {
    Teacher teacher = new Teacher();
    assertNull(teacher.getId());
    assertNotNull(teacher.getCourses()); // courses list initialized, not null
    assertTrue(teacher.getCourses().isEmpty());
}

@Test
void testGetFullName() {
    Teacher teacher = new Teacher("John", "Smith", ...);
    assertEquals("John Smith", teacher.getFullName()); // firstName + " " + lastName
}

@Test
void testSetDepartment() {
    Teacher teacher = new Teacher();
    Department dept = new Department("CSE", "CS", "Computer Science");
    teacher.setDepartment(dept);
    assertEquals(dept, teacher.getDepartment()); // department linked correctly?
}
```

**Tests:** 5 tests | **What is verified:** constructors, full name, department & user linking

---

### 📄 `entity/StudentTest.java`

Tests the `Student` class, including course enrollment logic.

```java
@Test
void testGetFullName() {
    Student student = new Student("2024-001", "Alice", "Smith", ...);
    assertEquals("Alice Smith", student.getFullName());
}

@Test
void testEnrollInCourse() {
    Student student = new Student();
    Course course = new Course("CSE101", "Intro to CS", "Basic CS", 3);

    student.enrollInCourse(course); // call the enroll method

    // TWO things must happen:
    assertTrue(student.getEnrolledCourses().contains(course)); // student has the course
    assertTrue(course.getEnrolledStudents().contains(student)); // course has the student
}

@Test
void testDropCourse() {
    Student student = new Student();
    Course course = new Course("CSE101", ...);

    student.enrollInCourse(course);  // first enroll
    student.dropCourse(course);      // then drop

    // Both sides must be removed:
    assertFalse(student.getEnrolledCourses().contains(course));
    assertFalse(course.getEnrolledStudents().contains(student));
}
```

**Tests:** 7 tests | **What is verified:** constructors, full name, enroll/drop course (bidirectional)

---

### 📄 `entity/CourseTest.java`

Tests the `Course` class.

```java
@Test
void testNoArgsConstructor() {
    Course course = new Course();
    assertNull(course.getId());
    assertNotNull(course.getEnrolledStudents()); // set initialized, not null
    assertTrue(course.getEnrolledStudents().isEmpty());
}

@Test
void testParameterizedConstructor() {
    Course course = new Course("CSE101", "Intro to CS", "Basic CS", 3);
    assertEquals("CSE101", course.getCode());
    assertEquals("Intro to CS", course.getName());
    assertEquals(3, course.getCredits());
}

@Test
void testSetTeacher() {
    Course course = new Course("CSE101", ...);
    Teacher teacher = new Teacher("John", "Doe", ...);
    course.setTeacher(teacher);
    assertEquals(teacher, course.getTeacher()); // teacher linked?
}
```

**Tests:** 3 tests | **What is verified:** constructors, enrolled students set, teacher linking

---

## 🗄️ LAYER 2 — Repository Tests (Real Database Queries with H2)

These tests actually **save data to a database and query it back**. But instead of using your real PostgreSQL database, they use an **H2 in-memory database** — a tiny temporary database that lives only during the test and disappears when it finishes.

### 🔑 Key Annotation: `@DataJpaTest`

```java
@DataJpaTest   // ← This ONE annotation does:
               //   1. Starts Spring (only the database layer)
               //   2. Replaces PostgreSQL with H2 in-memory
               //   3. Resets the database after each test class
```

---

### 📄 `repository/RoleRepositoryTest.java`

```java
@DataJpaTest
class RoleRepositoryTest {

    @Autowired
    private RoleRepository roleRepository; // the REAL Spring JPA repository

    @BeforeEach
    void setUp() {
        roleRepository.deleteAll(); // clean DB before each test
    }

    @Test
    void testFindByName() {
        roleRepository.save(new Role("ROLE_TEACHER")); // save to H2 DB

        Optional<Role> found = roleRepository.findByName("ROLE_TEACHER"); // query it back
        assertTrue(found.isPresent());
        assertEquals("ROLE_TEACHER", found.get().getName());
    }

    @Test
    void testFindByNameNotFound() {
        Optional<Role> found = roleRepository.findByName("ROLE_ADMIN"); // doesn't exist
        assertFalse(found.isPresent()); // should be empty
    }

    @Test
    void testExistsByName() {
        roleRepository.save(new Role("ROLE_STUDENT"));

        assertTrue(roleRepository.existsByName("ROLE_STUDENT"));
        assertFalse(roleRepository.existsByName("ROLE_ADMIN"));
    }
}
```

**Tests:** 4 tests | **Custom queries tested:** `findByName()`, `existsByName()`

---

### 📄 `repository/DepartmentRepositoryTest.java`

```java
@Test
void testFindByCode() {
    departmentRepository.save(new Department("Computer Science", "CSE", "CS Dept"));

    Optional<Department> found = departmentRepository.findByCode("CSE");
    assertTrue(found.isPresent());
}

@Test
void testExistsByCode() {
    departmentRepository.save(new Department("Computer Science", "CSE", "CS Dept"));

    assertTrue(departmentRepository.existsByCode("CSE"));
    assertFalse(departmentRepository.existsByCode("EEE")); // doesn't exist
}

@Test
void testExistsByName() {
    departmentRepository.save(new Department("Computer Science", "CSE", "CS Dept"));

    assertTrue(departmentRepository.existsByName("Computer Science"));
    assertFalse(departmentRepository.existsByName("Electrical Engineering"));
}
```

**Tests:** 5 tests | **Custom queries tested:** `findByCode()`, `findByName()`, `existsByCode()`, `existsByName()`

---

### 📄 `repository/CourseRepositoryTest.java`

```java
@Test
void testFindByTeacherId() {
    // Assign TWO courses to the same teacher
    Course course1 = new Course("CSE101", "Intro to CS", "Basic CS", 3);
    course1.setTeacher(teacher);
    courseRepository.save(course1);

    Course course2 = new Course("CSE201", "Data Structures", "DS", 3);
    course2.setTeacher(teacher);
    courseRepository.save(course2);

    // Query: give me all courses for this teacher
    List<Course> found = courseRepository.findByTeacherId(teacher.getId());
    assertEquals(2, found.size()); // must find both
}

@Test
void testExistsByCode() {
    courseRepository.save(new Course("CSE101", ...));

    assertTrue(courseRepository.existsByCode("CSE101"));
    assertFalse(courseRepository.existsByCode("CSE999")); // doesn't exist
}
```

**Tests:** 4 tests | **Custom queries tested:** `findByCode()`, `findByTeacherId()`, `existsByCode()`

---

### 📄 `repository/StudentRepositoryTest.java`

```java
@Test
void testFindByDepartmentId() {
    // Create 2 students in the same department
    createStudent("2024-001", "test1@test.com", "user1");
    createStudent("2024-002", "test2@test.com", "user2");

    List<Student> found = studentRepository.findByDepartmentId(cse.getId());
    assertEquals(2, found.size()); // both found by department
}

@Test
void testExistsByStudentId() {
    createStudent("2024-001", "test@test.com", "user1");

    assertTrue(studentRepository.existsByStudentId("2024-001"));
    assertFalse(studentRepository.existsByStudentId("2024-999")); // doesn't exist
}
```

**Tests:** 7 tests | **Custom queries tested:** `findByStudentId()`, `findByEmail()`, `findByUserId()`, `findByDepartmentId()`, `existsByStudentId()`, `existsByEmail()`

---

### 📄 `repository/UserRepositoryTest.java`

Tests the `UserRepository` for login/authentication queries.

**Tests:** custom queries like `findByUsername()`, `existsByUsername()`, `existsByEmail()`

---

## ⚙️ LAYER 3 — Service Tests (Business Logic with Mockito)

These tests check the **business rules** — the most important layer. They test things like:
- Can't create two courses with the same code
- Deleting a student must also delete their user account
- Creating a teacher must also create a login account

**No real database is used.** Instead, Mockito creates **fake (mock) repositories** that return whatever you tell them to.

### 🔑 Key Annotations

```java
@ExtendWith(MockitoExtension.class)  // Use Mockito (no Spring needed)

@Mock                                // Create a FAKE version of this class
private CourseRepository courseRepository;

@InjectMocks                         // Create the REAL service, inject fake repos into it
private CourseService courseService;
```

### 🔑 How Mockito Works

```java
// "WHEN this method is called, RETURN this value"
when(courseRepository.findById(1L)).thenReturn(Optional.of(course));

// "WHEN any Course object is saved, simulate assigning an ID"
when(courseRepository.save(any(Course.class))).thenAnswer(inv -> {
    Course c = inv.getArgument(0);
    c.setId(2L);  // pretend the DB assigned ID=2
    return c;
});

// "VERIFY that this method was actually called during the test"
verify(courseRepository).save(any(Course.class));
verify(courseRepository, never()).save(any()); // verify it was NOT called
```

---

### 📄 `service/DepartmentServiceTest.java`

```java
@Test
void testGetAllDepartments() {
    when(departmentRepository.findAll()).thenReturn(Arrays.asList(cse, eee));

    List<DepartmentDTO> result = departmentService.getAllDepartments();

    assertEquals(2, result.size());
    assertEquals("Computer Science", result.get(0).getName());
    verify(departmentRepository).findAll(); // was findAll() actually called?
}

@Test
void testCreateDepartmentDuplicateCode() {
    DepartmentDTO dto = new DepartmentDTO();
    dto.setCode("CSE"); // CSE already exists

    when(departmentRepository.existsByCode("CSE")).thenReturn(true); // fake: yes it exists

    // The service should THROW an exception — can't have two "CSE" departments
    assertThrows(RuntimeException.class, () -> departmentService.createDepartment(dto));

    verify(departmentRepository, never()).save(any()); // save() must NOT have been called
}

@Test
void testDeleteDepartmentNotFound() {
    when(departmentRepository.existsById(99L)).thenReturn(false); // ID 99 doesn't exist

    assertThrows(RuntimeException.class, () -> departmentService.deleteDepartment(99L));
}
```

**Tests:** 8 tests covering: getAll, getById (found + not found), create (success + duplicate), update, delete (success + not found)

---

### 📄 `service/CourseServiceTest.java`

```java
@Test
void testGetCoursesByTeacherId() {
    when(courseRepository.findByTeacherId(1L)).thenReturn(List.of(course));

    List<CourseDTO> result = courseService.getCoursesByTeacherId(1L);

    assertEquals(1, result.size());
    assertEquals("CSE101", result.get(0).getCode());
}

@Test
void testCreateCourse() {
    CourseDTO dto = new CourseDTO();
    dto.setCode("CSE201");
    dto.setTeacherId(1L);

    when(courseRepository.existsByCode("CSE201")).thenReturn(false);  // code is free
    when(teacherRepository.findById(1L)).thenReturn(Optional.of(teacher)); // teacher exists
    when(courseRepository.save(any(Course.class))).thenAnswer(inv -> {
        Course c = inv.getArgument(0);
        c.setId(2L);
        c.setEnrolledStudents(new HashSet<>());
        return c;
    });

    CourseDTO result = courseService.createCourse(dto);

    assertEquals("CSE201", result.getCode());
    verify(courseRepository).save(any(Course.class)); // save WAS called
}

@Test
void testCreateCourseDuplicateCode() {
    CourseDTO dto = new CourseDTO();
    dto.setCode("CSE101"); // already exists!

    when(courseRepository.existsByCode("CSE101")).thenReturn(true);

    assertThrows(RuntimeException.class, () -> courseService.createCourse(dto));
    // save() should never be called when code is duplicate
}
```

**Tests:** 9 tests covering: getAll (with teacher name mapping), getById, getByIdNotFound, getByTeacherId, create (success + duplicate), update, delete (success + not found)

---

### 📄 `service/StudentServiceTest.java`

This is the most complex service test — creating a student also creates a `User` login account.

```java
@Test
void testCreateStudent() {
    StudentDTO dto = new StudentDTO();
    dto.setStudentId("2024-002");
    dto.setUsername("bob");
    dto.setPassword("pass123");
    dto.setDepartmentId(1L);

    // Set up ALL the fake repositories needed:
    when(studentRepository.existsByStudentId("2024-002")).thenReturn(false);
    when(studentRepository.existsByEmail("bob@test.com")).thenReturn(false);
    when(userRepository.existsByUsername("bob")).thenReturn(false);
    when(roleRepository.findByName("ROLE_STUDENT")).thenReturn(Optional.of(studentRole));
    when(passwordEncoder.encode("pass123")).thenReturn("encodedPass"); // BCrypt mock
    when(userRepository.save(any(User.class))).thenAnswer(inv -> { ... });
    when(departmentRepository.findById(1L)).thenReturn(Optional.of(cse));
    when(studentRepository.save(any(Student.class))).thenAnswer(inv -> { ... });

    StudentDTO result = studentService.createStudent(dto);

    assertEquals("Bob", result.getFirstName());
    // BOTH saves must happen:
    verify(userRepository).save(any(User.class));      // user account created
    verify(studentRepository).save(any(Student.class)); // student record created
}

@Test
void testDeleteStudent() {
    when(studentRepository.findById(1L)).thenReturn(Optional.of(student));

    studentService.deleteStudent(1L);

    // BOTH must be deleted:
    verify(userRepository).delete(user);          // user account deleted
    verify(studentRepository).deleteById(1L);     // student record deleted
}

@Test
void testEnrollInCourse() {
    Course course = new Course("CSE101", ...);

    when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
    when(courseRepository.findById(1L)).thenReturn(Optional.of(course));
    when(studentRepository.save(any(Student.class))).thenReturn(student);

    studentService.enrollInCourse(1L, 1L);

    assertTrue(student.getEnrolledCourses().contains(course)); // enrolled!
    verify(studentRepository).save(student);
}
```

**Tests:** 11 tests covering: getAll, getById, getByUserId, create (3 scenarios), update, delete, enrollInCourse

---

### 📄 `service/TeacherServiceTest.java`

Similar to StudentServiceTest — creating/deleting teacher also creates/deletes the User account.

```java
@Test
void testDeleteTeacher() {
    when(teacherRepository.findById(1L)).thenReturn(Optional.of(teacher));

    teacherService.deleteTeacher(1L);

    verify(userRepository).delete(user);         // user account deleted
    verify(teacherRepository).deleteById(1L);    // teacher record deleted
}
```

**Tests:** 9 tests covering: getAll, getById, getByUserId, create (success + duplicate email), update, delete (success + not found)

---

## 🌐 LAYER 4 — Controller Tests (HTTP + Spring Security)

These tests check **HTTP request handling** and **who is allowed to access which URL**. They use `MockMvc` — a fake web browser built into Spring — so no real browser or running server is needed.

### 🔑 Key Annotations

```java
@WebMvcTest(CourseController.class) // Load ONLY the web layer (fast!)
@Import(SecurityConfig.class)       // Load the REAL security rules

@Autowired
private MockMvc mockMvc;            // Fake browser to send HTTP requests

@MockBean
private CourseService courseService; // Fake service (don't need real business logic)

@WithMockUser(username = "teacher1", roles = {"TEACHER"})
// ↑ Pretend we are logged in as teacher1 with TEACHER role
```

---

### 📄 `controller/HomeControllerTest.java`

```java
@Test
void testHomeRedirectsToLogin() throws Exception {
    mockMvc.perform(get("/"))           // GET "/" (not logged in)
            .andExpect(status().is3xxRedirection())  // should redirect
            .andExpect(redirectedUrl("/login"));     // to /login page
}

@Test
void testLoginPage() throws Exception {
    mockMvc.perform(get("/login"))      // GET "/login" (public page)
            .andExpect(status().isOk()) // 200 OK — allowed without login
            .andExpect(view().name("login")); // login.html template
}

@Test
void testAboutPage() throws Exception {
    mockMvc.perform(get("/about"))      // /about is public
            .andExpect(status().isOk());
}

@Test
@WithMockUser(username = "teacher1", roles = {"TEACHER"})
void testDashboardAsTeacher() throws Exception {
    when(customUserDetailsService.getUserByUsername("teacher1")).thenReturn(user);
    when(teacherService.getTeacherEntityByUserId(1L)).thenReturn(teacher);

    mockMvc.perform(get("/dashboard"))
            .andExpect(status().isOk())
            .andExpect(view().name("teacher/dashboard"))  // teacher gets teacher dashboard
            .andExpect(model().attributeExists("teacher")); // teacher data in the model
}

@Test
@WithMockUser(username = "student1", roles = {"STUDENT"})
void testDashboardAsStudent() throws Exception {
    mockMvc.perform(get("/dashboard"))
            .andExpect(view().name("student/dashboard")); // student gets student dashboard
}

@Test
void testDashboardUnauthenticatedRedirects() throws Exception {
    mockMvc.perform(get("/dashboard")) // not logged in
            .andExpect(status().is3xxRedirection()); // redirect to login
}
```

**Tests:** 6 tests | **What is verified:** public pages, role-based dashboard routing, unauthenticated redirect

---

### 📄 `controller/DepartmentControllerTest.java`

```java
@Test
void testListDepartmentsUnauthenticated() throws Exception {
    mockMvc.perform(get("/departments"))
            .andExpect(status().is3xxRedirection()); // not logged in → redirect
}

@Test
@WithMockUser(roles = {"TEACHER"})
void testShowCreateFormAsTeacher() throws Exception {
    mockMvc.perform(get("/departments/new"))
            .andExpect(status().isOk())             // teacher CAN access
            .andExpect(view().name("department/form"));
}

@Test
@WithMockUser(roles = {"STUDENT"})
void testShowCreateFormAsStudentForbidden() throws Exception {
    mockMvc.perform(get("/departments/new"))
            .andExpect(status().isForbidden()); // student CANNOT — 403 Forbidden
}

@Test
@WithMockUser(roles = {"TEACHER"})
void testCreateDepartmentAsTeacher() throws Exception {
    mockMvc.perform(post("/departments/create")
                    .with(csrf())                    // CSRF token required for POST
                    .param("name", "Mechanical Engineering")
                    .param("code", "ME")
                    .param("description", "ME Department"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/departments")); // redirect after successful create
}
```

**Tests:** 8 tests | **Security rules confirmed:**
- Unauthenticated → redirect to login
- Student → 403 Forbidden for create/edit/delete
- Teacher → full access (200 OK)

---

### 📄 `controller/CourseControllerTest.java`

```java
// Security matrix tested:
// GET /courses       → Student: OK, Teacher: OK, Guest: Redirect
// GET /courses/new   → Student: Forbidden, Teacher: OK
// POST /courses/create → Student: Forbidden, Teacher: OK → redirect to /courses
// GET /courses/view/{id} → Student: OK, Teacher: OK
// POST /courses/delete/{id} → Student: Forbidden, Teacher: OK → redirect to /courses

@Test
@WithMockUser(roles = {"TEACHER"})
void testDeleteCourseAsTeacher() throws Exception {
    doNothing().when(courseService).deleteCourse(1L); // mock: delete does nothing

    mockMvc.perform(post("/courses/delete/1").with(csrf()))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/courses")); // after delete, go back to list
}

@Test
@WithMockUser(roles = {"STUDENT"})
void testDeleteCourseAsStudentForbidden() throws Exception {
    mockMvc.perform(post("/courses/delete/1").with(csrf()))
            .andExpect(status().isForbidden()); // students cannot delete!
}
```

**Tests:** 10 tests | **CSRF protection also tested** (all POST requests need `.with(csrf())`)

---

## 🚀 Application Context Test

### 📄 `StudentManagementSystemApplicationTests.java`

```java
@SpringBootTest   // Start the ENTIRE Spring application (all layers)
@ActiveProfiles   // Use test profile (H2 instead of PostgreSQL)
class StudentManagementSystemApplicationTests {

    @Test
    void contextLoads() {
        // No code inside — just starting the app IS the test.
        // If the app crashes on startup → this test FAILS.
        // If the app starts cleanly → this test PASSES.
    }
}
```

This is the **smoke test** — if everything in your application is wired together correctly (security config, database, services, controllers), the app starts. If there's a broken bean or misconfiguration, this fails.

---

## 📊 Complete Test Inventory

| Test File | Layer | Tests | Framework | Database |
|---|---|:---:|---|---|
| `entity/RoleTest` | Entity | 6 | JUnit 5 | None |
| `entity/UserTest` | Entity | 4 | JUnit 5 | None |
| `entity/DepartmentTest` | Entity | 3 | JUnit 5 | None |
| `entity/TeacherTest` | Entity | 5 | JUnit 5 | None |
| `entity/StudentTest` | Entity | 7 | JUnit 5 | None |
| `entity/CourseTest` | Entity | 3 | JUnit 5 | None |
| `repository/RoleRepositoryTest` | Repository | 4 | JUnit 5 + H2 | H2 in-memory |
| `repository/DepartmentRepositoryTest` | Repository | 5 | JUnit 5 + H2 | H2 in-memory |
| `repository/CourseRepositoryTest` | Repository | 4 | JUnit 5 + H2 | H2 in-memory |
| `repository/StudentRepositoryTest` | Repository | 7 | JUnit 5 + H2 | H2 in-memory |
| `repository/UserRepositoryTest` | Repository | ~4 | JUnit 5 + H2 | H2 in-memory |
| `service/DepartmentServiceTest` | Service | 8 | JUnit 5 + Mockito | None (mocked) |
| `service/CourseServiceTest` | Service | 9 | JUnit 5 + Mockito | None (mocked) |
| `service/StudentServiceTest` | Service | 11 | JUnit 5 + Mockito | None (mocked) |
| `service/TeacherServiceTest` | Service | 9 | JUnit 5 + Mockito | None (mocked) |
| `controller/HomeControllerTest` | Controller | 6 | JUnit 5 + MockMvc | None (mocked) |
| `controller/DepartmentControllerTest` | Controller | 8 | JUnit 5 + MockMvc | None (mocked) |
| `controller/CourseControllerTest` | Controller | 10 | JUnit 5 + MockMvc | None (mocked) |
| `StudentManagementSystemApplicationTests` | Integration | 1 | SpringBootTest | H2 in-memory |
| **TOTAL** | | **~118** | | |

---

## 🛠️ Tools & Libraries Used for Testing

### 1. JUnit 5 (Jupiter)
The core testing framework for Java. You write test methods with `@Test`, and JUnit runs them.

```java
@Test              // marks a method as a test case
@BeforeEach        // runs BEFORE every single @Test method
void setUp() { ... }
```

### 2. Mockito
Creates **fake (mock) objects** so you can test one layer without starting the others.

```java
@Mock              // Create a fake object
@InjectMocks       // Create the real object and inject fakes into it
when(...).thenReturn(...)   // Define fake behavior
verify(mock).method(...)    // Confirm a method was called
assertThrows(...)           // Confirm an exception was thrown
```

### 3. MockMvc (Spring Test)
A fake HTTP client for testing controllers without a real web server.

```java
mockMvc.perform(get("/courses"))
       .andExpect(status().isOk())
       .andExpect(view().name("course/list"))
       .andExpect(model().attributeExists("courses"));
```

### 4. Spring Security Test
Simulates logged-in users for controller tests.

```java
@WithMockUser(username = "teacher1", roles = {"TEACHER"})
// Pretend this user is logged in — no real login needed
```

### 5. H2 Database
An ultra-lightweight SQL database written in Java. Used automatically by `@DataJpaTest` so repository tests work without PostgreSQL.

### 6. Assertions (JUnit 5)
```java
assertEquals(expected, actual)     // must be equal
assertNotEquals(a, b)              // must NOT be equal
assertTrue(condition)              // must be true
assertFalse(condition)             // must be false
assertNull(value)                  // must be null
assertNotNull(value)               // must NOT be null
assertThrows(ExceptionType, code)  // code must throw that exception
```

---

## ▶️ Running Tests Locally

```bash
# Run ALL tests
./mvnw test

# Run tests for a specific class only
./mvnw test -Dtest=CourseServiceTest

# Run tests for a specific layer (pattern matching)
./mvnw test -Dtest="*ServiceTest"
./mvnw test -Dtest="*ControllerTest"
./mvnw test -Dtest="*RepositoryTest"

# Run tests + see detailed output
./mvnw test -Dtest=CourseServiceTest -pl . --no-transfer-progress
```

After running, reports are generated at:
```
target/surefire-reports/
├── TEST-*.xml                   → Machine-readable XML results
└── *.txt                        → Human-readable text results
```

---

## 🔄 How Tests Execute — Step by Step

```
You run: ./mvnw test
              │
              ▼
┌─────────────────────────────────────────┐
│  1. Maven compiles src/main/java        │
│  2. Maven compiles src/test/java        │
│  3. Maven Surefire Plugin finds @Test   │
└────────────────────┬────────────────────┘
                     │
         ┌───────────┼────────────┐
         ▼           ▼            ▼
   Entity Tests  Repository    Service Tests
   (no Spring)   Tests (H2)    (Mockito only)
   → Instant     → Creates     → No DB
                   H2 DB,
                   runs SQL
         │           │            │
         └───────────┼────────────┘
                     │
                     ▼
            Controller Tests
            (MockMvc + Security)
            → Fake HTTP requests
            → Real security rules
                     │
                     ▼
           App Context Test
           (Full Spring Boot)
           → Starts everything
           → contextLoads() passes
                     │
                     ▼
    ┌──────────────────────────────┐
    │  BUILD SUCCESS (all pass)    │
    │  or                          │
    │  BUILD FAILURE (any fail)    │
    └──────────────────────────────┘
```

---

## 🤖 GitHub Actions CI/CD Pipeline

### What is CI/CD?

| Term | Full Form | What it means |
|---|---|---|
| **CI** | Continuous Integration | Every code push → automatically build & test |
| **CD** | Continuous Deployment | If tests pass → automatically deploy to server |

**Without CI/CD:** You push code, nobody knows if it works until someone manually tests it.
**With CI/CD:** You push code, GitHub automatically runs all 118 tests within minutes and tells you PASS or FAIL.

---

### GitHub Actions Workflow File

This file goes at `.github/workflows/ci.yml` in the repository root.
GitHub automatically detects it and runs the pipeline.

```yaml
name: Java CI with Maven

# WHEN to trigger this pipeline:
on:
  push:
    branches: [ "main" ]       # every push to main branch
  pull_request:
    branches: [ "main" ]       # every Pull Request targeting main

jobs:
  build-and-test:
    runs-on: ubuntu-latest     # GitHub gives a fresh Ubuntu Linux VM

    steps:

      # Step 1: Download the code from GitHub
      - name: Checkout repository
        uses: actions/checkout@v4

      # Step 2: Install Java 21 (matches pom.xml java.version)
      - name: Set up JDK 21
        uses: actions/setup-java@v4
        with:
          java-version: '21'
          distribution: 'temurin'   # Eclipse Temurin (OpenJDK)
          cache: 'maven'            # Cache Maven deps → faster builds

      # Step 3: Run all tests
      - name: Run Tests
        run: ./mvnw test --no-transfer-progress
        # H2 is used automatically — NO PostgreSQL needed on the VM!

      # Step 4: Save test reports as downloadable artifacts
      - name: Upload Test Reports
        uses: actions/upload-artifact@v4
        if: always()               # upload even if tests failed
        with:
          name: test-reports
          path: target/surefire-reports/
          retention-days: 30       # keep reports for 30 days
```

---

### The Complete CI/CD Flow

```
Developer writes code
        │
        ▼
git commit + git push → GitHub
        │
        ▼
GitHub detects push to "main" branch
        │
        ▼
GitHub Actions reads .github/workflows/ci.yml
        │
        ▼
GitHub creates a BRAND NEW Ubuntu VM (free, disposable)
        │
        ▼
VM downloads your code (git checkout)
        │
        ▼
VM installs Java 21
        │
        ▼
VM runs: ./mvnw test
        │
        ├── Entity Tests      → PASS ✅ (instant, pure Java)
        ├── Repository Tests  → PASS ✅ (H2 auto-configured, no PostgreSQL needed)
        ├── Service Tests     → PASS ✅ (Mockito, no DB)
        ├── Controller Tests  → PASS ✅ (MockMvc + Security)
        └── Context Test      → PASS ✅ (full Spring boot with H2)
        │
        ▼
ALL TESTS PASS?
        │
     YES │                      NO │
        ▼                         ▼
  ✅ Green checkmark          ❌ Red X on GitHub
  on GitHub commit            Developer gets email
  Code is trusted             "Build Failed — fix it!"
        │
        ▼
  (Optional CD step)
  Deploy to production server
```

---

### Why H2 Makes CI Work

Your [pom.xml](pom.xml) has both databases as dependencies:

```xml
<!-- H2 — ultra-lightweight, auto-used during tests -->
<dependency>
    <groupId>com.h2database</groupId>
    <artifactId>h2</artifactId>
    <scope>runtime</scope>
</dependency>

<!-- PostgreSQL — used in real production/Docker -->
<dependency>
    <groupId>org.postgresql</groupId>
    <artifactId>postgresql</artifactId>
    <scope>runtime</scope>
</dependency>
```

The GitHub Actions VM has **no PostgreSQL installed**, but that doesn't matter because:
- `@DataJpaTest` tells Spring "use an in-memory database for tests"
- Spring sees H2 on the classpath and uses it automatically
- All SQL queries that work on PostgreSQL also work on H2
- The VM is clean after each run — no leftover test data

---

### GitHub Actions Status Badges

Once the workflow runs, you can add a status badge to your README:

```markdown
![CI](https://github.com/AriyaArKa/3-2-LABS/actions/workflows/ci.yml/badge.svg)
```

This shows a live badge:
- `passing` → all tests passed on last push
- `failing` → tests are broken

---

### What You See on GitHub

1. Go to your repository: `github.com/AriyaArKa/3-2-LABS`
2. Click the **"Actions"** tab
3. See a list of every pipeline run with:
   - ✅ Green = all tests passed
   - ❌ Red = something failed
   - Click any run → see detailed logs for each step

---

## 🔐 Spring Security Testing Explained

Your app uses **Spring Security** for authentication and authorization.
The controller tests verify that the security rules actually work.

### HTTP Status Codes Used in Tests

| Code | Meaning | When it happens |
|---|:---:|---|
| `200 OK` | Success | User has access, page loads |
| `302 Found` | Redirect | Not logged in → sent to `/login` |
| `403 Forbidden` | Access Denied | Logged in but wrong role |

### CSRF Protection

CSRF (Cross-Site Request Forgery) is a web security feature. Spring Security requires a secret token on every `POST`/`PUT`/`DELETE` request to prove the request came from YOUR website, not a hacker's.

```java
// Without CSRF token → test would fail with 403
mockMvc.perform(post("/courses/create")
        .with(csrf())        // ← adds the required CSRF token
        .param("code", "CSE201"))
```

In controller tests, all `POST` requests must include `.with(csrf())`.

### Security Rules Summary (Tested by Controller Tests)

| URL | Method | Guest | STUDENT | TEACHER |
|---|---|:---:|:---:|:---:|
| `/login` | GET | ✅ 200 | ✅ 200 | ✅ 200 |
| `/about` | GET | ✅ 200 | ✅ 200 | ✅ 200 |
| `/dashboard` | GET | ➡️ redirect | ✅ 200 | ✅ 200 |
| `/departments` | GET | ➡️ redirect | ✅ 200 | ✅ 200 |
| `/departments/new` | GET | ➡️ redirect | ❌ 403 | ✅ 200 |
| `/departments/create` | POST | ➡️ redirect | ❌ 403 | ✅ redirect |
| `/courses` | GET | ➡️ redirect | ✅ 200 | ✅ 200 |
| `/courses/new` | GET | ➡️ redirect | ❌ 403 | ✅ 200 |
| `/courses/create` | POST | ➡️ redirect | ❌ 403 | ✅ redirect |
| `/courses/view/{id}` | GET | ➡️ redirect | ✅ 200 | ✅ 200 |
| `/courses/delete/{id}` | POST | ➡️ redirect | ❌ 403 | ✅ redirect |

---

## 📦 Tech Stack for Testing (from pom.xml)

```xml
<!-- Spring Boot Test — includes JUnit 5, Mockito, MockMvc, AssertJ -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-test</artifactId>
    <scope>test</scope>    <!-- only used during testing, not in production -->
</dependency>

<!-- Spring Security Test — @WithMockUser, csrf() support -->
<dependency>
    <groupId>org.springframework.security</groupId>
    <artifactId>spring-security-test</artifactId>
    <scope>test</scope>
</dependency>

<!-- H2 — in-memory database for repository + context tests -->
<dependency>
    <groupId>com.h2database</groupId>
    <artifactId>h2</artifactId>
    <scope>runtime</scope>
</dependency>
```

The `scope: test` means these libraries are only included when running tests — they are NOT bundled into the final production JAR file.

---

## 🎯 Quick Reference — Annotations Cheat Sheet

| Annotation | Where Used | What it Does |
|---|---|---|
| `@Test` | Any layer | Marks method as a test case |
| `@BeforeEach` | Any layer | Runs before every test method |
| `@ExtendWith(MockitoExtension.class)` | Service | Enables Mockito |
| `@Mock` | Service | Creates a fake object |
| `@InjectMocks` | Service | Creates real object with fakes injected |
| `@DataJpaTest` | Repository | Starts only DB layer, uses H2 |
| `@Autowired` | Repository | Injects real Spring beans |
| `@WebMvcTest(X.class)` | Controller | Starts only web layer |
| `@MockBean` | Controller | Creates fake Spring bean |
| `@Import(SecurityConfig.class)` | Controller | Loads real security rules |
| `@SpringBootTest` | Integration | Starts full application |
| `@WithMockUser(roles = {...})` | Controller | Simulates logged-in user |

---

*This document covers all 19 test files across 4 testing layers with ~118 individual test cases, and the complete GitHub Actions CI/CD pipeline for the Student Management System.*
