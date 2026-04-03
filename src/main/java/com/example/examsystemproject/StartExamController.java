package com.example.examsystemproject;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class StartExamController {

    @FXML
    private Label dueExamCountLabel;

    @FXML
    private VBox dueExamBox;

    private User user;

    @FXML
    public void initialize() {
        refreshPage();
        System.out.println("StartExam page loaded.");
    }

    public void setUser(User user) {
        this.user = user;
        refreshPage();
    }

    @FXML
    public void onSitForExam(ActionEvent e) {
        Object source = e.getSource();
        if (!(source instanceof Button button) || !(button.getUserData() instanceof Exam exam)) {
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("ExamWindow.fxml"));
            Parent root = loader.load();
            ExamWindowController controller = loader.getController();
            controller.setContext(exam, user);

            Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException ex) {
            throw new RuntimeException("Could not open exam window", ex);
        }
    }

    @FXML
    public void onPracticeExam(ActionEvent e) {
        System.out.println("Practice exam clicked");
    }

    @FXML
    public void onViewLeaderboard(ActionEvent e) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Leaderboard.fxml"));
        Parent root = loader.load();
        LeaderboardController controller = loader.getController();
        controller.setUser(user);

        Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }

    @FXML
    public void onBackToDashboard(ActionEvent e) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("DashBoard.fxml"));
        Parent root = loader.load();
        DashBoardController controller = loader.getController();
        controller.setUser(user);

        Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }

    private void refreshPage() {
        if (dueExamBox == null || dueExamCountLabel == null) {
            return;
        }

        dueExamBox.getChildren().clear();

        try {
            List<Exam> exams = ExamFileManager.loadAllExams();
            List<ExamResult> allResults = ExamResultFileManager.loadAllResults();
            String username = user != null ? user.getUsername() : "student";

            Set<String> attemptedExamIds = new HashSet<>();
            for (ExamResult result : allResults) {
                if (username.equalsIgnoreCase(result.getStudentUsername())) {
                    attemptedExamIds.add(result.getExamId());
                }
            }

            int pending = 0;
            for (Exam exam : exams) {
                if (!attemptedExamIds.contains(exam.getExamId())) {
                    pending++;
                    dueExamBox.getChildren().add(createDueCard(exam));
                }
            }

            dueExamCountLabel.setText(pending + " pending");

            if (pending == 0) {
                dueExamBox.getChildren().add(createInfoLabel("No pending exam. You are all caught up."));
            }
        } catch (IOException ex) {
            dueExamCountLabel.setText("0 pending");
            dueExamBox.getChildren().add(createInfoLabel("Could not load exams: " + ex.getMessage()));
        }
    }

    private HBox createDueCard(Exam exam) {
        HBox card = new HBox(12);
        card.setPadding(new Insets(12, 14, 12, 14));
        card.setStyle("-fx-background-color: #1d4265; -fx-background-radius: 9;");

        VBox infoBox = new VBox(4);
        Label title = new Label(exam.getExamName());
        title.setStyle("-fx-text-fill: #f6fbff; -fx-font-size: 14; -fx-font-weight: bold; -fx-font-family: 'Trebuchet MS';");
        Label meta = new Label("Exam ID: " + exam.getExamId() + "   Subject: " + exam.getSubject() + "   Duration: " + exam.getDurationMinutes() + " min");
        meta.setStyle("-fx-text-fill: #b7d3e8; -fx-font-size: 12; -fx-font-family: 'Trebuchet MS';");
        infoBox.getChildren().addAll(title, meta);
        HBox.setHgrow(infoBox, Priority.ALWAYS);

        Button startBtn = new Button("Start Exam");
        startBtn.setUserData(exam);
        startBtn.setOnAction(this::onSitForExam);
        startBtn.setStyle("-fx-background-color: #e26f2b; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-family: 'Trebuchet MS'; -fx-background-radius: 7; -fx-cursor: hand;");

        card.getChildren().addAll(infoBox, startBtn);
        return card;
    }

    private Label createInfoLabel(String text) {
        Label label = new Label(text);
        label.setWrapText(true);
        label.setStyle("-fx-text-fill: #d7e8f6; -fx-font-size: 12; -fx-font-family: 'Trebuchet MS';");
        return label;
    }

}

