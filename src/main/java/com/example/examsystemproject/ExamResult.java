package com.example.examsystemproject;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class ExamResult {
    private final String examId;
    private final String examName;
    private final String studentUsername;
    private final int score;
    private final int totalMarks;
    private final Map<String, String> submittedAnswers;

    public ExamResult(String examId, String examName, String studentUsername, int score, int totalMarks) {
        this(examId, examName, studentUsername, score, totalMarks, Collections.emptyMap());
    }

    public ExamResult(String examId, String examName, String studentUsername, int score, int totalMarks,
                      Map<String, String> submittedAnswers) {
        this.examId = examId;
        this.examName = examName;
        this.studentUsername = studentUsername;
        this.score = score;
        this.totalMarks = totalMarks;
        this.submittedAnswers = submittedAnswers == null
                ? Collections.emptyMap()
                : Collections.unmodifiableMap(new LinkedHashMap<>(submittedAnswers));
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

    public Map<String, String> getSubmittedAnswers() {
        return submittedAnswers;
    }
}
