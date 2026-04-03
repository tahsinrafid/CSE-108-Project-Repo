package com.example.examsystemproject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ExamResultFileManager {
    private static final String FILE_PATH = "exam_results";

    public static void addResult(ExamResult result) throws IOException {
        File file = new File(FILE_PATH);
        boolean fileExists = file.exists() && file.length() > 0;

        try (FileWriter writer = new FileWriter(file, true)) {
            if (fileExists) {
                writer.write("\n");
            }
            writer.write(formatLine(result));
        }
    }

    public static List<ExamResult> loadAllResults() throws IOException {
        List<ExamResult> results = new ArrayList<>();
        File file = new File(FILE_PATH);
        if (!file.exists()) {
            return results;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) {
                    continue;
                }

                String[] parts = line.split(",", 6);
                if (parts.length < 5) {
                    continue;
                }

                int score;
                int total;
                try {
                    score = Integer.parseInt(parts[3]);
                    total = Integer.parseInt(parts[4]);
                } catch (NumberFormatException ex) {
                    continue;
                }

                Map<String, String> submittedAnswers = parts.length >= 6
                        ? parseSubmittedAnswers(parts[5])
                        : new LinkedHashMap<>();

                results.add(new ExamResult(parts[0], parts[1], parts[2], score, total, submittedAnswers));
            }
        }

        return results;
    }

    private static String formatLine(ExamResult result) {
        return sanitize(result.getExamId()) + ","
                + sanitize(result.getExamName()) + ","
                + sanitize(result.getStudentUsername()) + ","
                + result.getScore() + ","
                + result.getTotalMarks() + ","
                + serializeSubmittedAnswers(result.getSubmittedAnswers());
    }

    private static String serializeSubmittedAnswers(Map<String, String> submittedAnswers) {
        if (submittedAnswers == null || submittedAnswers.isEmpty()) {
            return "";
        }

        StringBuilder builder = new StringBuilder();
        for (Map.Entry<String, String> entry : submittedAnswers.entrySet()) {
            String questionId = sanitize(entry.getKey());
            String answer = sanitize(entry.getValue());
            if (questionId.isEmpty() || answer.isEmpty()) {
                continue;
            }

            if (builder.length() > 0) {
                builder.append("|");
            }
            builder.append(questionId).append(":").append(answer);
        }
        return builder.toString();
    }

    private static Map<String, String> parseSubmittedAnswers(String value) {
        Map<String, String> answers = new LinkedHashMap<>();
        if (value == null || value.trim().isEmpty()) {
            return answers;
        }

        String[] pairs = value.split("\\|");
        for (String pair : pairs) {
            String[] parts = pair.split(":", 2);
            if (parts.length != 2) {
                continue;
            }

            String questionId = parts[0].trim();
            String answer = parts[1].trim().toUpperCase();
            if (!questionId.isEmpty() && !answer.isEmpty()) {
                answers.put(questionId, answer);
            }
        }
        return answers;
    }

    private static String sanitize(String value) {
        return value == null ? "" : value.replace(",", " ").trim();
    }
}
