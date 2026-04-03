package com.example.examsystemproject;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class LeaderboardController {
    @FXML
    private VBox leaderboardList;

    @FXML
    private TextField searchField;

    private User user;

    @FXML
    public void initialize() {
        clearLeaderboard();
    }

    public void setUser(User user) {
        this.user = user;
        clearLeaderboard();
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

    @FXML
    public void onSearch() {
        String examQuery = searchField == null ? "" : searchField.getText();
        populateLeaderboard(examQuery);
    }

    private void clearLeaderboard() {
        if (leaderboardList != null) {
            leaderboardList.getChildren().clear();
        }
    }

    private void populateLeaderboard(String examQuery) {
        if (leaderboardList == null) {
            return;
        }

        leaderboardList.getChildren().clear();

        String normalizedQuery = examQuery == null ? "" : examQuery.trim();
        if (normalizedQuery.isEmpty()) {
            return;
        }

        try {
            List<ExamResult> results = ExamResultFileManager.loadAllResults();
            if (results.isEmpty()) {
                leaderboardList.getChildren().add(createEmptyMessage());
                return;
            }

            Map<String, String> subjectByExamId = ExamFileManager.loadAllExams().stream()
                    .collect(Collectors.toMap(Exam::getExamId, Exam::getSubject, (first, ignored) -> first));

            List<ExamResult> filtered = results.stream()
                    .filter(result -> containsIgnoreCase(result.getExamName(), normalizedQuery))
                    .sorted(Comparator
                            .comparingInt(ExamResult::getScore)
                            .reversed()
                            .thenComparing(ExamResult::getStudentUsername, String.CASE_INSENSITIVE_ORDER))
                    .toList();

            if (filtered.isEmpty()) {
                leaderboardList.getChildren().add(createNoMatchMessage(normalizedQuery));
                return;
            }

            int rank = 1;
            for (ExamResult result : filtered) {
                String subject = subjectByExamId.getOrDefault(result.getExamId(), "-");
                leaderboardList.getChildren().add(createLeaderboardRow(rank, result, subject));
                rank++;
            }
        } catch (IOException ex) {
            Label error = new Label("Unable to load leaderboard: " + ex.getMessage());
            error.setStyle("-fx-text-fill: #ffb4b4; -fx-font-size: 13; -fx-font-family: 'Trebuchet MS';");
            leaderboardList.getChildren().add(error);
        }
    }

    private boolean containsIgnoreCase(String value, String query) {
        if (value == null || query == null) {
            return false;
        }
        return value.toLowerCase().contains(query.toLowerCase());
    }

    private HBox createLeaderboardRow(int rank, ExamResult result, String subject) {
        HBox row = new HBox(8);
        row.setPadding(new Insets(8, 10, 8, 10));
        row.setStyle("-fx-background-color: #f4f4f7; -fx-border-color: #e7e5ee; -fx-border-width: 0 0 1 0;");

        Label examLabel = createCell(result.getExamName(), 170, "#2d2940", false);
        Label subjectLabel = createCell(subject, 115, "#4e4a60", false);
        Label userLabel = createCell(result.getStudentUsername(), 185, "#2d2940", false);
        Label totalLabel = createCell(String.valueOf(result.getTotalMarks()), 105, "#2d2940", true);
        Label obtainedLabel = createCell(String.valueOf(result.getScore()), 120, "#2d2940", true);
        Label rankLabel = createCell(String.valueOf(rank), 63, "#2d2940", true);

        row.getChildren().addAll(examLabel, subjectLabel, userLabel, totalLabel, obtainedLabel, rankLabel);
        return row;
    }

    private Label createCell(String text, double width, String color, boolean center) {
        Label label = new Label(text == null ? "-" : text);
        label.setPrefWidth(width);
        label.setMinWidth(width);
        label.setStyle("-fx-text-fill: " + color + "; -fx-font-size: 12; -fx-font-family: 'Trebuchet MS';"
                + (center ? " -fx-alignment: center;" : ""));
        return label;
    }

    private Label createEmptyMessage() {
        Label empty = new Label("No exam marks available yet.");
        empty.setWrapText(true);
        empty.setStyle("-fx-text-fill: #6d6780; -fx-font-size: 13; -fx-font-family: 'Trebuchet MS';");
        return empty;
    }

    private Label createNoMatchMessage(String examName) {
        Label empty = new Label("No results found for exam: " + examName);
        empty.setWrapText(true);
        empty.setStyle("-fx-text-fill: #6d6780; -fx-font-size: 13; -fx-font-family: 'Trebuchet MS';");
        return empty;
    }
}
