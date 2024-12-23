package com.example.demo.repository;

import com.example.demo.models.Question;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class QuestionRepository {
    private final Map<String, Question> questions = new HashMap<>();

    public Question save(Question question) {
        questions.put(question.getId(), question);
        return question;
    }
    public boolean existsById(String id) {
        return questions.containsKey(id);
    }

}
