package com.example.examsystemproject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class QnaFileManager {
    private static final String FILE_PATH = "qna_posts";

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

                String[] parts = line.split(",", 8);
                if (parts.length != 7 && parts.length != 8) {
                    continue;
                }

                if (parts.length == 7) {
                    posts.add(new QnaPost(parts[0], parts[1], parts[2], parts[3], parts[4], "", parts[5], parts[6]));
                } else {
                    posts.add(new QnaPost(parts[0], parts[1], parts[2], parts[3], parts[4], parts[5], parts[6], parts[7]));
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
        return sanitize(post.getPostId()) + ","
                + sanitize(post.getQuestionText()) + ","
                + sanitize(post.getAuthorUsername()) + ","
                + sanitize(post.getAuthorRole()) + ","
                + sanitize(post.getVisibility()) + ","
            + sanitize(post.getAssignedTeacherUsername()) + ","
            + sanitize(post.getAnswerText()) + ","
                + sanitize(post.getAnsweredBy());
    }

    private static String sanitize(String value) {
        return value == null ? "" : value.replace(",", " ").trim();
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
