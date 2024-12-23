package com.example.demo.tests;

import com.example.demo.models.Course;
import com.example.demo.models.Lesson;
import com.example.demo.models.User;
import com.example.demo.models.UserRole;
import com.example.demo.repository.CourseRepository;
import com.example.demo.repository.LessonRepository;
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
class LessonControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private LessonRepository lessonRepository;

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
        baseUrl = "http://localhost:" + port + "/api/lessons";

        // Create and save the required users
        User instructorUser = new User(1L, "youssef", passwordEncoder.encode("password"), "youssef@gmail.com", UserRole.INSTRUCTOR, null, null);
        User adminUser = new User(2L, "mohamed", passwordEncoder.encode("password"), "mohamed@gmail.com", UserRole.ADMIN, null, null);
        User studentUser = new User(3L, "mazen", passwordEncoder.encode("password"), "mazen@gmail.com", UserRole.STUDENT, null, null);

        userRepository.save(instructorUser);
        userRepository.save(adminUser);
        userRepository.save(studentUser);
    }

    @Test
    @DirtiesContext
    void testInstructorCreateLesson() {
        String token = jwtService.generateToken("youssef");

        // Ensure the courseId and instructorId are valid and exist in the database
        Course course = new Course("CS001", "Java Programming", "Introduction to Java Programming", 3, 1L);
        courseRepository.save(course);

        // Create a lesson with a unique title
        Lesson newLesson = new Lesson(null, "CS001", "Java Programming Language", 1L);

        HttpHeaders headers = new HttpHeaders();
        headers.set("User-Role", "INSTRUCTOR");
        headers.set("Authorization", "Bearer " + token);
        headers.set("Content-Type", "application/json");
        HttpEntity<Lesson> request = new HttpEntity<>(newLesson, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(baseUrl, request, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo("Lesson added successfully.");
    }

    @Test
    @DirtiesContext
    void testAdminCreateLesson() {
        String token = jwtService.generateToken("mohamed");

        // Ensure the courseId and instructorId are valid and exist in the database
        Course course = new Course("CS001", "Java Programming", "Introduction to Java Programming", 3, 1L);
        courseRepository.save(course);

        Lesson newLesson = lessonRepository.save("CS001", "Java Programming Language", 1L);
        HttpHeaders headers = new HttpHeaders();
        headers.set("User-Role", "ADMIN");
        headers.set("Authorization", "Bearer " + token);
        headers.set("Content-Type", "application/json");
        HttpEntity<Lesson> request = new HttpEntity<>(newLesson, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(baseUrl, request, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isEqualTo("Only Instructors can create lessons.");
    }

    @Test
    @DirtiesContext
    void testStudentCreateLesson() {
        String token = jwtService.generateToken("mazen");

        // Ensure the courseId and instructorId are valid and exist in the database
        Course course = new Course("CS001", "Java Programming", "Introduction to Java Programming", 3, 1L);
        courseRepository.save(course);

        Lesson newLesson = lessonRepository.save("CS001", "Java Programming Language", 1L);
        HttpHeaders headers = new HttpHeaders();
        headers.set("User-Role", "STUDENT");
        headers.set("Authorization", "Bearer " + token);
        headers.set("Content-Type", "application/json");
        HttpEntity<Lesson> request = new HttpEntity<>(newLesson, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(baseUrl, request, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isEqualTo("Only Instructors can create lessons.");
    }

    @Test
    @DirtiesContext
    void testListAllLessons() {
        String token = jwtService.generateToken("youssef");

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        HttpEntity<Void> request = new HttpEntity<>(headers);

        ResponseEntity<Lesson[]> response = restTemplate.exchange(baseUrl, HttpMethod.GET, request, Lesson[].class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
    }

    @Test
    @DirtiesContext
    void testExistsByTitleAndCourseId() {
        // Ensure the courseId and instructorId are valid and exist in the database
        Course course = new Course("CS001", "Java Programming", "Introduction to Java Programming", 3, 1L);
        courseRepository.save(course);

        // Create a lesson to test the existence check
        Lesson existingLesson = lessonRepository.save("CS001", "Java Programming Language", 1L);

        // Check if the lesson exists by title and courseId
        boolean exists = lessonRepository.existsByTitleAndCourseId("Java Programming Language", "CS001");

        // Assert that the lesson exists
        assertThat(exists).isTrue();

        // Check if a non-existing lesson returns false
        boolean notExists = lessonRepository.existsByTitleAndCourseId("Non-Existent Title", "CS001");

        // Assert that the non-existing lesson does not exist
        assertThat(notExists).isFalse();
    }
}