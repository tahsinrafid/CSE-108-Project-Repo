package com.example.examsystemproject;

public class QuestionReport {
    private final String questionId;
    private final String username;
    private final String details;

    public QuestionReport(String questionId, String username, String details) {
        this.questionId = questionId;
        this.username = username;
        this.details = details;
    }

    public String getQuestionId() {
        return questionId;
    }

    public String getUsername() {
        return username;
    }

    public String getDetails() {
        return details;
    }
}
