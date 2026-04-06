package com.example.examsystemproject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ResultDetailsController {
    @FXML
    private Label examTitleLabel;
    @FXML
    private Label examMetaLabel;
    @FXML
    private Label classLabel;
    @FXML
    private Label subjectLabel;
    @FXML
    private Label questionsLabel;
    @FXML
    private Label scoreLabel;
    @FXML
    private Label percentageLabel;
    @FXML
    private VBox questionsContainer;

    private ExamResult result;
    private User user;

    public void setResultAndUser(ExamResult examResult, User currentUser) {
        this.result = examResult;
        this.user = currentUser;
        loadResultDetails();
    }

    private void loadResultDetails() {
        if (result == null) {
            return;
        }

        try {
            List<Exam> allExams = ExamFileManager.loadAllExams();
            Exam exam = null;
            for (Exam e : allExams) {
                if (e.getExamId().equals(result.getExamId())) {
                    exam = e;
                    break;
                }
            }

            if (exam == null) {
                Label error = new Label("Exam details not found.");
                error.setStyle("-fx-text-fill: #b53a3a; -fx-font-size: 13; -fx-font-family: 'Trebuchet MS';");
                questionsContainer.getChildren().add(error);
                return;
            }

            examTitleLabel.setText(result.getExamName());
            examMetaLabel.setText("Subject: " + exam.getSubject() + "  |  Questions: " + exam.getQuestionIds().size());

            classLabel.setText(exam.getClassLevel() != null && !exam.getClassLevel().isEmpty() ? exam.getClassLevel() : "-");
            subjectLabel.setText(exam.getSubject());
            questionsLabel.setText(String.valueOf(exam.getQuestionIds().size()));

            int percentage = result.getTotalMarks() > 0 ? (int) Math.round((result.getScore() * 100.0) / result.getTotalMarks()) : 0;
            scoreLabel.setText(result.getScore() + " / " + result.getTotalMarks());
            percentageLabel.setText(percentage + "%");

            List<Question> allQuestions = QuesFileManager.loadAllQuestions();
            List<Question> examQuestions = new ArrayList<>();
            for (Question q : allQuestions) {
                if (exam.getQuestionIds().contains(q.getQuesID())) {
                    examQuestions.add(q);
                }
            }

            questionsContainer.getChildren().clear();
            for (int i = 0; i < examQuestions.size(); i++) {
                questionsContainer.getChildren().add(createQuestionCard(examQuestions.get(i), i + 1));
            }
        } catch (IOException ex) {
            Label error = new Label("Failed to load result details: " + ex.getMessage());
            error.setStyle("-fx-text-fill: #b53a3a; -fx-font-size: 13; -fx-font-family: 'Trebuchet MS';");
            questionsContainer.getChildren().add(error);
        }
    }

    private VBox createQuestionCard(Question question, int index) {
        VBox card = new VBox(10);
        card.setPadding(new Insets(14));
        card.setStyle("-fx-background-color: #f7f6fb; -fx-background-radius: 8; "
                + "-fx-border-color: #dedbea; -fx-border-radius: 8; -fx-border-width: 1;");

        Label questionText = new Label("Question " + index + ": " + question.getQuesText());
        questionText.setWrapText(true);
        questionText.setStyle("-fx-text-fill: #2f2349; -fx-font-size: 13; -fx-font-weight: bold; -fx-font-family: 'Trebuchet MS';");

        VBox options = new VBox(6);
        String studentAnswer = result.getSubmittedAnswers().get(question.getQuesID());
        String[] optionTexts = {question.getOption1(), question.getOption2(), question.getOption3(), question.getOption4()};
        String[] optionCodes = {"A", "B", "C", "D"};

        for (int i = 0; i < optionTexts.length; i++) {
            String optionCode = optionCodes[i];
            String optionText = optionTexts[i];
            boolean isCorrect = optionCode.equalsIgnoreCase(question.getCorrectAns());
            boolean isStudentAnswer = optionCode.equalsIgnoreCase(studentAnswer);
            boolean isStudentWrongPick = isStudentAnswer && !isCorrect;

            HBox optionBox = new HBox(8);
            optionBox.setPadding(new Insets(8, 10, 8, 10));
            optionBox.setStyle("-fx-border-radius: 5; -fx-background-radius: 5;");

            if (isCorrect) {
                optionBox.setStyle(optionBox.getStyle() + " -fx-background-color: #dff5e6; -fx-border-color: #a8dfc1; -fx-border-width: 1;");
            } else if (isStudentWrongPick) {
                optionBox.setStyle(optionBox.getStyle() + " -fx-background-color: #fde7e7; -fx-border-color: #e7aaaa; -fx-border-width: 1;");
            } else {
                optionBox.setStyle(optionBox.getStyle() + " -fx-background-color: #efedf6; -fx-border-color: #d3cfdd; -fx-border-width: 1;");
            }

            Label optionLabel = new Label(optionCode + ". " + optionText);
            optionLabel.setWrapText(true);
            optionLabel.setMaxWidth(Double.MAX_VALUE);
            HBox.setHgrow(optionLabel, Priority.ALWAYS);

            String textColor = isCorrect ? "#2a6d36" : (isStudentWrongPick ? "#a32929" : "#4e4a60");
            optionLabel.setStyle("-fx-text-fill: " + textColor + "; -fx-font-size: 12; -fx-font-family: 'Trebuchet MS';");

            if (isCorrect) {
                Label checkMark = new Label("✓");
                checkMark.setStyle("-fx-text-fill: #2a6d36; -fx-font-size: 12; -fx-font-weight: bold; -fx-font-family: 'Trebuchet MS';");
                optionBox.getChildren().addAll(optionLabel, checkMark);
            } else if (isStudentWrongPick) {
                Label yourPick = new Label("Your Pick");
                yourPick.setStyle("-fx-text-fill: #a32929; -fx-font-size: 11; -fx-font-weight: bold; -fx-font-family: 'Trebuchet MS';");
                optionBox.getChildren().addAll(optionLabel, yourPick);
            } else {
                optionBox.getChildren().add(optionLabel);
            }

            options.getChildren().add(optionBox);
        }

        String shownStudentAnswer = studentAnswer == null || studentAnswer.isBlank() ? "Not Available" : studentAnswer;
        Label studentAnswerLabel = new Label("Your Answer: " + shownStudentAnswer);
        boolean hasStudentAnswer = studentAnswer != null && !studentAnswer.isBlank();
        boolean isStudentCorrect = hasStudentAnswer && studentAnswer.equalsIgnoreCase(question.getCorrectAns());
        String studentAnswerColor = !hasStudentAnswer ? "#2f4b8a" : (isStudentCorrect ? "#2a6d36" : "#a32929");
        studentAnswerLabel.setStyle("-fx-text-fill: " + studentAnswerColor + "; -fx-font-size: 11; -fx-font-weight: bold; -fx-font-family: 'Trebuchet MS'; -fx-padding: 6 0 0 0;");

        Label correctAnswer = new Label("Correct Answer: " + question.getCorrectAns());
        correctAnswer.setStyle("-fx-text-fill: #2a6d36; -fx-font-size: 11; -fx-font-weight: bold; -fx-font-family: 'Trebuchet MS'; -fx-padding: 6 0 0 0;");

        card.getChildren().addAll(questionText, options, studentAnswerLabel, correctAnswer);
        return card;
    }

    @FXML
    public void onBack(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("ViewResults.fxml"));
        Parent root = loader.load();

        ViewResultsController controller = loader.getController();
        controller.setUser(user);

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }
}


