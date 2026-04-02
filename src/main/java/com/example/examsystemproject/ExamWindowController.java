package com.example.examsystemproject;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.ScaleTransition;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

public class ExamWindowController {
    @FXML
    private Label examTitleLabel;
    @FXML
    private Label examMetaLabel;
    @FXML
    private Label timerLabel;
    @FXML
    private VBox questionContainer;
    @FXML
    private VBox resultCard;
    @FXML
    private Label resultLabel;
    @FXML
    private Label resultDetailLabel;
    @FXML
    private Button submitButton;

    private final Map<String, ToggleGroup> answerGroups = new LinkedHashMap<>();
    private final Map<String, Map<String, RadioButton>> optionButtons = new HashMap<>();
    private Exam exam;
    private User user;
    private List<Question> examQuestions;
    private boolean submitted;
    private Timeline timer;
    private int remainingSeconds;

    @FXML
    public void initialize() {
        hideResultCard();
    }

    public void setContext(Exam exam, User user) {
        this.exam = exam;
        this.user = user;
        loadQuestions();
        populateHeader();
        startTimer();
    }

    @FXML
    public void onSubmitExam(ActionEvent event) {
        if (submitted) {
            return;
        }

        if (exam == null || examQuestions == null) {
            showAlert("No exam loaded", "Please select an exam first.");
            return;
        }

        int totalMarks = examQuestions.size() * 2;
        int score = 0;
        for (Question question : examQuestions) {
            ToggleGroup group = answerGroups.get(question.getQuesID());
            if (group == null || group.getSelectedToggle() == null) {
                continue;
            }

            RadioButton selected = (RadioButton) group.getSelectedToggle();
            if (selected.getUserData() != null && selected.getUserData().toString().equalsIgnoreCase(question.getCorrectAns())) {
                score += 2;
            }
        }

        String username = user != null ? user.getUsername() : "student";
        try {
            ExamResultFileManager.addResult(new ExamResult(
                    exam.getExamId(),
                    exam.getExamName(),
                    username,
                    score,
                    totalMarks));
            submitted = true;
            submitButton.setDisable(true);
            stopTimer();
            highlightSubmittedAnswers();
            revealResult(score, totalMarks);
        } catch (IOException ex) {
            showAlert("Save failed", "Could not store your marks: " + ex.getMessage());
        }
    }

    @FXML
    public void onBack(ActionEvent event) throws IOException {
        stopTimer();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("StudentStartExam.fxml"));
        Parent root = loader.load();
        StartExamController controller = loader.getController();
        controller.setUser(user);

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }

    private void populateHeader() {
        if (examTitleLabel == null || examMetaLabel == null || exam == null) {
            return;
        }
        examTitleLabel.setText(exam.getExamName());
        examMetaLabel.setText("Exam ID: " + exam.getExamId() + "  |  Subject: " + exam.getSubject() + "  |  Duration: " + exam.getDurationMinutes() + " min");
        remainingSeconds = Math.max(1, exam.getDurationMinutes()) * 60;
        updateTimerLabel();
    }

    private void loadQuestions() {
        answerGroups.clear();
        if (questionContainer == null || exam == null) {
            return;
        }

        questionContainer.getChildren().clear();
        try {
            List<Question> allQuestions = QuesFileManager.loadAllQuestions();
            examQuestions = allQuestions.stream()
                    .filter(q -> exam.getQuestionIds().contains(q.getQuesID()))
                    .toList();

            for (int i = 0; i < examQuestions.size(); i++) {
                questionContainer.getChildren().add(createQuestionCard(examQuestions.get(i), i + 1));
            }

            if (examQuestions.isEmpty()) {
                questionContainer.getChildren().add(infoLabel("No questions were found for this exam."));
            }
        } catch (IOException ex) {
            questionContainer.getChildren().add(infoLabel("Could not load questions: " + ex.getMessage()));
        }
    }

    private VBox createQuestionCard(Question question, int index) {
        VBox card = new VBox(12);
        card.setPadding(new Insets(16));
        card.setStyle("-fx-background-color: rgba(23, 49, 78, 0.88); -fx-background-radius: 12; -fx-border-radius: 12; -fx-border-color: rgba(154, 199, 221, 0.28);");

        Label title = new Label("Question " + index + ": " + question.getQuesText());
        title.setWrapText(true);
        title.setStyle("-fx-text-fill: #f4fbff; -fx-font-size: 15; -fx-font-weight: bold; -fx-font-family: 'Trebuchet MS';");

        VBox options = new VBox(8);
        ToggleGroup group = new ToggleGroup();
        answerGroups.put(question.getQuesID(), group);
        Map<String, RadioButton> buttonsForQuestion = new LinkedHashMap<>();
        optionButtons.put(question.getQuesID(), buttonsForQuestion);

        String[] optionTexts = {question.getOption1(), question.getOption2(), question.getOption3(), question.getOption4()};
        String[] optionCodes = {"A", "B", "C", "D"};
        for (int i = 0; i < optionTexts.length; i++) {
            RadioButton option = new RadioButton(optionCodes[i] + ". " + optionTexts[i]);
            option.setUserData(optionCodes[i]);
            option.setToggleGroup(group);
            option.setStyle("-fx-text-fill: #e4f2fb; -fx-font-size: 13; -fx-font-family: 'Trebuchet MS';");
            buttonsForQuestion.put(optionCodes[i], option);
            options.getChildren().add(option);
        }

        Label answerKey = new Label("Correct Answer: " + question.getCorrectAns());
        answerKey.setStyle("-fx-text-fill: #8ff0a4; -fx-font-size: 12; -fx-font-weight: bold; -fx-font-family: 'Trebuchet MS';");

        card.getChildren().addAll(title, options, answerKey);
        return card;
    }

    private void startTimer() {
        stopTimer();
        if (timerLabel == null || exam == null) {
            return;
        }

        timer = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            remainingSeconds--;
            updateTimerLabel();
            if (remainingSeconds <= 0) {
                stopTimer();
                if (!submitted) {
                    submitButton.fire();
                }
            }
        }));
        timer.setCycleCount(Timeline.INDEFINITE);
        timer.playFromStart();
    }

    private void stopTimer() {
        if (timer != null) {
            timer.stop();
        }
    }

    private void updateTimerLabel() {
        if (timerLabel == null) {
            return;
        }
        int minutes = Math.max(0, remainingSeconds) / 60;
        int seconds = Math.max(0, remainingSeconds) % 60;
        timerLabel.setText(String.format("Time Left: %02d:%02d", minutes, seconds));
    }

    private void highlightSubmittedAnswers() {
        if (examQuestions == null) {
            return;
        }

        for (Question question : examQuestions) {
            Map<String, RadioButton> buttons = optionButtons.get(question.getQuesID());
            ToggleGroup group = answerGroups.get(question.getQuesID());
            if (buttons == null || group == null) {
                continue;
            }

            String correctAns = question.getCorrectAns();
            for (Map.Entry<String, RadioButton> entry : buttons.entrySet()) {
                RadioButton option = entry.getValue();
                boolean isCorrect = entry.getKey().equalsIgnoreCase(correctAns);
                boolean isSelected = group.getSelectedToggle() != null
                        && entry.getKey().equalsIgnoreCase(group.getSelectedToggle().getUserData().toString());

                if (isCorrect) {
                    option.setStyle(option.getStyle() + "-fx-text-fill: #5fe08e; -fx-background-color: rgba(95,224,142,0.16); -fx-padding: 4 8; -fx-background-radius: 6;");
                } else if (isSelected) {
                    option.setStyle(option.getStyle() + "-fx-text-fill: #ff7a7a; -fx-background-color: rgba(255,122,122,0.18); -fx-padding: 4 8; -fx-background-radius: 6;");
                }
                option.setDisable(true);
            }
        }
    }

    private void revealResult(int score, int totalMarks) {
        if (resultCard == null || resultLabel == null || resultDetailLabel == null) {
            return;
        }

        int percent = totalMarks > 0 ? (int) Math.round((score * 100.0) / totalMarks) : 0;
        resultLabel.setText("Marks Obtained: " + score + " / " + totalMarks);
        resultDetailLabel.setText("Score: " + percent + "%");
        resultCard.setVisible(true);
        resultCard.setManaged(true);
        resultCard.setOpacity(0);
        resultCard.setScaleX(0.92);
        resultCard.setScaleY(0.92);
        resultCard.setTranslateY(18);

        FadeTransition fade = new FadeTransition(Duration.millis(420), resultCard);
        fade.setFromValue(0);
        fade.setToValue(1);

        ScaleTransition scale = new ScaleTransition(Duration.millis(420), resultCard);
        scale.setFromX(0.92);
        scale.setFromY(0.92);
        scale.setToX(1.0);
        scale.setToY(1.0);

        TranslateTransition slide = new TranslateTransition(Duration.millis(420), resultCard);
        slide.setFromY(18);
        slide.setToY(0);

        fade.play();
        scale.play();
        slide.play();
    }

    private void hideResultCard() {
        if (resultCard == null) {
            return;
        }
        resultCard.setVisible(false);
        resultCard.setManaged(false);
    }

    private Label infoLabel(String text) {
        Label label = new Label(text);
        label.setWrapText(true);
        label.setStyle("-fx-text-fill: #d7e8f6; -fx-font-size: 12; -fx-font-family: 'Trebuchet MS';");
        return label;
    }

    private void showAlert(String title, String text) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(text);
        alert.showAndWait();
    }
}
