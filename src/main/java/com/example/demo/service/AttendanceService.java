package com.example.demo.service;

import com.example.demo.models.Attendance;
import com.example.demo.models.Lesson;
import com.example.demo.models.User;
import com.example.demo.models.UserRole;
import com.example.demo.repository.AttendanceRepository;
import com.example.demo.repository.LessonRepository;
import com.example.demo.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
public class AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final LessonRepository lessonRepository;
    private final UserRepository userRepository;

    @Autowired
    public AttendanceService(AttendanceRepository attendanceRepository, LessonRepository lessonRepository, UserRepository userRepository) {
        this.attendanceRepository = attendanceRepository;
        this.lessonRepository = lessonRepository;
        this.userRepository = userRepository;
    }

    public String generateOtp(Long lessonId, String courseId, UserRole role) {
        if (lessonId == null || courseId == null || role == null) {
            throw new IllegalArgumentException("Lesson ID, Course ID, and Role cannot be null.");
        }
        if (!isInstructor(role)) {
            throw new IllegalArgumentException("Only instructors can generate OTP.");
        }

        Optional<Lesson> lessonOptional = lessonRepository.findById(lessonId);
        if (lessonOptional.isEmpty()) {
            throw new IllegalArgumentException("Lesson not found.");
        }

        Lesson lesson = lessonOptional.get();
        if (!lesson.getCourseId().equals(courseId)) {
            throw new IllegalArgumentException("Course ID does not match the lesson.");
        }

        if (attendanceRepository.otpExists(lesson)) {
            String existingOtp = attendanceRepository.getOtpForLesson(lesson);
            throw new IllegalArgumentException("An OTP already exists for this lesson: " + existingOtp);
        }

        String otp = generateRandomOtp();
        Optional<User> instructorOptional = userRepository.findById(lesson.getInstructorId());
        if (instructorOptional.isEmpty()) {
            throw new IllegalArgumentException("Instructor not found.");
        }

        // CHANGED: Pass 'null' as the instructor to avoid setting 'studentId' prematurely
        attendanceRepository.saveOtp(lesson, null, otp);

        return otp;
    }

    public boolean validateAndMarkAttendance(Long lessonId, Long studentId, String otp, UserRole role) {
        if (lessonId == null || studentId == null || otp == null || role == null) {
            throw new IllegalArgumentException("Lesson ID, Student ID, OTP, and Role cannot be null.");
        }

        if (role != UserRole.STUDENT) {
            throw new IllegalArgumentException("Only students can submit OTP.");
        }

        Optional<User> userOptional = userRepository.findById(studentId);
        if (userOptional.isEmpty()) {
            throw new IllegalArgumentException("Student not found.");
        }

        User student = userOptional.get();
        if (student.getRole() != UserRole.STUDENT) {
            throw new IllegalArgumentException("The provided user ID does not belong to a student.");
        }

        Optional<Lesson> lessonOptional = lessonRepository.findById(lessonId);
        if (lessonOptional.isEmpty()) {
            throw new IllegalArgumentException("Lesson not found.");
        }

        Lesson lesson = lessonOptional.get();

        boolean isEnrolled = student.getCourses().stream()
                .anyMatch(course -> course.getId().equals(lesson.getCourseId()));
        if (!isEnrolled) {
            throw new IllegalArgumentException("Student is not enrolled in the course.");
        }

        // Validate the OTP and mark attendance
        boolean isMarked = attendanceRepository.markAttendance(lesson, student, otp);
        if (!isMarked) {
            throw new IllegalArgumentException("Invalid OTP or attendance already marked.");
        }
        return true;
    }



    private boolean isInstructor(UserRole role) {
        return role == UserRole.INSTRUCTOR;
    }

    private boolean isStudent(UserRole role) {
        return role == UserRole.STUDENT;
    }

    public List<Attendance> getMarkedAttendances(Long lessonId, UserRole role) {
        if (!isInstructor(role)) {
            throw new IllegalArgumentException("Only instructors can view attendance records.");
        }

        Optional<Lesson> lessonOptional = lessonRepository.findById(lessonId);
        if (lessonOptional.isEmpty()) {
            throw new IllegalArgumentException("Lesson not found.");
        }

        Lesson lesson = lessonOptional.get();
        return attendanceRepository.getMarkedAttendancesByLesson(lesson);
    }

    private String generateRandomOtp() {
        Random random = new Random();
        int otp = 100000 + random.nextInt(900000);
        return String.valueOf(otp);
    }
}
