package com.example.examsystemproject;

public class ExamResult {
    private final String examId;
    private final String examName;
    private final String studentUsername;
    private final int score;
    private final int totalMarks;

    public ExamResult(String examId, String examName, String studentUsername, int score, int totalMarks) {
        this.examId = examId;
        this.examName = examName;
        this.studentUsername = studentUsername;
        this.score = score;
        this.totalMarks = totalMarks;
    }

    public String getExamId() {
        return examId;
    }

    public String getExamName() {
        return examName;
    }

    public String getStudentUsername() {
        return studentUsername;
    }

    public int getScore() {
        return score;
    }

    public int getTotalMarks() {
        return totalMarks;
    }
}
