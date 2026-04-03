package com.example.examsystemproject;

import java.util.List;

public class Exam {
    private final String examId;
    private final String examName;
    private final String subject;
    private final String classLevel;
    private final int durationMinutes;
    private final int marks;
    private final List<String> questionIds;
    private final String createdBy;

    public Exam(String examId, String examName, String subject, int durationMinutes, List<String> questionIds, String createdBy) {
        this(examId, examName, subject, "", durationMinutes, 0, questionIds, createdBy);
    }

    public Exam(String examId, String examName, String subject, String classLevel, int durationMinutes, int marks, List<String> questionIds, String createdBy) {
        this.examId = examId;
        this.examName = examName;
        this.subject = subject;
        this.classLevel = classLevel;
        this.durationMinutes = durationMinutes;
        this.marks = marks;
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

    public String getClassLevel() {
        return classLevel;
    }

    public int getDurationMinutes() {
        return durationMinutes;
    }

    public int getMarks() {
        return marks;
    }

    public List<String> getQuestionIds() {
        return questionIds;
    }

    public String getCreatedBy() {
        return createdBy;
    }
}

