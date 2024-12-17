package com.example.demo.contollers;

import com.example.demo.models.Lesson;
import com.example.demo.models.UserRole;
import com.example.demo.service.LessonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@RestController
@RequestMapping("/api/lessons")
public class LessonController {

    @Autowired
    private final LessonService lessonService ;


    public LessonController(LessonService lessonService) {
        this.lessonService = lessonService;
    }

    @PostMapping
    public ResponseEntity<String> createLesson(@RequestBody Lesson lesson ,
                                               @RequestHeader("User-Role") String role) {
        try {
            UserRole userRole = UserRole.valueOf(role.toUpperCase());
            lessonService.createLesson(lesson.getCourseId(), lesson.getTitle(), userRole);
            return ResponseEntity.ok("Lesson add successfully.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(400).body(e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<Collection<Lesson>> listAllLessons() {
        return ResponseEntity.ok(lessonService.listAllLesson());
    }
}
