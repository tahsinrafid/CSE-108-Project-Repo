package com.example.examsystemproject;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExamDetailsController {

    @FXML
    private Label examTitleLabel;
    @FXML
    private Label subjectLabel;
    @FXML
    private Label classLabel;
    @FXML
    private Label marksLabel;
    @FXML
    private Label durationLabel;
    @FXML
    private Label createdByLabel;
    @FXML
    private Label totalQuestionsLabel;
    @FXML
    private VBox questionsContainer;

    private Exam exam;
    private User user;

    public void setExamAndUser(Exam selectedExam, User currentUser) {
        this.exam = selectedExam;
        this.user = currentUser;
        loadExamDetails();
    }

    private void loadExamDetails() {
        if (exam == null) {
            return;
        }

        examTitleLabel.setText(exam.getExamName());

        subjectLabel.setText(safe(exam.getSubject()));
        classLabel.setText(safe(exam.getClassLevel()));
        marksLabel.setText(String.valueOf(exam.getMarks()));
        durationLabel.setText(exam.getDurationMinutes() + " minutes");
        createdByLabel.setText(safe(exam.getCreatedBy()));
        totalQuestionsLabel.setText(String.valueOf(exam.getQuestionIds().size()));

        questionsContainer.getChildren().clear();

        try {
            List<Question> allQuestions = QuesFileManager.loadAllQuestions();
            Map<String, Question> questionsById = new HashMap<>();
            for (Question question : allQuestions) {
                questionsById.put(question.getQuesID(), question);
            }

            if (exam.getQuestionIds().isEmpty()) {
                questionsContainer.getChildren().add(createInfoLabel("No questions available for this exam."));
                return;
            }

            int displayIndex = 1;
            for (String questionId : exam.getQuestionIds()) {
                Question question = questionsById.get(questionId);
                if (question != null) {
                    questionsContainer.getChildren().add(createQuestionCard(question, displayIndex++));
                } else {
                    questionsContainer.getChildren().add(createInfoLabel("Question not found: " + questionId));
                }
            }
        } catch (IOException ex) {
            questionsContainer.getChildren().add(createInfoLabel("Failed to load questions: " + ex.getMessage()));
        }
    }

    private Label createInfoLabel(String message) {
        Label label = new Label(message);
        label.setStyle("-fx-text-fill: #b53a3a; -fx-font-size: 12; -fx-font-family: 'Trebuchet MS'; -fx-padding: 6 0 0 0;");
        return label;
    }

    private VBox createQuestionCard(Question question, int index) {
        VBox card = new VBox(8);
        card.setStyle("-fx-background-color: #f7f6fb; -fx-background-radius: 8; "
                + "-fx-border-color: #dedbea; -fx-border-radius: 8; -fx-border-width: 1; -fx-padding: 14;");

        Label questionText = new Label("Question " + index + ": " + safe(question.getQuesText()));
        questionText.setWrapText(true);
        questionText.setStyle("-fx-text-fill: #2f2349; -fx-font-size: 13; -fx-font-weight: bold; -fx-font-family: 'Trebuchet MS';");

        VBox optionsBox = new VBox(6);
        addOption(optionsBox, "A", question.getOption1(), question.getCorrectAns());
        addOption(optionsBox, "B", question.getOption2(), question.getCorrectAns());
        addOption(optionsBox, "C", question.getOption3(), question.getCorrectAns());
        addOption(optionsBox, "D", question.getOption4(), question.getCorrectAns());

        Label answerLabel = new Label("Correct Answer: " + safe(question.getCorrectAns()));
        answerLabel.setStyle("-fx-text-fill: #2a6d36; -fx-font-size: 11; -fx-font-weight: bold; -fx-font-family: 'Trebuchet MS'; -fx-padding: 4 0 0 0;");

        card.getChildren().addAll(questionText, optionsBox, answerLabel);
        return card;
    }

    private void addOption(VBox optionsBox, String optionCode, String optionText, String correctAnswerCode) {
        HBox row = new HBox(8);
        row.setStyle("-fx-background-color: #efedf6; -fx-background-radius: 6; -fx-padding: 8 10; -fx-border-color: #d3cfdd; -fx-border-radius: 6;");

        Label optionLabel = new Label(optionCode + ". " + safe(optionText));
        optionLabel.setWrapText(true);
        optionLabel.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(optionLabel, Priority.ALWAYS);
        optionLabel.setStyle("-fx-text-fill: #4e4a60; -fx-font-size: 12; -fx-font-family: 'Trebuchet MS';");

        if (optionCode.equalsIgnoreCase(safe(correctAnswerCode))) {
            row.setStyle("-fx-background-color: #dff5e6; -fx-background-radius: 6; -fx-padding: 8 10; -fx-border-color: #a8dfc1; -fx-border-radius: 6;");
            Label correctTag = new Label("Correct");
            correctTag.setStyle("-fx-text-fill: #2a6d36; -fx-font-size: 11; -fx-font-weight: bold; -fx-font-family: 'Trebuchet MS';");
            row.getChildren().addAll(optionLabel, correctTag);
        } else {
            row.getChildren().add(optionLabel);
        }

        optionsBox.getChildren().add(row);
    }

    private String safe(String value) {
        return value == null || value.trim().isEmpty() ? "-" : value;
    }

    @FXML
    public void onBack(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("ViewExams.fxml"));
        Parent root = loader.load();

        ViewExamsController controller = loader.getController();
        controller.setUser(user);

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }
}

