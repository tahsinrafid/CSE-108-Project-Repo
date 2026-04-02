package com.example.examsystemproject;

import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class QuesFileManager {

    private static final String FILE_PATH = "questions";

    public static void addQues(Question ques) throws IOException {
        File file = new File(FILE_PATH);
        boolean fileExists = file.exists() && file.length() > 0;
        try (FileWriter writer = new FileWriter(file, true)) {
            if (fileExists) {
                writer.write("\n");
            }
            writer.write(formatLine(ques));
        }
    }

    public static String nextSequentialQuesId() throws IOException {
        List<Question> questions = loadAllQuestions();
        Set<Integer> usedIds = new HashSet<>();

        for (Question question : questions) {
            int idNumber = extractSequentialId(question.getQuesID());
            if (idNumber > 0) {
                usedIds.add(idNumber);
            }
        }

        int nextId = 1;
        while (usedIds.contains(nextId)) {
            nextId++;
        }

        return "Q" + nextId;
    }

    public static List<Question> loadAllQuestions() throws IOException {
        List<Question> questions = new ArrayList<>();
        File file = new File(FILE_PATH);
        if (!file.exists()) return questions;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                String[] parts = line.split(",", 7);
                if (parts.length == 7) {
                    questions.add(new Question(parts[0], parts[1], parts[2], parts[3], parts[4], parts[5], parts[6]));
                }
            }
        }
        return questions;
    }

    public static void updateQues(Question updated) throws IOException {
        List<Question> questions = loadAllQuestions();
        for (int i = 0; i < questions.size(); i++) {
            if (questions.get(i).getQuesID().equals(updated.getQuesID())) {
                questions.set(i, updated);
                break;
            }
        }
        rewriteFile(questions);
    }

    public static void deleteQues(String quesID) throws IOException {
        List<Question> questions = loadAllQuestions();
        questions.removeIf(q -> q.getQuesID().equals(quesID));
        rewriteFile(questions);
    }

    private static void rewriteFile(List<Question> questions) throws IOException {
        File file = new File(FILE_PATH);
        try (FileWriter writer = new FileWriter(file, false)) {
            for (int i = 0; i < questions.size(); i++) {
                if (i > 0) writer.write("\n");
                writer.write(formatLine(questions.get(i)));
            }
        }
    }

    private static String formatLine(Question q) {
        return q.getQuesID() + "," + q.getQuesText() + "," + q.getOption1() + ","
                + q.getOption2() + "," + q.getOption3() + "," + q.getOption4() + "," + q.getCorrectAns();
    }

    private static int extractSequentialId(String quesID) {
        if (quesID == null || quesID.length() < 2) {
            return -1;
        }

        char prefix = quesID.charAt(0);
        if (prefix != 'Q' && prefix != 'q') {
            return -1;
        }

        String numberPart = quesID.substring(1);

        for (int i = 0; i < numberPart.length(); i++) {
            if (!Character.isDigit(numberPart.charAt(i))) {
                return -1;
            }
        }

        try {
            return Integer.parseInt(numberPart);
        } catch (NumberFormatException ex) {
            // Ignore malformed/oversized IDs and keep sequential generation stable.
            return -1;
        }
    }
}
