package com.example.demo.repository;
import com.example.demo.models.Attendance;

import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class AttendanceRepository {

    private final List<Attendance> attendanceRecords = new ArrayList<>();

    // Save OTP for a lesson
    public void saveOtp(Long lessonId, String otp) {
        boolean otpExists = attendanceRecords.stream()
                .anyMatch(attendance -> attendance.getLessonId().equals(lessonId) && attendance.getOtp() != null);

        if (!otpExists) {
            Attendance lessonOtp = new Attendance(lessonId, null, otp); // studentId is null here
            attendanceRecords.add(lessonOtp);
        }
    }
    // Get OTP for a lesson
    public String getOtpForLesson(Long lessonId) {
        return attendanceRecords.stream()
                .filter(attendance -> attendance.getLessonId().equals(lessonId) && attendance.getOtp() != null)
                .map(Attendance::getOtp)
                .findFirst()
                .orElse(null);  // Returns null if no OTP is found for the lesson
    }

    // Check if OTP exists for a specific lessonId
    public boolean otpExists(Long lessonId) {
        return attendanceRecords.stream()
                .anyMatch(attendance -> attendance.getLessonId().equals(lessonId) && attendance.getOtp() != null);
    }

    // Validate OTP for a specific lesson
    public boolean validateOtp(Long lessonId, String otp) {
        return attendanceRecords.stream()
                .anyMatch(attendance -> attendance.getLessonId().equals(lessonId) &&
                        otp.equals(attendance.getOtp()));
    }

    // Mark attendance for a student
    public boolean markAttendance(Long lessonId, Long studentId) {
        // Check if OTP exists for the lesson
        String lessonOtp = attendanceRecords.stream()
                .filter(attendance -> attendance.getLessonId().equals(lessonId) && attendance.getOtp() != null)
                .map(Attendance::getOtp)
                .findFirst()
                .orElse(null);

        if (lessonOtp != null) {
            // Add a new record for the student with the same OTP
            Attendance studentAttendance = new Attendance(lessonId, studentId, lessonOtp);
            studentAttendance.setMarked(true);
            attendanceRecords.add(studentAttendance);
            return true;
        }
        return false; // Lesson OTP does not exist
    }

    // Get all marked attendances for a specific lesson
    public List<Attendance> getMarkedAttendancesByLessonId(Long lessonId) {
        return attendanceRecords.stream()
                .filter(attendance -> attendance.getLessonId().equals(lessonId) && attendance.isMarked())
                .collect(Collectors.toList());
    }

    // Get all attendance records
    public List<Attendance> findAll() {
        return new ArrayList<>(attendanceRecords);
    }
}
