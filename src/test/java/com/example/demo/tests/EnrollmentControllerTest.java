package com.example.demo.tests;

import com.example.demo.models.Course;
import com.example.demo.models.Enrollment;
import com.example.demo.models.User;
import com.example.demo.models.UserRole;
import com.example.demo.repository.CourseRepository;
import com.example.demo.repository.EnrollmentRepository;
import com.example.demo.repository.UserRepository;
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
import org.springframework.test.annotation.DirtiesContext;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class EnrollmentControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private EnrollmentRepository enrollmentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private JWTService jwtService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private String baseUrl;

    @BeforeEach
    void setUp() {
        baseUrl = "http://localhost:" + port + "/api/enrollments";

        // Create and save the required users
        User studentUser = new User(3L, "mazen", passwordEncoder.encode("password"), "mazen@gmail.com", UserRole.STUDENT, null, null , null);
        userRepository.save(studentUser);

        User instructorUser = new User(1L, "youssef", passwordEncoder.encode("password"), "youssef@gmail.com", UserRole.INSTRUCTOR, null, null , null);
        userRepository.save(instructorUser);

        User adminUser = new User(2L, "mohamed", passwordEncoder.encode("password"), "mohamed@gmail.com", UserRole.ADMIN, null, null , null);
        userRepository.save(adminUser);

        // Create and save a course
        Course course = new Course("CS001", "Java Programming", "Introduction to Java Programming", 3, 1L);
        courseRepository.save(course);
    }

    @Test
    void testEnrollStudent() {
        String token = jwtService.generateToken("mazen");

        Enrollment newEnrollment = new Enrollment("CS001", 3L);
        HttpHeaders headers = new HttpHeaders();
        headers.set("User-Role", "STUDENT");
        headers.set("Authorization", "Bearer " + token);
        headers.set("Content-Type", "application/json");
        HttpEntity<Enrollment> request = new HttpEntity<>(newEnrollment, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(baseUrl, request, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo("Student enrolled successfully.");
    }

    @Test
    void testEnrollInstructor() {
        String token = jwtService.generateToken("youssef");

        Enrollment newEnrollment = new Enrollment("CS001", 1L);
        HttpHeaders headers = new HttpHeaders();
        headers.set("User-Role", "INSTRUCTOR");
        headers.set("Authorization", "Bearer " + token);
        headers.set("Content-Type", "application/json");
        HttpEntity<Enrollment> request = new HttpEntity<>(newEnrollment, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(baseUrl, request, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isEqualTo("Only students can enroll in a course.");
    }

    @Test
    void testEnrollAdmin() {
        String token = jwtService.generateToken("mohamed");

        Enrollment newEnrollment = new Enrollment("CS001", 2L);
        HttpHeaders headers = new HttpHeaders();
        headers.set("User-Role", "ADMIN");
        headers.set("Authorization", "Bearer " + token);
        headers.set("Content-Type", "application/json");
        HttpEntity<Enrollment> request = new HttpEntity<>(newEnrollment, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(baseUrl, request, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isEqualTo("Only students can enroll in a course.");
    }

    @Test
    void testEnrollStudentAlreadyEnrolled() {
        String token = jwtService.generateToken("mazen");

        // Enroll the student first
        Enrollment initialEnrollment = new Enrollment("CS001", 3L);
        enrollmentRepository.save(initialEnrollment.getCourseId(), initialEnrollment.getStudentId());

        Enrollment newEnrollment = new Enrollment("CS001", 3L);
        HttpHeaders headers = new HttpHeaders();
        headers.set("User-Role", "STUDENT");
        headers.set("Authorization", "Bearer " + token);
        headers.set("Content-Type", "application/json");
        HttpEntity<Enrollment> request = new HttpEntity<>(newEnrollment, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(baseUrl, request, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isEqualTo("Student is already enrolled in this course.");
    }

    @Test
    void testEnrollStudentToNonExistentCourse() {
        String token = jwtService.generateToken("mazen");

        Enrollment newEnrollment = new Enrollment("CS009", 3L); // Non-existent course ID
        HttpHeaders headers = new HttpHeaders();
        headers.set("User-Role", "STUDENT");
        headers.set("Authorization", "Bearer " + token);
        headers.set("Content-Type", "application/json");
        HttpEntity<Enrollment> request = new HttpEntity<>(newEnrollment, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(baseUrl, request, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isEqualTo("Course not found.");
    }

    @Test
    void testListAllEnrollments() {
        String token = jwtService.generateToken("youssef");

        HttpHeaders headers = new HttpHeaders();
        headers.set("User-Role", "INSTRUCTOR");
        headers.set("Authorization", "Bearer " + token);
        HttpEntity<Void> request = new HttpEntity<>(headers);

        ResponseEntity<Enrollment[]> response = restTemplate.exchange(baseUrl, HttpMethod.GET, request, Enrollment[].class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
    }
}