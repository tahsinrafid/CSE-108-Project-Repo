package com.example.examsystemproject;

public class QnaPost {
    private final String postId;
    private final String questionText;
    private final String authorUsername;
    private final String authorRole;
    private final String visibility;
    private final String assignedTeacherUsername;
    private final String answerText;
    private final String answeredBy;

    public QnaPost(String postId, String questionText, String authorUsername, String authorRole, String visibility, String assignedTeacherUsername, String answerText, String answeredBy) {
        this.postId = postId;
        this.questionText = questionText;
        this.authorUsername = authorUsername;
        this.authorRole = authorRole;
        this.visibility = visibility;
        this.assignedTeacherUsername = assignedTeacherUsername;
        this.answerText = answerText;
        this.answeredBy = answeredBy;
    }

    public String getPostId() {
        return postId;
    }

    public String getQuestionText() {
        return questionText;
    }

    public String getAuthorUsername() {
        return authorUsername;
    }

    public String getAuthorRole() {
        return authorRole;
    }

    public String getVisibility() {
        return visibility;
    }

    public String getAssignedTeacherUsername() {
        return assignedTeacherUsername;
    }

    public String getAnswerText() {
        return answerText;
    }

    public String getAnsweredBy() {
        return answeredBy;
    }

    public boolean isAnswered() {
        return answerText != null && !answerText.trim().isEmpty();
    }
}
