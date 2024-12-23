package com.example.demo.contollers;

import com.example.demo.models.Attendance;
import com.example.demo.models.UserRole;
import com.example.demo.service.AttendanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/attendance")
public class AttendanceController {

    private final AttendanceService attendanceService;

    @Autowired
    public AttendanceController(AttendanceService attendanceService) {
        this.attendanceService = attendanceService;
    }

    // Generate OTP for a lesson
    @PostMapping("/generate-otp")
    public ResponseEntity<String> generateOtp(@RequestBody Attendance attendance,
                                              @RequestHeader("User-Role") String role) {
        try {
            UserRole userRole = UserRole.valueOf(role.toUpperCase());
            String otp = attendanceService.generateOtp(attendance.getLessonId(), userRole);
            return ResponseEntity.ok("OTP generated successfully: " + otp);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(400).body(e.getMessage());
        }
    }

    // Submit OTP to mark attendance
    // Submit OTP to mark attendance
    @PostMapping("/submit-otp")
    public ResponseEntity<String> submitOtp(@RequestBody Attendance attendance,
                                            @RequestHeader("User-Role") String role) {
        try {
            // Validate input - ensure all required fields are provided
            if (attendance.getLessonId() == null || attendance.getStudentId() == null || attendance.getOtp() == null) {
                return ResponseEntity.status(400).body("Lesson ID, Student ID, and OTP cannot be null.");
            }

            // Convert role from header to UserRole enum
            UserRole userRole = UserRole.valueOf(role.toUpperCase());

            // Check if the user is a student
            if (userRole != UserRole.STUDENT) {
                return ResponseEntity.status(403).body("Only students can submit OTP.");
            }

            // Validate OTP and mark attendance
            boolean isMarked = attendanceService.validateAndMarkAttendance(
                    attendance.getLessonId(), attendance.getStudentId(), attendance.getOtp(), userRole);

            if (isMarked) {
                return ResponseEntity.ok("Attendance marked successfully.");
            } else {
                return ResponseEntity.status(400).body("Invalid OTP or attendance already marked.");
            }

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(400).body(e.getMessage());
        }
    }


    // Get all marked attendances for a lesson
    @GetMapping("/lesson/{lessonId}/marked")
    public ResponseEntity<?> getMarkedAttendances(@PathVariable Long lessonId,
                                                  @RequestHeader("User-Role") String role) {
        try {
            UserRole userRole = UserRole.valueOf(role.toUpperCase());
            List<Attendance> attendances = attendanceService.getMarkedAttendances(lessonId, userRole);
            return ResponseEntity.ok(attendances);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(400).body(e.getMessage());
        }
    }
}