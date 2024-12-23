package com.example.demo.repository;
import com.example.demo.models.Attendance;

import com.example.demo.models.Lesson;
import com.example.demo.models.User;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Repository
public class AttendanceRepository {

    private final List<Attendance> attendanceRecords = new ArrayList<>();

    public void saveOtp(Lesson lesson, User instructor, String otp) {
        if (lesson == null) {
            throw new IllegalArgumentException("Lesson cannot be null.");
        }

        boolean otpExists = attendanceRecords.stream()
                .anyMatch(record -> record.getLessonId().equals(lesson.getId()) && record.getOtp().equals(otp));

        if (!otpExists) {
            Attendance lessonOtp = new Attendance(lesson, null, otp);
            attendanceRecords.add(lessonOtp);
        }
    }


    public boolean otpExists(Lesson lesson) {
        return attendanceRecords.stream()
                .anyMatch(record -> record.getLessonId().equals(lesson.getId()) && !record.isMarked());
    }

    public String getOtpForLesson(Lesson lesson) {
        return attendanceRecords.stream()
                .filter(record -> record.getLessonId().equals(lesson.getId()) && !record.isMarked())
                .map(Attendance::getOtp)
                .findFirst()
                .orElse(null);
    }

    public boolean validateOtp(Lesson lesson, String otp) {
        return attendanceRecords.stream()
                .anyMatch(record -> record.getLessonId().equals(lesson.getId()) && record.getOtp().equals(otp) && !record.isMarked());
    }

/*
    public boolean markAttendance(Lesson lesson, User student, String otp) {
        // Validate OTP
        boolean isValidOtp = attendanceRecords.stream()
                .anyMatch(record -> record.getLessonId().equals(lesson.getId())
                        && record.getOtp().equals(otp)
                        && !record.isMarked());
        if (!isValidOtp) {
            return false;
        }

        // Create a new attendance record for the student
        Attendance newAttendance = new Attendance(lesson, student, otp);
        newAttendance.setStudentId(student.getId());
        newAttendance.setMarked(true);

        attendanceRecords.add(newAttendance);
        return true;
    }
*/

    public boolean markAttendance(Lesson lesson, User student, String otp) {
        if (lesson == null || student == null || otp == null) {
            throw new IllegalArgumentException("Lesson, Student, and OTP cannot be null.");
        }

        // Check if the student has already marked attendance for this lesson
        boolean isAlreadyMarked = attendanceRecords.stream()
                .anyMatch(record -> record.getLessonId().equals(lesson.getId())
                        && record.getStudentId() != null
                        && record.getStudentId().equals(student.getId())
                        && record.isMarked());
        if (isAlreadyMarked) {
            return false;
        }

        // Validate OTP
        boolean isValidOtp = attendanceRecords.stream()
                .anyMatch(record -> record.getLessonId().equals(lesson.getId())
                        && record.getOtp().equals(otp)
                        && !record.isMarked());
        if (!isValidOtp) {
            return false;
        }

        // Mark attendance
        Attendance newAttendance = new Attendance(lesson, student, otp);
        newAttendance.setStudentId(student.getId());
        newAttendance.setMarked(true);

        attendanceRecords.add(newAttendance);
        return true;
    }



    public List<Attendance> getMarkedAttendancesByLesson(Lesson lesson) {
        return attendanceRecords.stream()
                .filter(record -> record.getLessonId().equals(lesson.getId()) && record.isMarked())
                .toList();
    }

    public List<Attendance> findAll() {
        return new ArrayList<>(attendanceRecords);
    }
}