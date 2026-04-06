package com.example.examsystemproject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class QnaFileManager {
    private static final String FILE_PATH = "qna_posts";
    private static final String V2_PREFIX = "V2";

    public static void addPost(QnaPost post) throws IOException {
        File file = new File(FILE_PATH);
        boolean fileExists = file.exists() && file.length() > 0;

        try (FileWriter writer = new FileWriter(file, true)) {
            if (fileExists) {
                writer.write("\n");
            }
            writer.write(formatLine(post));
        }
    }

    public static List<QnaPost> loadAllPosts() throws IOException {
        List<QnaPost> posts = new ArrayList<>();
        File file = new File(FILE_PATH);
        if (!file.exists()) {
            return posts;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) {
                    continue;
                }

                QnaPost parsed = parseLine(line);
                if (parsed != null) {
                    posts.add(parsed);
                }
            }
        }

        return posts;
    }

    public static void updatePost(QnaPost updated) throws IOException {
        List<QnaPost> posts = loadAllPosts();
        for (int i = 0; i < posts.size(); i++) {
            if (posts.get(i).getPostId().equals(updated.getPostId())) {
                posts.set(i, updated);
                break;
            }
        }
        rewrite(posts);
    }

    private static void rewrite(List<QnaPost> posts) throws IOException {
        File file = new File(FILE_PATH);
        try (FileWriter writer = new FileWriter(file, false)) {
            for (int i = 0; i < posts.size(); i++) {
                if (i > 0) {
                    writer.write("\n");
                }
                writer.write(formatLine(posts.get(i)));
            }
        }
    }

    public static void addComment(String postId, QnaComment comment) throws IOException {
        if (postId == null || postId.trim().isEmpty() || comment == null) {
            return;
        }

        List<QnaPost> posts = loadAllPosts();
        for (int i = 0; i < posts.size(); i++) {
            QnaPost post = posts.get(i);
            if (postId.equalsIgnoreCase(post.getPostId())) {
                posts.set(i, post.withComment(comment));
                rewrite(posts);
                return;
            }
        }
    }

    public static boolean deleteComment(String postId, int commentIndex) throws IOException {
        if (postId == null || postId.trim().isEmpty() || commentIndex < 0) {
            return false;
        }

        List<QnaPost> posts = loadAllPosts();
        for (int i = 0; i < posts.size(); i++) {
            QnaPost post = posts.get(i);
            if (!postId.equalsIgnoreCase(post.getPostId())) {
                continue;
            }

            if (commentIndex >= post.getCommentCount()) {
                return false;
            }

            posts.set(i, post.withCommentRemovedAt(commentIndex));
            rewrite(posts);
            return true;
        }

        return false;
    }

    public static String nextSequentialPostId() throws IOException {
        List<QnaPost> posts = loadAllPosts();
        int max = 0;
        for (QnaPost post : posts) {
            int id = extractSequentialId(post.getPostId());
            if (id > max) {
                max = id;
            }
        }
        return "P" + (max + 1);
    }

    private static String formatLine(QnaPost post) {
        return V2_PREFIX + "|"
                + encode(post.getPostId()) + "|"
                + encode(post.getQuestionText()) + "|"
                + encode(post.getAuthorUsername()) + "|"
                + encode(post.getAuthorRole()) + "|"
                + encode(serializeComments(post.getComments()));
    }

    private static QnaPost parseLine(String line) {
        if (line.startsWith(V2_PREFIX + "|")) {
            String[] parts = line.split("\\|", 6);
            if (parts.length < 5) {
                return null;
            }

            String commentsPayload = parts.length >= 6 ? decode(parts[5]) : "";
            return new QnaPost(
                    decode(parts[1]),
                    decode(parts[2]),
                    decode(parts[3]),
                    decode(parts[4]),
                    deserializeComments(commentsPayload));
        }

        // Legacy format migration support:
        // id,question,author,role,visibility,assignedTeacher,answer,answeredBy
        String[] parts = line.split(",", 8);
        if (parts.length != 7 && parts.length != 8) {
            return null;
        }

        String postId = safeLegacy(parts, 0);
        String questionText = safeLegacy(parts, 1);
        String authorUsername = safeLegacy(parts, 2);
        String authorRole = safeLegacy(parts, 3);
        String answerText = parts.length == 7 ? safeLegacy(parts, 5) : safeLegacy(parts, 6);
        String answeredBy = parts.length == 7 ? safeLegacy(parts, 6) : safeLegacy(parts, 7);

        List<QnaComment> comments = new ArrayList<>();
        if (!answerText.isEmpty()) {
            String commentAuthor = answeredBy.isEmpty() ? "community" : answeredBy;
            String commentRole = inferRole(commentAuthor);
            comments.add(new QnaComment(commentAuthor, commentRole, answerText));
        }

        return new QnaPost(postId, questionText, authorUsername, authorRole, comments);
    }

    private static String safeLegacy(String[] parts, int index) {
        if (index < 0 || index >= parts.length || parts[index] == null) {
            return "";
        }
        return parts[index].trim();
    }

    private static String inferRole(String username) {
        if (username == null) {
            return "student";
        }
        String normalized = username.trim().toLowerCase();
        if (normalized.contains("teacher") || normalized.startsWith("sir") || normalized.startsWith("maam")) {
            return "teacher";
        }
        return "student";
    }

    private static String serializeComments(List<QnaComment> comments) {
        if (comments == null || comments.isEmpty()) {
            return "";
        }

        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < comments.size(); i++) {
            QnaComment comment = comments.get(i);
            if (i > 0) {
                builder.append("||");
            }
            builder.append(encode(comment.getAuthorUsername()))
                    .append("~")
                    .append(encode(comment.getAuthorRole()))
                    .append("~")
                    .append(encode(comment.getCommentText()));
        }
        return builder.toString();
    }

    private static List<QnaComment> deserializeComments(String payload) {
        List<QnaComment> comments = new ArrayList<>();
        if (payload == null || payload.trim().isEmpty()) {
            return comments;
        }

        String[] items = payload.split("\\|\\|");
        for (String item : items) {
            String[] fields = item.split("~", 3);
            if (fields.length != 3) {
                continue;
            }
            comments.add(new QnaComment(decode(fields[0]), decode(fields[1]), decode(fields[2])));
        }
        return comments;
    }

    private static String encode(String value) {
        String input = value == null ? "" : value.trim();
        return URLEncoder.encode(input, StandardCharsets.UTF_8);
    }

    private static String decode(String value) {
        String input = value == null ? "" : value;
        return URLDecoder.decode(input, StandardCharsets.UTF_8);
    }

    private static int extractSequentialId(String postId) {
        if (postId == null || postId.length() < 2) {
            return -1;
        }
        if (postId.charAt(0) != 'P' && postId.charAt(0) != 'p') {
            return -1;
        }
        try {
            return Integer.parseInt(postId.substring(1));
        } catch (NumberFormatException ex) {
            return -1;
        }
    }
}
