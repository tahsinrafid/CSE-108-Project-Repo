package com.example.examsystemproject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ViewResultsController {
    @FXML
    private VBox resultsList;

    private User user;

    @FXML
    public void initialize() {
        resultsList.getChildren().clear();
    }

    public void setUser(User user) {
        this.user = user;
        loadResults();
    }

    private void loadResults() {
        resultsList.getChildren().clear();

        if (user == null) {
            Label errorLabel = new Label("User not loaded. Please log in again.");
            errorLabel.setStyle("-fx-text-fill: #b53a3a; -fx-font-size: 13; -fx-font-family: 'Trebuchet MS'; -fx-padding: 20;");
            resultsList.getChildren().add(errorLabel);
            return;
        }

        try {
            List<ExamResult> allResults = ExamResultFileManager.loadAllResults();
            List<ExamResult> userResults = new ArrayList<>();
            for (ExamResult result : allResults) {
                if (user.getUsername().equalsIgnoreCase(result.getStudentUsername())) {
                    userResults.add(result);
                }
            }

            if (userResults.isEmpty()) {
                Label emptyLabel = new Label("No exam attempts yet. Start an exam to see your results here.");
                emptyLabel.setStyle("-fx-text-fill: #6d6780; -fx-font-size: 13; -fx-font-family: 'Trebuchet MS'; -fx-padding: 20;");
                resultsList.getChildren().add(emptyLabel);
                return;
            }

            Map<String, String> classLevelMap = new HashMap<>();
            Map<String, String> subjectMap = new HashMap<>();
            try {
                List<Exam> exams = ExamFileManager.loadAllExams();
                for (Exam exam : exams) {
                    classLevelMap.put(exam.getExamId(), exam.getClassLevel());
                    subjectMap.put(exam.getExamId(), exam.getSubject());
                }
            } catch (IOException ex) {
                // Silently continue if exams can't be loaded
            }

            for (ExamResult result : userResults) {
                resultsList.getChildren().add(createResultRow(result, classLevelMap, subjectMap));
            }
        } catch (IOException ex) {
            Label errorLabel = new Label("Failed to load results: " + ex.getMessage());
            errorLabel.setStyle("-fx-text-fill: #b53a3a; -fx-font-size: 13; -fx-font-family: 'Trebuchet MS'; -fx-padding: 20;");
            resultsList.getChildren().add(errorLabel);
        }
    }

    private HBox createResultRow(ExamResult result, Map<String, String> classMap, Map<String, String> ignored) {
        HBox row = new HBox(10);
        row.setPadding(new Insets(12, 14, 12, 14));
        row.setStyle("-fx-background-color: #f4f4f7; -fx-border-color: #e7e5ee; -fx-border-width: 0 0 1 0;");

        Label examNameLabel = createCell(result.getExamName(), 200, "#2d2940", false);
        Label classLabel = createCell(classMap.getOrDefault(result.getExamId(), "-"), 120, "#4e4a60", false);
        Label marksLabel = createCell(result.getScore() + "/" + result.getTotalMarks(), 120, "#2d2940", true);

        int percentage = result.getTotalMarks() > 0 ? (int) Math.round((result.getScore() * 100.0) / result.getTotalMarks()) : 0;
        Label percentageLabel = createCell(percentage + "%", 150, "#2d2940", true);

        Button viewBtn = new Button("View");
        viewBtn.setStyle("-fx-background-color: #111111; -fx-text-fill: white; -fx-font-weight: bold; "
                + "-fx-background-radius: 12; -fx-cursor: hand; -fx-font-size: 11; -fx-padding: 4 16; -fx-font-family: 'Trebuchet MS';");
        viewBtn.setMinWidth(80);
        viewBtn.setOnAction(event -> {
            try {
                openResultDetails(result);
            } catch (IOException ex) {
                System.err.println("Failed to open result details: " + ex.getMessage());
            }
        });

        row.getChildren().addAll(examNameLabel, classLabel, marksLabel, percentageLabel, viewBtn);
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

    private void openResultDetails(ExamResult result) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("ResultDetails.fxml"));
        Parent root = loader.load();

        ResultDetailsController controller = loader.getController();
        controller.setResultAndUser(result, user);

        Stage stage = (Stage) resultsList.getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }

    @FXML
    public void onBack(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("DashBoard.fxml"));
        Parent root = loader.load();

        DashBoardController controller = loader.getController();
        controller.setUser(user);

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }
}


