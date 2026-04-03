package com.example.examsystemproject;

import java.io.IOException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

public class LeaderboardController {
    @FXML
    private VBox leaderboardList;

    private User user;

    @FXML
    public void initialize() {
        populateLeaderboard();
    }

    public void setUser(User user) {
        this.user = user;
        populateLeaderboard();
    }

    @FXML
    public void onBack(javafx.event.ActionEvent event) throws IOException {
        String target = (user != null && "teacher".equalsIgnoreCase(user.getRole()))
                ? "Teacher_DashBoard.fxml"
                : "DashBoard.fxml";

        FXMLLoader loader = new FXMLLoader(getClass().getResource(target));
        Parent root = loader.load();

        if ("Teacher_DashBoard.fxml".equals(target)) {
            TeacherDashBoardController controller = loader.getController();
            controller.setUser(user);
        } else {
            DashBoardController controller = loader.getController();
            controller.setUser(user);
        }

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }

    private void populateLeaderboard() {
        if (leaderboardList == null) {
            return;
        }

        leaderboardList.getChildren().clear();

        try {
            List<ExamResult> results = ExamResultFileManager.loadAllResults();
            if (results.isEmpty()) {
                leaderboardList.getChildren().add(createEmptyMessage());
                return;
            }

            // Keep the best score per student for fair ranking.
            Map<String, ExamResult> bestByStudent = new HashMap<>();
            for (ExamResult result : results) {
                ExamResult existing = bestByStudent.get(result.getStudentUsername());
                if (existing == null || compareResult(result, existing) > 0) {
                    bestByStudent.put(result.getStudentUsername(), result);
                }
            }

            List<ExamResult> ranked = bestByStudent.values().stream()
                    .sorted(Comparator
                            .comparingDouble(this::percentage)
                            .reversed()
                            .thenComparing(ExamResult::getStudentUsername))
                    .toList();

            int rank = 1;
            for (ExamResult result : ranked) {
                leaderboardList.getChildren().add(createLeaderboardRow(rank, result));
                rank++;
            }
        } catch (IOException ex) {
            Label error = new Label("Unable to load leaderboard: " + ex.getMessage());
            error.setStyle("-fx-text-fill: #ffb4b4; -fx-font-size: 13; -fx-font-family: 'Trebuchet MS';");
            leaderboardList.getChildren().add(error);
        }
    }

    private int compareResult(ExamResult a, ExamResult b) {
        return Double.compare(percentage(a), percentage(b));
    }

    private double percentage(ExamResult result) {
        if (result.getTotalMarks() <= 0) {
            return 0;
        }
        return (result.getScore() * 100.0) / result.getTotalMarks();
    }

    private HBox createLeaderboardRow(int rank, ExamResult result) {
        HBox row = new HBox(14);
        row.setPadding(new Insets(10, 14, 10, 14));
        row.setStyle("-fx-background-color: rgba(34, 69, 101, 0.88); -fx-background-radius: 8;");

        Label rankLabel = new Label("#" + rank);
        rankLabel.setPrefWidth(45);
        rankLabel.setStyle("-fx-text-fill: #f7d774; -fx-font-weight: bold; -fx-font-size: 14; -fx-font-family: 'Trebuchet MS';");

        Label userLabel = new Label(result.getStudentUsername());
        userLabel.setPrefWidth(180);
        userLabel.setStyle("-fx-text-fill: #eef7ff; -fx-font-size: 14; -fx-font-weight: bold; -fx-font-family: 'Trebuchet MS';");

        Label examLabel = new Label(result.getExamName());
        examLabel.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(examLabel, Priority.ALWAYS);
        examLabel.setStyle("-fx-text-fill: #b7d3eb; -fx-font-size: 13; -fx-font-family: 'Trebuchet MS';");

        int percent = (int) Math.round(percentage(result));
        Label markLabel = new Label(result.getScore() + "/" + result.getTotalMarks() + " (" + percent + "%)");
        markLabel.setStyle("-fx-text-fill: #8ff0a4; -fx-font-size: 13; -fx-font-weight: bold; -fx-font-family: 'Trebuchet MS';");

        row.getChildren().addAll(rankLabel, userLabel, examLabel, markLabel);
        return row;
    }

    private Label createEmptyMessage() {
        Label empty = new Label("No exam marks available yet. Once students take exams, rankings will appear here.");
        empty.setWrapText(true);
        empty.setStyle("-fx-text-fill: #d5e7f6; -fx-font-size: 13; -fx-font-family: 'Trebuchet MS';");
        return empty;
    }
}
