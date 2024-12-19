package com.example.demo.service;

import com.example.demo.models.Course;
import com.example.demo.models.Lesson;
import com.example.demo.models.User;
import com.example.demo.models.UserRole;
import com.example.demo.repository.CourseRepository;
import com.example.demo.repository.LessonRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Optional;

@Service
public class LessonService {

    @Autowired
    private final LessonRepository lessonRepository ;
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;


    public LessonService(LessonRepository lessonRepository, CourseRepository courseRepository , UserRepository userRepository ) {
        this.lessonRepository = lessonRepository;
        this.courseRepository = courseRepository;
        this.userRepository = userRepository;
    }

    public void createLesson(String courseId, String title, UserRole role, Long instructorId) {
        if (!isInstructor(role)) {
            throw new IllegalArgumentException("Only Instructors can create lessons.");
        }

        if (lessonRepository.existsByTitleAndCourseId(title, courseId)) {
            throw new IllegalArgumentException("A lesson with this title already exists in the course.");
        }

        // Check if the instructor exists
        if (!userRepository.existsById(instructorId)) {
            throw new IllegalArgumentException("Instructor not found.");
        }

        // Check if the course exists
        Optional<Course> courseOptional = courseRepository.findById(courseId);
        if (courseOptional.isEmpty()) {
            throw new IllegalArgumentException("Course not found.");
        }

        // Save the lesson using the parameters
        Lesson lesson = lessonRepository.save(courseId, title, instructorId);

        // Add the lesson to the course
        Course course = courseOptional.get();
        course.getLessons().add(lesson);
        courseRepository.save(course);
    }

    public Collection<Lesson> listAllLesson() {
        return lessonRepository.findAll();
    }
    private boolean isInstructor(UserRole role) {
        return role == UserRole.INSTRUCTOR;
    }
}
