package com.example.examsystemproject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import javafx.animation.PauseTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

public class SpeedTestController {
    @FXML
    private Label titleLabel;
    @FXML
    private Label questionCountLabel;
    @FXML
    private Label questionLabel;
    @FXML
    private RadioButton option1Btn;
    @FXML
    private RadioButton option2Btn;
    @FXML
    private RadioButton option3Btn;
    @FXML
    private RadioButton option4Btn;
    @FXML
    private ToggleGroup optionGroup;
    @FXML
    private Button submitBtn;
    @FXML
    private Label feedbackLabel;
    @FXML
    private Label resultLabel;
    @FXML
    private VBox resultContainer;
    @FXML
    private Label correctAnsLabel;
    @FXML
    private Button nextBtn;

    private User user;
    private List<Question> allQuestions;
    private List<Question> speedTestQuestions;
    private List<Question> wrongQuestions;
    private int currentQuestionIndex;
    private int score;
    private static final int QUESTION_LIMIT = 10;
    private static final int PAUSE_DURATION = 5; // seconds

    @FXML
    public void initialize() {
        submitBtn.setOnAction(e -> handleSubmitAnswer());
        nextBtn.setOnAction(e -> loadNextQuestion());
        feedbackLabel.setText("");
        resultContainer.setVisible(false);
    }

    public void setUser(User user) {
        this.user = user;
        initializeSpeedTest();
    }

    private void initializeSpeedTest() {
        try {
            allQuestions = QuesFileManager.loadAllQuestions();
            speedTestQuestions = new ArrayList<>();
            wrongQuestions = new ArrayList<>();
            currentQuestionIndex = 0;
            score = 0;

            // Randomly select up to QUESTION_LIMIT questions
            List<Question> shuffled = new ArrayList<>(allQuestions);
            Collections.shuffle(shuffled);
            int limit = Math.min(QUESTION_LIMIT, shuffled.size());
            for (int i = 0; i < limit; i++) {
                speedTestQuestions.add(shuffled.get(i));
            }

            titleLabel.setText("Speed Test - " + speedTestQuestions.size() + " Questions");
            loadNextQuestion();
        } catch (IOException e) {
            showError("Failed to load questions: " + e.getMessage());
        }
    }

    private void loadNextQuestion() {
        // If current question was wrong and we just answered it correctly, remove from wrong list
        if (currentQuestionIndex < speedTestQuestions.size()) {
            Question currentQ = speedTestQuestions.get(currentQuestionIndex);
            if (wrongQuestions.contains(currentQ)) {
                wrongQuestions.remove(currentQ);
            }
        }

        // Check if we have more questions to ask, or if we should ask from wrong list
        if (currentQuestionIndex >= speedTestQuestions.size()) {
            if (!wrongQuestions.isEmpty()) {
                // Ask wrong questions again
                currentQuestionIndex = 0;
                speedTestQuestions = new ArrayList<>(wrongQuestions);
                wrongQuestions.clear();
                titleLabel.setText("Speed Test - Re-attempt Wrong Answers (" + speedTestQuestions.size() + ")");
            } else {
                // All done
                showResults();
                return;
            }
        }

        Question question = speedTestQuestions.get(currentQuestionIndex);
        displayQuestion(question);
        currentQuestionIndex++;
    }

    private void displayQuestion(Question question) {
        questionLabel.setText(question.getQuesText());
        option1Btn.setText(question.getOption1());
        option2Btn.setText(question.getOption2());
        option3Btn.setText(question.getOption3());
        option4Btn.setText(question.getOption4());

        // Clear selection and reset styles
        optionGroup.selectToggle(null);
        option1Btn.setStyle("");
        option2Btn.setStyle("");
        option3Btn.setStyle("");
        option4Btn.setStyle("");

        feedbackLabel.setText("");
        resultContainer.setVisible(false);
        submitBtn.setDisable(false);
        nextBtn.setVisible(false);
        questionCountLabel.setText("Question " + currentQuestionIndex + " of " + speedTestQuestions.size());
    }

    private void handleSubmitAnswer() {
        RadioButton selectedOption = (RadioButton) optionGroup.getSelectedToggle();
        if (selectedOption == null) {
            showError("Please select an answer");
            return;
        }

        Question currentQuestion = speedTestQuestions.get(currentQuestionIndex - 1);
        String selectedAnswer = selectedOption.getText();
        String correctAnswer = currentQuestion.getCorrectAns();

        boolean isCorrect = selectedAnswer.equals(correctAnswer);

        if (isCorrect) {
            score++;
            feedbackLabel.setText("✓ Correct!");
            feedbackLabel.setStyle("-fx-text-fill: #5fe08e; -fx-font-size: 16; -fx-font-weight: bold;");
            selectedOption.setStyle("-fx-text-fill: #5fe08e;");
        } else {
            feedbackLabel.setText("✗ Wrong!");
            feedbackLabel.setStyle("-fx-text-fill: #ff7a7a; -fx-font-size: 16; -fx-font-weight: bold;");
            selectedOption.setStyle("-fx-text-fill: #ff7a7a;");
            
            // Show correct answer
            resultLabel.setText("Correct Answer: " + correctAnswer);
            resultContainer.setVisible(true);

            // Add to wrong questions for re-attempt
            if (!wrongQuestions.contains(currentQuestion)) {
                wrongQuestions.add(currentQuestion);
            }
        }

        submitBtn.setDisable(true);
        disableAllOptions();

        // Pause for 5 seconds then load next question
        PauseTransition pause = new PauseTransition(Duration.seconds(PAUSE_DURATION));
        pause.setOnFinished(e -> loadNextQuestion());
        pause.play();
    }

    private void disableAllOptions() {
        option1Btn.setDisable(true);
        option2Btn.setDisable(true);
        option3Btn.setDisable(true);
        option4Btn.setDisable(true);
    }

    private void showResults() {
        int totalAsked = currentQuestionIndex;
        int percentage = (int) ((score * 100.0) / totalAsked);

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Speed Test Complete");
        alert.setHeaderText("Your Results");
        alert.setContentText("Score: " + score + " / " + totalAsked + " (" + percentage + "%)");
        Optional<ButtonType> result = alert.showAndWait();

        if (result.isPresent()) {
            onBack(null);
        }
    }

    private void showError(String message) {
        feedbackLabel.setText(message);
        feedbackLabel.setStyle("-fx-text-fill: #ff7a7a; -fx-font-size: 14;");
    }

    @FXML
    public void onBack(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("DashBoard.fxml"));
            Parent root = loader.load();
            DashBoardController controller = loader.getController();
            controller.setUser(user);

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            showError("Failed to load dashboard: " + e.getMessage());
        }
    }
}
