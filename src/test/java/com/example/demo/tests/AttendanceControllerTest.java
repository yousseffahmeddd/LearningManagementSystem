package com.example.demo.tests;

import com.example.demo.models.Attendance;
import com.example.demo.models.User;
import com.example.demo.models.UserRole;
import com.example.demo.repository.AttendanceRepository;
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

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AttendanceControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private AttendanceRepository attendanceRepository;

    @Autowired
    private LessonRepository lessonRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JWTService jwtService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private String baseUrl;

    @BeforeEach
    void setUp() {
        baseUrl = "http://localhost:" + port + "/api/attendance";

        // Create and save the required users
        User instructorUser = new User(1L, "youssef", passwordEncoder.encode("password"), "youssef@gmail.com", UserRole.INSTRUCTOR, null, null);
        userRepository.save(instructorUser);

        User adminUser = new User(3L, "mohamed", passwordEncoder.encode("password"), "mohamed@gmail.com", UserRole.ADMIN, null, null);
        userRepository.save(adminUser);

        User studentUser = new User(2L, "mazen", passwordEncoder.encode("password"), "mazen@gmail.com", UserRole.STUDENT, null, null);
        userRepository.save(studentUser);

        // Create and save a lesson
        lessonRepository.save("CS001", "Java Programming Language", 1L);
    }

    @Test
    void testGenerateOtpAsInstructor() {
        String token = jwtService.generateToken("youssef");

        Attendance attendance = new Attendance(1L, null, null);
        HttpHeaders headers = new HttpHeaders();
        headers.set("User-Role", "INSTRUCTOR");
        headers.set("Authorization", "Bearer " + token);
        headers.set("Content-Type", "application/json");
        HttpEntity<Attendance> request = new HttpEntity<>(attendance, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(baseUrl + "/generate-otp", request, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains("OTP generated successfully");
    }

    @Test
    void testGenerateOtpAsAdmin() {
        String token = jwtService.generateToken("mohamed");

        Attendance attendance = new Attendance(1L, null, null);
        HttpHeaders headers = new HttpHeaders();
        headers.set("User-Role", "ADMIN");
        headers.set("Authorization", "Bearer " + token);
        headers.set("Content-Type", "application/json");
        HttpEntity<Attendance> request = new HttpEntity<>(attendance, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(baseUrl + "/generate-otp", request, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).contains("Only instructors can generate OTP.");
    }

    @Test
    void testSubmitOtpAsStudent() {
        String token = jwtService.generateToken("youssef");

        // Generate OTP first
        Attendance attendance = new Attendance(1L, null, null);
        HttpHeaders headers = new HttpHeaders();
        headers.set("User-Role", "INSTRUCTOR");
        headers.set("Authorization", "Bearer " + token);
        headers.set("Content-Type", "application/json");
        HttpEntity<Attendance> generateOtpRequest = new HttpEntity<>(attendance, headers);
        restTemplate.postForEntity(baseUrl + "/generate-otp", generateOtpRequest, String.class);

        // Submit OTP
        token = jwtService.generateToken("mazen");
        String otp = attendanceRepository.getOtpForLesson(1L);
        Attendance submitAttendance = new Attendance(1L, 2L, otp);
        headers.set("User-Role", "STUDENT");
        headers.set("Authorization", "Bearer " + token);
        HttpEntity<Attendance> submitOtpRequest = new HttpEntity<>(submitAttendance, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(baseUrl + "/submit-otp", submitOtpRequest, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo("Attendance marked successfully.");
    }

    @Test
    void testGetMarkedAttendancesAsInstructor() {
        String token = jwtService.generateToken("youssef");

        HttpHeaders headers = new HttpHeaders();
        headers.set("User-Role", "INSTRUCTOR");
        headers.set("Authorization", "Bearer " + token);
        HttpEntity<Void> request = new HttpEntity<>(headers);

        ResponseEntity<Attendance[]> response = restTemplate.exchange(baseUrl + "/lesson/1/marked", HttpMethod.GET, request, Attendance[].class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
    }

    @Test
    void testGetMarkedAttendancesAsAdmin() {
        String token = jwtService.generateToken("mohamed");

        HttpHeaders headers = new HttpHeaders();
        headers.set("User-Role", "ADMIN");
        headers.set("Authorization", "Bearer " + token);
        HttpEntity<Void> request = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(baseUrl + "/lesson/1/marked", HttpMethod.GET, request, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).contains("Only instructors can view attendance records.");
    }

    @Test
    void testGetMarkedAttendancesAsStudent() {
        String token = jwtService.generateToken("mazen");

        HttpHeaders headers = new HttpHeaders();
        headers.set("User-Role", "STUDENT");
        headers.set("Authorization", "Bearer " + token);
        HttpEntity<Void> request = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(baseUrl + "/lesson/1/marked", HttpMethod.GET, request, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).contains("Only instructors can view attendance records.");
    }
}