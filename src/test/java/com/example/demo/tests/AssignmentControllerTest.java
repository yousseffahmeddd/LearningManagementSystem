package com.example.demo.tests;

import com.example.demo.models.Assignment;
import com.example.demo.models.Submission;
import com.example.demo.models.User;
import com.example.demo.models.UserRole;
import com.example.demo.repository.AssignmentRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.JWTService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import java.io.IOException;
import java.util.Map;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Fail.fail;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AssignmentControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private JWTService jwtService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AssignmentRepository assignmentRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private String baseUrl;

    @BeforeEach
    void setUp() {
        baseUrl = "http://localhost:" + port + "/api/assignments";

        // Create and save the required users
        User instructorUser = new User(1L, "youssef", passwordEncoder.encode("password"), "youssef@gmail.com", UserRole.INSTRUCTOR, null, null , null);
        User studentUser = new User(2L, "mazen", passwordEncoder.encode("password"), "mazen@gmail.com", UserRole.STUDENT, null, null , null);
        User adminUser = new User(3L, "mohamed", passwordEncoder.encode("password"), "mohamed@gmail.com", UserRole.ADMIN, null, null , null);
        userRepository.save(instructorUser);
        userRepository.save(studentUser);
        userRepository.save(adminUser);
    }

    @Test
    void testCreateAssignmentAsInstructor() {
        String token = jwtService.generateToken("youssef");

        Assignment newAssignment = new Assignment("A001", "Assignment 1", "Assignment Description", "CS001", null, null);
        HttpHeaders headers = new HttpHeaders();
        headers.set("User-Role", "INSTRUCTOR");
        headers.set("Authorization", "Bearer " + token);
        headers.set("Content-Type", "application/json");
        HttpEntity<Assignment> request = new HttpEntity<>(newAssignment, headers);

        ResponseEntity<Assignment> response = restTemplate.postForEntity(baseUrl + "/create", request, Assignment.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getTitle()).isEqualTo("Assignment 1");
    }

    @Test
    void testCreateAssignmentAsAdmin() {
        String token = jwtService.generateToken("mohamed");

        Assignment newAssignment = new Assignment("A002", "Assignment 2", "Assignment Description", "CS001", null, null);
        HttpHeaders headers = new HttpHeaders();
        headers.set("User-Role", "ADMIN");
        headers.set("Authorization", "Bearer " + token);
        headers.set("Content-Type", "application/json");
        HttpEntity<Assignment> request = new HttpEntity<>(newAssignment, headers);

        ResponseEntity<Assignment> response = restTemplate.postForEntity(baseUrl + "/create", request, Assignment.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getTitle()).isEqualTo("Assignment 2");
    }

    @Test
    void testCreateAssignmentAsStudent() {
        String token = jwtService.generateToken("mazen");

        Assignment newAssignment = new Assignment("A003", "Assignment 3", "Assignment Description", "CS001", null, null);
        HttpHeaders headers = new HttpHeaders();
        headers.set("User-Role", "STUDENT");
        headers.set("Authorization", "Bearer " + token);
        headers.set("Content-Type", "application/json");
        HttpEntity<Assignment> request = new HttpEntity<>(newAssignment, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(baseUrl + "/create", request, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).contains("Only instructors or admins can create assignments.");
    }

    @Test
    void testSubmitAssignmentAsStudent() {
        String token = jwtService.generateToken("mazen");

        MockMultipartFile file = new MockMultipartFile("file", "test.txt", "text/plain", "Test content".getBytes());
        HttpHeaders headers = new HttpHeaders();
        headers.set("Student-Id", "2");
        headers.set("User-Role", "STUDENT");
        headers.set("Authorization", "Bearer " + token);
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        try {
            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("file", new ByteArrayResource(file.getBytes()) {
                @Override
                public String getFilename() {
                    return file.getOriginalFilename();
                }
            });

            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

            ResponseEntity<String> response = restTemplate.exchange(baseUrl + "/A001/submit", HttpMethod.POST, requestEntity, String.class);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).contains("test.txt");
        } catch (IOException e) {
            e.printStackTrace();
            fail("IOException occurred while getting file bytes");
        }
    }

    @Test
    void testSubmitAssignmentAsInstructor() {
        String token = jwtService.generateToken("youssef");

        MockMultipartFile file = new MockMultipartFile("file", "test.txt", "text/plain", "Test content".getBytes());
        HttpHeaders headers = new HttpHeaders();
        headers.set("Student-Id", "1");
        headers.set("User-Role", "INSTRUCTOR");
        headers.set("Authorization", "Bearer " + token);
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        try {
            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("file", new ByteArrayResource(file.getBytes()) {
                @Override
                public String getFilename() {
                    return file.getOriginalFilename();
                }
            });

            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

            ResponseEntity<String> response = restTemplate.exchange(baseUrl + "/A001/submit", HttpMethod.POST, requestEntity, String.class);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
            assertThat(response.getBody()).contains("Only students are allowed to submit assignments.");
        } catch (IOException e) {
            e.printStackTrace();
            fail("IOException occurred while getting file bytes");
        }
    }

    @Test
    void testSubmitAssignmentAsAdmin() {
        String token = jwtService.generateToken("mohamed");

        MockMultipartFile file = new MockMultipartFile("file", "test.txt", "text/plain", "Test content".getBytes());
        HttpHeaders headers = new HttpHeaders();
        headers.set("Student-Id", "3");
        headers.set("User-Role", "ADMIN");
        headers.set("Authorization", "Bearer " + token);
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        try {
            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("file", new ByteArrayResource(file.getBytes()) {
                @Override
                public String getFilename() {
                    return file.getOriginalFilename();
                }
            });

            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

            ResponseEntity<String> response = restTemplate.exchange(baseUrl + "/A001/submit", HttpMethod.POST, requestEntity, String.class);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
            assertThat(response.getBody()).contains("Only students are allowed to submit assignments.");
        } catch (IOException e) {
            e.printStackTrace();
            fail("IOException occurred while getting file bytes");
        }
    }

    @Test
    void testGetAllAssignments() {
        String token = jwtService.generateToken("youssef");

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        HttpEntity<Void> request = new HttpEntity<>(headers);

        ResponseEntity<Assignment[]> response = restTemplate.exchange(baseUrl + "/assignments", HttpMethod.GET, request, Assignment[].class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
    }

    @Test
    void testGetAllSubmissionsAsInstructor() {
        String token = jwtService.generateToken("youssef");

        HttpHeaders headers = new HttpHeaders();
        headers.set("User-Role", "INSTRUCTOR");
        headers.set("Authorization", "Bearer " + token);
        HttpEntity<Void> request = new HttpEntity<>(headers);

        ResponseEntity<Submission[]> response = restTemplate.exchange(baseUrl + "/submissions", HttpMethod.GET, request, Submission[].class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
    }

    @Test
    void testGetAllSubmissionsAsStudent() {
        String token = jwtService.generateToken("mazen");

        HttpHeaders headers = new HttpHeaders();
        headers.set("User-Role", "STUDENT");
        headers.set("Authorization", "Bearer " + token);
        HttpEntity<Void> request = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(baseUrl + "/submissions", HttpMethod.GET, request, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(response.getBody()).contains("Access denied: Only instructors can view all submissions.");
    }

    @Test
    void testGetAllSubmissionsAsAdmin() {
        String token = jwtService.generateToken("mohamed");

        HttpHeaders headers = new HttpHeaders();
        headers.set("User-Role", "ADMIN");
        headers.set("Authorization", "Bearer " + token);
        HttpEntity<Void> request = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(baseUrl + "/submissions", HttpMethod.GET, request, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(response.getBody()).contains("Access denied: Only instructors can view all submissions.");
    }

    @Test
    void testAddGradeAndFeedbackAsInstructor() {
        String token = jwtService.generateToken("youssef");

        // Ensure the assignment exists
        Assignment newAssignment = new Assignment("A001", "Assignment 1", "Assignment Description", "CS001", null, null);
        assignmentRepository.save(newAssignment);

        HttpHeaders headers = new HttpHeaders();
        headers.set("User-Role", "INSTRUCTOR");
        headers.set("Authorization", "Bearer " + token);
        headers.set("Content-Type", "application/json");
        HttpEntity<Map<String, String>> request = new HttpEntity<>(Map.of("grade", "A", "feedback", "Good job"), headers);

        ResponseEntity<Assignment> response = restTemplate.postForEntity(baseUrl + "/A001/feedback", request, Assignment.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getGrade()).isEqualTo("A");
        assertThat(response.getBody().getFeedback()).isEqualTo("Good job");
    }

    @Test
    void testAddGradeAndFeedbackAsStudent() {
        String token = jwtService.generateToken("mazen");

        HttpHeaders headers = new HttpHeaders();
        headers.set("User-Role", "STUDENT");
        headers.set("Authorization", "Bearer " + token);
        headers.set("Content-Type", "application/json");
        HttpEntity<Map<String, String>> request = new HttpEntity<>(Map.of("grade", "A", "feedback", "Good job"), headers);

        ResponseEntity<String> response = restTemplate.postForEntity(baseUrl + "/A001/feedback", request, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(response.getBody()).contains("Access denied: Only instructors can add grades and feedback.");
    }

    @Test
    void testAddGradeAndFeedbackAsAdmin() {
        String token = jwtService.generateToken("mohamed");

        HttpHeaders headers = new HttpHeaders();
        headers.set("User-Role", "ADMIN");
        headers.set("Authorization", "Bearer " + token);
        headers.set("Content-Type", "application/json");
        HttpEntity<Map<String, String>> request = new HttpEntity<>(Map.of("grade", "A", "feedback", "Good job"), headers);

        ResponseEntity<String> response = restTemplate.postForEntity(baseUrl + "/A001/feedback", request, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(response.getBody()).contains("Access denied: Only instructors can add grades and feedback.");
    }
}