package com.example.demo.models;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Notification {
    private Long id;
    private String message;
    private LocalDate timestamp;
    private boolean isRead;
    private Long userId; // Reference to the user this notification belongs to

    public Notification() {}

    public Notification(Long id, String message, LocalDate timestamp, boolean isRead, Long userId) {
        this.id = id;
        this.message = message;
        this.timestamp = timestamp;
        this.isRead = isRead;
        this.userId = userId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDate getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDate timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "Notification{" +
                "id=" + id +
                ", message='" + message + '\'' +
                ", timestamp=" + timestamp +
                ", isRead=" + isRead +
                ", userId=" + userId +
                '}';
    }
}