package com.example.demo.contollers;

import com.example.demo.models.Attendance;
import com.example.demo.models.UserRole;
import com.example.demo.service.AttendanceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

            if (attendance.getLesson() == null) {
                throw new IllegalArgumentException("Lesson must be provided in the request.");
            }

            String otp = attendanceService.generateOtp(attendance.getLesson().getId(),
                    attendance.getLesson().getCourseId(),
                    userRole);
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
        Logger logger = LoggerFactory.getLogger(AttendanceController.class);
        try {
            // Log the received values for debugging
            logger.info("Received Attendance: {}", attendance);
            logger.info("Lesson ID: {}", attendance.getLessonId());
            logger.info("Student ID: {}", attendance.getStudentId());
            logger.info("OTP: {}", attendance.getOtp());

            // Validate and mark attendance using the service
            boolean isMarked = attendanceService.validateAndMarkAttendance(
                    attendance.getLessonId(), attendance.getStudentId(), attendance.getOtp(), UserRole.valueOf(role.toUpperCase()));

            // Return success or failure message based on the result from the service
            if (isMarked) {
                return ResponseEntity.ok("Attendance marked successfully.");
            } else {
                return ResponseEntity.status(400).body("Invalid OTP or attendance already marked.");
            }

        } catch (IllegalArgumentException e) {
            // Return error message if validation fails
            return ResponseEntity.status(400).body("Error: " + e.getMessage());
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