package com.example.demo.tests;

import com.example.demo.models.Course;
import com.example.demo.models.User;
import com.example.demo.models.UserRole;
import com.example.demo.repository.CourseRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.CourseService;
import com.example.demo.service.JWTService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CourseControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CourseService courseService;

    @Autowired
    private JWTService jwtService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private String baseUrl;

    @BeforeEach
    void setUp() {
        baseUrl = "http://localhost:" + port + "/api/courses";

        // Create and save the required users
        User instructorUser = new User(1L, "youssef", passwordEncoder.encode("password"), "youssef@gmail.com", UserRole.INSTRUCTOR, null, null , null);
        User adminUser = new User(2L, "mohamed", passwordEncoder.encode("password"), "mohamed@gmail.com", UserRole.ADMIN, null, null ,  null);
        User studentUser = new User(3L, "mazen", passwordEncoder.encode("password"), "mazen@gmail.com", UserRole.STUDENT, null, null , null);
        userRepository.save(instructorUser);
        userRepository.save(adminUser);
        userRepository.save(studentUser);

        // Create and save a course for update test
        Course course = new Course("CS001", "C++ Programing", "Introduction to C++ Programming", 3, 1L);
        courseRepository.save(course);
    }

    @Test
    void testGetAllCourses() {
        String token = jwtService.generateToken("mohamed");

        HttpHeaders headers = new HttpHeaders();
        headers.set("User-Role", "ADMIN");
        headers.set("Authorization", "Bearer " + token);
        HttpEntity<Void> request = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(baseUrl, HttpMethod.GET, request, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void testAdminCreateCourse() {
        String token = jwtService.generateToken("mohamed");

        // Use the correct instructorId (1L) for the instructor_user
        Course newCourse = new Course("CS002", "Java Programming", "Introduction to Java Programming", 3, 1L);
        HttpHeaders headers = new HttpHeaders();
        headers.set("User-Role", "ADMIN");
        headers.set("Authorization", "Bearer " + token);
        headers.set("Content-Type", "application/json");
        HttpEntity<Course> request = new HttpEntity<>(newCourse, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(baseUrl, request, String.class);

        System.out.println("Response Status Code: " + response.getStatusCode());
        System.out.println("Response Body: " + response.getBody());

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo("Course created successfully");
    }

    @Test
    void testInstructorCreateCourse() {
        String token = jwtService.generateToken("youssef");

        Course newCourse = new Course("CS003", "C# Programming", "Introduction to C# Programming", 3, 1L);
        HttpHeaders headers = new HttpHeaders();
        headers.set("User-Role", "INSTRUCTOR");
        headers.set("Authorization", "Bearer " + token);
        headers.set("Content-Type", "application/json");
        HttpEntity<Course> request = new HttpEntity<>(newCourse, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(baseUrl, request, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo("Course created successfully");
    }

    @Test
    void testCreateCourseWithExistingTitle() {
        String token = jwtService.generateToken("youssef");

        // Ensure the course with the same title exists in the repository
        Course existingCourse = new Course("CS002", "C++ Programming", "Introduction to C++ Programming", 3, 1L);
        courseRepository.save(existingCourse);

        // Attempt to create a new course with the same title
        Course newCourseWithSameTitle = new Course("CS009", "C++ Programming", "Introduction to Java Programming", 3, 1L);
        HttpHeaders headers = new HttpHeaders();
        headers.set("User-Role", "INSTRUCTOR");
        headers.set("Authorization", "Bearer " + token);
        headers.set("Content-Type", "application/json");
        HttpEntity<Course> request = new HttpEntity<>(newCourseWithSameTitle, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(baseUrl, request, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isEqualTo("A course with this title already exists.");
    }

    @Test
    void testCreateCourseWithExistingId() {
        String token = jwtService.generateToken("youssef");

        // Attempt to create a new course with the same ID as an existing course
        Course newCourseWithSameId = new Course("CS001", "Java Programming", "Introduction to Java Programming", 3, 1L);
        HttpHeaders headers = new HttpHeaders();
        headers.set("User-Role", "INSTRUCTOR");
        headers.set("Authorization", "Bearer " + token);
        headers.set("Content-Type", "application/json");
        HttpEntity<Course> request = new HttpEntity<>(newCourseWithSameId, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(baseUrl, request, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isEqualTo("A course with this ID already exists.");
    }

    @Test
    void testStudentCannotCreateCourse() {
        String token = jwtService.generateToken("mazen");

        // Ensure the student ID matches the ID of the student user created in setUp
        Course newCourse = new Course("CS003", "C# Programming", "Introduction to C# Programming", 3, 1L);
        HttpHeaders headers = new HttpHeaders();
        headers.set("User-Role", "STUDENT");
        headers.set("Authorization", "Bearer " + token);
        headers.set("Content-Type", "application/json");
        HttpEntity<Course> request = new HttpEntity<>(newCourse, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(baseUrl, request, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isEqualTo("Only Instructors or Admins can create courses.");
    }

    @Test
    void testStudentCannotUpdateCourse() {
        String token = jwtService.generateToken("mazen");

        Course updatedCourse = new Course("CS003", "C# Programming", "Introduction to C# Programming", 3, 1L);
        HttpHeaders headers = new HttpHeaders();
        headers.set("User-Role", "STUDENT");
        headers.set("Authorization", "Bearer " + token);
        headers.set("Content-Type", "application/json");
        HttpEntity<Course> request = new HttpEntity<>(updatedCourse, headers);

        ResponseEntity<String> response = restTemplate.exchange(baseUrl + "/CS001", HttpMethod.PUT, request, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isEqualTo("Only Instructors or Admins can update courses.");
    }

    @Test
    void testStudentCannotDeleteCourse() {
        String token = jwtService.generateToken("mazen");

        HttpHeaders headers = new HttpHeaders();
        headers.set("User-Role", "STUDENT");
        headers.set("Authorization", "Bearer " + token);
        HttpEntity<Void> request = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(baseUrl + "/CS001", HttpMethod.DELETE, request, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isEqualTo("Only Admins can delete courses.");
    }

    @Test
    void testInstructorUpdateCourse() {
        String token = jwtService.generateToken("youssef");

        // Ensure the course exists in the repository
        Course existingCourse = new Course("CS001", "C++ Programming", "Introduction to C++ Programming", 3, 1L);
        courseRepository.save(existingCourse);

        // Update the course with valid data
        Course updatedCourse = new Course("CS001", "Advanced C++ Programming", "Advanced topics in C++ Programming", 3, 1L);
        HttpHeaders headers = new HttpHeaders();
        headers.set("User-Role", "INSTRUCTOR");
        headers.set("Authorization", "Bearer " + token);
        headers.set("Content-Type", "application/json");
        HttpEntity<Course> request = new HttpEntity<>(updatedCourse, headers);

        ResponseEntity<String> response = restTemplate.exchange(baseUrl + "/CS001", HttpMethod.PUT, request, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo("Course updated successfully.");
    }

    @Test
    void testInstructorDeleteCourse() {
        String token = jwtService.generateToken("youssef");

        HttpHeaders headers = new HttpHeaders();
        headers.set("User-Role", "INSTRUCTOR");
        headers.set("Authorization", "Bearer " + token);
        HttpEntity<Void> request = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(baseUrl + "/CS001", HttpMethod.DELETE, request, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isEqualTo("Only Admins can delete courses.");
    }

    @Test
    void testAdminUpdateCourse() {
        String token = jwtService.generateToken("mohamed");

        // Ensure the course exists in the repository
        Course existingCourse = new Course("CS001", "C++ Programming", "Introduction to C++ Programming", 3, 1L);
        courseRepository.save(existingCourse);

        // Update the course with valid data
        Course updatedCourse = new Course("CS001", "Advanced C++ Programming", "Advanced topics in C++ Programming", 3, 1L);
        HttpHeaders headers = new HttpHeaders();
        headers.set("User-Role", "ADMIN");
        headers.set("Authorization", "Bearer " + token);
        headers.set("Content-Type", "application/json");
        HttpEntity<Course> request = new HttpEntity<>(updatedCourse, headers);

        ResponseEntity<String> response = restTemplate.exchange(baseUrl + "/CS001", HttpMethod.PUT, request, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo("Course updated successfully.");
    }

    @Test
    void testAdminDeleteCourse() {
        String token = jwtService.generateToken("mohamed");

        HttpHeaders headers = new HttpHeaders();
        headers.set("User-Role", "ADMIN");
        headers.set("Authorization", "Bearer " + token);
        HttpEntity<Void> request = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(baseUrl + "/CS001", HttpMethod.DELETE, request, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo("Course deleted successfully.");
    }
}