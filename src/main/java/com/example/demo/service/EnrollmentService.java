package com.example.demo.service;

import com.example.demo.models.*;
import com.example.demo.repository.CourseRepository;
import com.example.demo.repository.EnrollmentRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EnrollmentService {
    private final EnrollmentRepository enrollmentRepository;
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;

    @Autowired
    public EnrollmentService(EnrollmentRepository enrollmentRepository, CourseRepository courseRepository, UserRepository userRepository) {
        this.enrollmentRepository = enrollmentRepository;
        this.courseRepository = courseRepository;
        this.userRepository = userRepository;
    }

    public void enrollStudent(String courseId, Long studentId, UserRole role) {
        if (role != UserRole.STUDENT) {
            throw new IllegalArgumentException("Only students can enroll in a course.");
        }

        Optional<Course> courseOptional = courseRepository.findById(courseId);
        if (courseOptional.isEmpty()) {
            throw new IllegalArgumentException("Course not found.");
        }

        Optional<User> studentOptional = userRepository.findById(studentId);
        if (studentOptional.isEmpty()) {
            throw new IllegalArgumentException("Student not found.");
        }

        User student = studentOptional.get();
        if (student.getRole() != UserRole.STUDENT) {
            throw new IllegalArgumentException("Only students can enroll in a course.");
        }

        Course course = courseOptional.get();
        if (enrollmentRepository.existsByCourseIdAndStudentId(courseId, studentId)) {
            throw new IllegalArgumentException("Student is already enrolled in this course.");
        }

        // Add student to the course
        course.getStudents().add(student);
        courseRepository.save(course);

        // Add course to the student's list of courses
        CourseDTO courseInfo = new CourseDTO(course.getId(), course.getTitle());
        student.getCourses().add(courseInfo);
        userRepository.save(student);

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
