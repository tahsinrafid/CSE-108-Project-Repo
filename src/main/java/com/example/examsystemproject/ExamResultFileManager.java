package com.example.examsystemproject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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

                String[] parts = line.split(",", 5);
                if (parts.length != 5) {
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

                results.add(new ExamResult(parts[0], parts[1], parts[2], score, total));
            }
        }

        return results;
    }

    private static String formatLine(ExamResult result) {
        return sanitize(result.getExamId()) + ","
                + sanitize(result.getExamName()) + ","
                + sanitize(result.getStudentUsername()) + ","
                + result.getScore() + ","
                + result.getTotalMarks();
    }

    private static String sanitize(String value) {
        return value == null ? "" : value.replace(",", " ").trim();
    }
}
