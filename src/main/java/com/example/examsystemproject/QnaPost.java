package com.example.examsystemproject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class QnaPost {
    private final String postId;
    private final String questionText;
    private final String authorUsername;
    private final String authorRole;
    private final List<QnaComment> comments;

    public QnaPost(String postId, String questionText, String authorUsername, String authorRole, List<QnaComment> comments) {
        this.postId = postId;
        this.questionText = questionText;
        this.authorUsername = authorUsername;
        this.authorRole = authorRole;
        this.comments = comments == null ? new ArrayList<>() : new ArrayList<>(comments);
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

    public List<QnaComment> getComments() {
        return Collections.unmodifiableList(comments);
    }

    public int getCommentCount() {
        return comments.size();
    }

    public QnaPost withComment(QnaComment comment) {
        List<QnaComment> next = new ArrayList<>(comments);
        next.add(comment);
        return new QnaPost(postId, questionText, authorUsername, authorRole, next);
    }

    public QnaPost withCommentRemovedAt(int commentIndex) {
        if (commentIndex < 0 || commentIndex >= comments.size()) {
            return this;
        }

        List<QnaComment> next = new ArrayList<>(comments);
        next.remove(commentIndex);
        return new QnaPost(postId, questionText, authorUsername, authorRole, next);
    }
}
