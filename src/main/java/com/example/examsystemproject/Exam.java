package com.example.examsystemproject;

import java.util.List;

public class Exam {
    private final String examId;
    private final String examName;
    private final String subject;
    private final int durationMinutes;
    private final List<String> questionIds;
    private final String createdBy;

    public Exam(String examId, String examName, String subject, int durationMinutes, List<String> questionIds, String createdBy) {
        this.examId = examId;
        this.examName = examName;
        this.subject = subject;
        this.durationMinutes = durationMinutes;
        this.questionIds = questionIds;
        this.createdBy = createdBy;
    }

    public String getExamId() {
        return examId;
    }

    public String getExamName() {
        return examName;
    }

    public String getSubject() {
        return subject;
    }

    public int getDurationMinutes() {
        return durationMinutes;
    }

    public List<String> getQuestionIds() {
        return questionIds;
    }

    public String getCreatedBy() {
        return createdBy;
    }
}

