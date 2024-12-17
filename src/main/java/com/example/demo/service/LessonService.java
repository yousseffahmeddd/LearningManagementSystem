package com.example.demo.service;

import com.example.demo.models.Lesson;
import com.example.demo.models.User;
import com.example.demo.models.UserRole;
import com.example.demo.repository.LessonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
public class LessonService {

    @Autowired
    private final LessonRepository lessonRepository ;

    public LessonService(LessonRepository repository) {
        this.lessonRepository = repository;
    }

    public void createLesson(String courseId, String title, UserRole role) {
        if (!isInstructor(role)) {
            throw new IllegalArgumentException("Only Instructors can create lessons.");
        }

        if (lessonRepository.existsByTitleAndCourseId(title, courseId)) {
            throw new IllegalArgumentException("A lesson with this title already exists in the course.");
        }

        lessonRepository.save(courseId, title);
    }
    public Collection<Lesson> listAllLesson() {
        return lessonRepository.findAll();
    }
    private boolean isInstructor(UserRole role) {
        return role == UserRole.INSTRUCTOR;
    }
}
