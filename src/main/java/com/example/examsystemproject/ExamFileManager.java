package com.example.examsystemproject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ExamFileManager {

    private static final String FILE_PATH = "exams";

    public static void addExam(Exam exam) throws IOException {
        File file = new File(FILE_PATH);
        boolean fileExists = file.exists() && file.length() > 0;

        try (FileWriter writer = new FileWriter(file, true)) {
            if (fileExists) {
                writer.write("\n");
            }
            writer.write(formatLine(exam));
        }
    }

    public static List<Exam> loadAllExams() throws IOException {
        List<Exam> exams = new ArrayList<>();
        File file = new File(FILE_PATH);
        if (!file.exists()) {
            return exams;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) {
                    continue;
                }

                String[] parts = line.split(",", -1);
                if (parts.length != 6 && parts.length != 8) {
                    continue;
                }

                int duration;
                try {
                    duration = Integer.parseInt(parts.length == 8 ? parts[4] : parts[3]);
                } catch (NumberFormatException ex) {
                    continue;
                }

                int marks = 0;
                if (parts.length == 8) {
                    try {
                        marks = Integer.parseInt(parts[5]);
                    } catch (NumberFormatException ex) {
                    }
                }

                List<String> questionIds = new ArrayList<>();
                String questionPart = parts.length == 8 ? parts[6] : parts[4];
                if (!questionPart.isEmpty()) {
                    String[] ids = questionPart.split("\\|");
                    for (String id : ids) {
                        String trimmedId = id.trim();
                        if (!trimmedId.isEmpty()) {
                            questionIds.add(trimmedId);
                        }
                    }
                }

                if (parts.length == 8) {
                    exams.add(new Exam(parts[0], parts[1], parts[2], parts[3], duration, marks, questionIds, parts[7]));
                } else {
                    exams.add(new Exam(parts[0], parts[1], parts[2], duration, questionIds, parts[5]));
                }
            }
        }

        return exams;
    }

    public static String nextSequentialExamId() throws IOException {
        List<Exam> exams = loadAllExams();
        int max = 0;

        for (Exam exam : exams) {
            int id = extractSequentialId(exam.getExamId());
            if (id > max) {
                max = id;
            }
        }

        return "E" + (max + 1);
    }

    private static String formatLine(Exam exam) {
        String joinedIds = String.join("|", exam.getQuestionIds());
        return sanitize(exam.getExamId()) + ","
                + sanitize(exam.getExamName()) + ","
                + sanitize(exam.getSubject()) + ","
                + sanitize(exam.getClassLevel()) + ","
                + exam.getDurationMinutes() + ","
                + exam.getMarks() + ","
                + joinedIds + ","
                + sanitize(exam.getCreatedBy());
    }

    private static String sanitize(String value) {
        return value == null ? "" : value.replace(",", " ").trim();
    }

    private static int extractSequentialId(String examId) {
        if (examId == null || examId.length() < 2) {
            return -1;
        }

        char prefix = examId.charAt(0);
        if (prefix != 'E' && prefix != 'e') {
            return -1;
        }

        String numberPart = examId.substring(1);
        for (int i = 0; i < numberPart.length(); i++) {
            if (!Character.isDigit(numberPart.charAt(i))) {
                return -1;
            }
        }

        try {
            return Integer.parseInt(numberPart);
        } catch (NumberFormatException ex) {
            return -1;
        }
    }
}

