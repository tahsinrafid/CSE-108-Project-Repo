package com.example.examsystemproject;

public class QnaComment {
    private final String authorUsername;
    private final String authorRole;
    private final String commentText;

    public QnaComment(String authorUsername, String authorRole, String commentText) {
        this.authorUsername = authorUsername;
        this.authorRole = authorRole;
        this.commentText = commentText;
    }

    public String getAuthorUsername() {
        return authorUsername;
    }

    public String getAuthorRole() {
        return authorRole;
    }

    public String getCommentText() {
        return commentText;
    }

    public boolean isTeacher() {
        return "teacher".equalsIgnoreCase(authorRole);
    }
}

