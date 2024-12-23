package com.example.demo.utility;

import com.example.demo.models.Attendance;
import com.example.demo.models.QuizAttempt;
import com.example.demo.models.Submission;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class ExcelUtil {

    public static void writeAttendancesToExcel(List<Attendance> attendances, String filePath) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Attendances");

        int rowNum = 0;
        Row headerRow = sheet.createRow(rowNum++);
        headerRow.createCell(0).setCellValue("Lesson ID");
        headerRow.createCell(1).setCellValue("Student ID");
        headerRow.createCell(2).setCellValue("OTP");

        for (Attendance attendance : attendances) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(attendance.getLessonId());
            row.createCell(1).setCellValue(attendance.getStudentId());
            row.createCell(2).setCellValue(attendance.getOtp());
        }

        try (FileOutputStream fileOut = new FileOutputStream(filePath)) {
            workbook.write(fileOut);
        }
        workbook.close();
    }

    public static void writeQuizAttemptsToExcel(List<QuizAttempt> quizAttempts, String filePath) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Quiz Attempts");

        int rowNum = 0;
        Row headerRow = sheet.createRow(rowNum++);
        headerRow.createCell(0).setCellValue("Quiz ID");
        headerRow.createCell(1).setCellValue("Student ID");
        headerRow.createCell(2).setCellValue("Grade");

        for (QuizAttempt attempt : quizAttempts) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(attempt.getQuizId());
            row.createCell(1).setCellValue(attempt.getStudentId());
            row.createCell(2).setCellValue(attempt.getGrade());
        }

        try (FileOutputStream fileOut = new FileOutputStream(filePath)) {
            workbook.write(fileOut);
        }
        workbook.close();
    }

    public static void writeSubmissionsToExcel(List<Submission> submissions, String filePath) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Submissions");

        int rowNum = 0;
        Row headerRow = sheet.createRow(rowNum++);
        headerRow.createCell(0).setCellValue("Assignment ID");
        headerRow.createCell(1).setCellValue("Student ID");
        headerRow.createCell(2).setCellValue("File Name");

        for (Submission submission : submissions) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(submission.getAssignmentId());
            row.createCell(1).setCellValue(submission.getStudentId());
            row.createCell(2).setCellValue(submission.getFileName());
        }

        try (FileOutputStream fileOut = new FileOutputStream(filePath)) {
            workbook.write(fileOut);
        }
        workbook.close();
    }
}