package com.example.demo.repository;

import com.example.demo.models.Notification;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class NotificationRepository {

    private final HashMap<Long, List<Notification>> notificationStorage = new HashMap<>();

    // Save a notification
    /*public void save(Notification notification) {
        notificationStorage
                .computeIfAbsent(notification.getUserId(), k -> new ArrayList<>())
                .add(notification);
    }*/

    private final AtomicLong idSequence = new AtomicLong(0); // AtomicLong for thread-safe ID generation

    // Save a notification with automatic ID generation
    public void save(Notification notification) {
        if (notification.getId() == null) { // Generate ID only if it's not already set
            notification.setId(idSequence.incrementAndGet());
        }
        notificationStorage
                .computeIfAbsent(notification.getUserId(), k -> new ArrayList<>())
                .add(notification);
    }


    // Retrieve all notifications for a user
    public List<Notification> findByUserId(Long userId) {
        return notificationStorage.getOrDefault(userId, new ArrayList<>());
    }

    // Find a specific notification by ID for a user
    public Optional<Notification> findById(Long userId, Long notificationId) {
        return findByUserId(userId).stream()
                .filter(notification -> notification.getId().equals(notificationId))
                .findFirst();
    }

    // Delete all notifications for a user
    public void deleteByUserId(Long userId) {
        notificationStorage.remove(userId);
    }

    // Delete a specific notification
    public void deleteById(Long userId, Long notificationId) {
        List<Notification> userNotifications = notificationStorage.get(userId);
        if (userNotifications != null) {
            userNotifications.removeIf(notification -> notification.getId().equals(notificationId));
        }
    }


    // Delete all notifications (general method, not user-specific)
    public void deleteAll() {
        notificationStorage.clear();
    }
}
