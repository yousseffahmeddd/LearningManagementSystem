package com.example.demo.repository;

import com.example.demo.models.User;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Repository
public class UserRepository {

    // Using a HashMap to store users by ID
    private final Map<Long, User> userMap = new HashMap<>();

    // Save a user by ID
    public void save(User user) {
        userMap.put(user.getId(), user);
    }

    // Find a user by ID
    public Optional<User> findById(Long id) {
        return Optional.ofNullable(userMap.get(id));
    }

    // Find a user by username
    public User findByUsername(String username) {
        return userMap.values()
                .stream()
                .filter(user -> user.getUsername().equals(username))
                .findFirst()
                .orElse(null);
    }

    // Find a user by email
    public Optional<User> findByEmail(String email) {
        return userMap.values()
                .stream()
                .filter(user -> user.getEmail().equals(email))
                .findFirst();
    }

    // Get all users
    public Collection<User> findAll() {
        return userMap.values();
    }

    // Delete a user by ID
    public void deleteById(Long id) {
        userMap.remove(id);
    }

    // Check if a user exists by ID
    public boolean existsById(Long id) {
        return userMap.containsKey(id);
    }

    // Check if a user exists by email
    public boolean existsByEmail(String email) {
        return userMap.values()
                .stream()
                .anyMatch(user -> user.getEmail().equals(email));
    }

    public void deleteAll() {
        userMap.clear();
    }
}
