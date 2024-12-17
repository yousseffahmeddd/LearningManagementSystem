package com.example.demo.service;

import com.example.demo.models.Attendance;
import com.example.demo.models.UserRole;
import com.example.demo.repository.AttendanceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;

@Service
public class AttendanceService {

    private final AttendanceRepository attendanceRepository;

    @Autowired
    public AttendanceService(AttendanceRepository attendanceRepository) {
        this.attendanceRepository = attendanceRepository;
    }

    // Generate OTP for a lesson (Instructor only)
    public String generateOtp(Long lessonId, UserRole role) {
        if (lessonId == null || role == null) {
            throw new IllegalArgumentException("Lesson ID and Role cannot be null.");
        }
        if (!isInstructor(role)) {
            throw new IllegalArgumentException("Only instructors can generate OTP.");
        }

        // Check if OTP already exists for the lesson
        if (attendanceRepository.otpExists(lessonId)) {
            // If OTP exists, return the existing OTP and print a message
            String existingOtp = attendanceRepository.getOtpForLesson(lessonId);
            System.out.println("This lesson already has an OTP: " + existingOtp);
            return existingOtp;  // Return the existing OTP
        }

        // If OTP does not exist, generate a new one
        String otp = generateRandomOtp();
        attendanceRepository.saveOtp(lessonId, otp);
        return otp;
    }


    // Validate OTP and mark attendance for a student
    // Validate OTP and mark attendance for a student
    public boolean validateAndMarkAttendance(Long lessonId, Long studentId, String otp, UserRole role) {
        if (lessonId == null || studentId == null || otp == null || role == null) {
            throw new IllegalArgumentException("Lesson ID, Student ID, OTP, and Role cannot be null.");
        }

        // Check if the role is STUDENT
        if (!isStudent(role)) {
            throw new IllegalArgumentException("Only students can submit OTP.");
        }

        // Validate the OTP for the lesson
        boolean isValidOtp = attendanceRepository.validateOtp(lessonId, otp);
        if (!isValidOtp) {
            throw new IllegalArgumentException("Invalid OTP for the lesson.");
        }

        // Mark attendance for the student
        return attendanceRepository.markAttendance(lessonId, studentId);
    }

    private boolean isInstructor(UserRole role) {
        return role == UserRole.INSTRUCTOR;
    }

    private boolean isStudent(UserRole role) {
        return role == UserRole.STUDENT;
    }

    // Get all marked attendances for a specific lesson
    public List<Attendance> getMarkedAttendances(Long lessonId, UserRole role) {
        if (!isInstructor(role)) {
            throw new IllegalArgumentException("Only instructors can view attendance records.");
        }
        return attendanceRepository.getMarkedAttendancesByLessonId(lessonId);
    }

    // Generate a 6-digit random OTP
    private String generateRandomOtp() {
        Random random = new Random();
        int otp = 100000 + random.nextInt(900000);
        return String.valueOf(otp);
    }

}
