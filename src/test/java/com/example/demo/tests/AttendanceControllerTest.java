package com.example.demo.tests;

import com.example.demo.models.Attendance;
import com.example.demo.models.Lesson;
import com.example.demo.models.User;
import com.example.demo.models.UserRole;
import com.example.demo.repository.AttendanceRepository;
import com.example.demo.repository.LessonRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.AttendanceService;
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
    private AttendanceService attendanceService;

    @Autowired
    private JWTService jwtService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private String baseUrl;

    @BeforeEach
    void setUp() {
        baseUrl = "http://localhost:" + port + "/api/attendance";

        // Create and save the required users
        User instructorUser = new User(1L, "youssef", passwordEncoder.encode("password"), "youssef@gmail.com", UserRole.INSTRUCTOR, null, null, null);
        User studentUser = new User(2L, "mazen", passwordEncoder.encode("password"), "mazen@gmail.com", UserRole.STUDENT, null, null, null);
        userRepository.save(instructorUser);
        userRepository.save(studentUser);

        // Create and save a lesson for testing
        lessonRepository.save("CS001", "Java Programming", 1L);
    }

    @Test
    void testGenerateOtp() {
        String token = jwtService.generateToken("youssef");

        Lesson lesson = lessonRepository.findById(1L).orElseThrow();
        Attendance attendance = new Attendance(lesson, null, null);

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
    void testSubmitOtp() {
        String token = jwtService.generateToken("mazen");

        Lesson lesson = lessonRepository.findById(1L).orElseThrow();
        String otp;
        if (!attendanceRepository.otpExists(lesson)) {
            otp = attendanceService.generateOtp(lesson.getId(), lesson.getCourseId(), UserRole.INSTRUCTOR);
        } else {
            otp = attendanceRepository.getOtpForLesson(lesson);
        }

        Attendance attendance = new Attendance();
        attendance.setLessonId(lesson.getId());
        attendance.setStudentId(2L);
        attendance.setOtp(otp);

        HttpHeaders headers = new HttpHeaders();
        headers.set("User-Role", "STUDENT");
        headers.set("Authorization", "Bearer " + token);
        headers.set("Content-Type", "application/json");
        HttpEntity<Attendance> request = new HttpEntity<>(attendance, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(baseUrl + "/submit-otp", request, String.class);

        System.out.println("Response Status Code: " + response.getStatusCode());
        System.out.println("Response Body: " + response.getBody());

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo("Attendance marked successfully.");
    }

    @Test
    void testGetMarkedAttendances() {
        String token = jwtService.generateToken("youssef");

        HttpHeaders headers = new HttpHeaders();
        headers.set("User-Role", "INSTRUCTOR");
        headers.set("Authorization", "Bearer " + token);
        HttpEntity<Void> request = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(baseUrl + "/lesson/1/marked", HttpMethod.GET, request, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }
}