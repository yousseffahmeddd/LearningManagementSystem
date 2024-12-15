package com.example.demo.service;

import com.example.demo.models.*;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class MockDatabase {
    private final Map<String, User> users = new HashMap<>();

    public MockDatabase() {

    }

    public User findByUserName(String userName) {
        return users.get(userName);
    }
}
