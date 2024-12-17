package com.example.demo.service;

import com.example.demo.models.Enrollment;
import com.example.demo.models.UserRole;
import com.example.demo.repository.EnrollmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EnrollmentService {
    private final EnrollmentRepository enrollmentRepository;

    @Autowired
    public EnrollmentService(EnrollmentRepository enrollmentRepository) {
        this.enrollmentRepository = enrollmentRepository;
    }

    // Enroll a student in a course
    public void enrollStudent(String courseId, Long studentId, UserRole role) {
        // Validate that the role is STUDENT
        if (isInstructor(role)) {
            throw new IllegalArgumentException("Only students or admins can enroll in a course.");
        }

        // Check if the student is already enrolled in the course
        if (enrollmentRepository.existsByCourseIdAndStudentId(courseId, studentId)) {
            throw new IllegalArgumentException("Student is already enrolled in this course.");
        }

        // Save the enrollment
        enrollmentRepository.save(courseId, studentId);
    }
    // Get all enrollments for a specific course
    public List<Enrollment> getEnrollmentsByCourseId(String courseId) {
        return enrollmentRepository.findByCourseId(courseId);
    }

    // Get all enrollments
    public List<Enrollment> getAllEnrollments(UserRole role) {
        if (!isInstructor(role)) {
            throw new IllegalArgumentException("Only instructors can view course enrollments.");
        }
        return enrollmentRepository.findAll();
    }

    private boolean isInstructor(UserRole role) {
        return role == UserRole.INSTRUCTOR;
    }
}
