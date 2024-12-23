package com.example.demo.service;
import com.example.demo.models.Notification;
import com.example.demo.models.User;
import com.example.demo.repository.NotificationRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;

    private final UserRepository userRepository;

    @Autowired
    public NotificationService(NotificationRepository notificationRepository , UserRepository userRepository) {
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
    }

    // Generate a unique notification ID
    private Long generateNotificationId(List<Notification> notifications) {
        return notifications.isEmpty() ? 1L : notifications.get(notifications.size() - 1).getId() + 1;
    }

    // Create a new notification
    public Notification createNotification(Long userId, String message) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Notification notification = new Notification();
        notification.setMessage(message);
        notification.setTimestamp(LocalDateTime.now());
        notification.setRead(false);
        notification.setUserId(userId);

        // Add notification to the repository and user's list
        notificationRepository.save(notification);
        user.getNotifications().add(notification);
        userRepository.save(user);

        return notification;
    }


    // Retrieve all notifications for a user
    public List<Notification> getNotificationsByUserId(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        return user.getNotifications();
    }


    // Retrieve unread notifications for a user
    public List<Notification> getUnreadNotifications(Long userId) {
        return getNotificationsByUserId(userId).stream()
                .filter(notification -> !notification.isRead())
                .toList();
    }

    // Mark a notification as read
    public void markAsRead(Long userId, Long notificationId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Notification notification = user.getNotifications()
                .stream()
                .filter(n -> n.getId().equals(notificationId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Notification not found"));

        notification.setRead(true);
        notificationRepository.save(notification);
    }


    // Delete all notifications for a user
    public void deleteAllNotifications(Long userId) {
        // Find user by ID
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // Clear the user's notifications list
        user.getNotifications().clear();

        // Remove notifications from repository
        notificationRepository.deleteByUserId(userId);

        // Save the user to persist changes
        userRepository.save(user);
    }


    public void deleteNotification(Long userId, Long notificationId) {
        // Find user by ID
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // Find the specific notification
        Notification notification = user.getNotifications()
                .stream()
                .filter(n -> n.getId().equals(notificationId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Notification not found"));

        // Remove the notification from the user's list
        user.getNotifications().remove(notification);

        // Delete the notification from the repository by ID
        notificationRepository.deleteById(userId, notificationId);

        // Save the user to persist changes
        userRepository.save(user);
    }


}