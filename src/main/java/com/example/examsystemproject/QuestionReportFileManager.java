package com.example.examsystemproject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class QuestionReportFileManager {
    private static final String FILE_PATH = "question_reports";

    public static void addReport(QuestionReport report) throws IOException {
        File file = new File(FILE_PATH);
        boolean fileExists = file.exists() && file.length() > 0;

        try (FileWriter writer = new FileWriter(file, true)) {
            if (fileExists) {
                writer.write("\n");
            }
            writer.write(formatLine(report));
        }
    }

    public static List<QuestionReport> loadAllReports() throws IOException {
        List<QuestionReport> reports = new ArrayList<>();
        File file = new File(FILE_PATH);
        if (!file.exists()) {
            return reports;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) {
                    continue;
                }

                String[] parts = line.split(",", 3);
                if (parts.length != 3) {
                    continue;
                }

                reports.add(new QuestionReport(parts[0], parts[1], parts[2]));
            }
        }

        return reports;
    }

    private static String formatLine(QuestionReport report) {
        return sanitize(report.getQuestionId()) + ","
                + sanitize(report.getUsername()) + ","
                + sanitize(report.getDetails());
    }

    private static String sanitize(String value) {
        return value == null ? "" : value.replace(",", " ").trim();
    }
}
