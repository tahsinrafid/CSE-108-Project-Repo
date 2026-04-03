package com.example.examsystemproject;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

public class ViewExamsController {

    @FXML
    private Label examsCountLabel;
    @FXML
    private VBox examsContainer;

    private User user;

    @FXML
    public void initialize() {
        examsCountLabel.setText("Loading exams...");
    }

    public void setUser(User user) {
        this.user = user;
        loadExams();
    }

    private void loadExams() {
        examsContainer.getChildren().clear();

        try {
            List<Exam> exams = ExamFileManager.loadAllExams();
            examsCountLabel.setText(exams.size() + " Exam" + (exams.size() != 1 ? "s" : ""));

            if (exams.isEmpty()) {
                Label emptyLabel = new Label("No exams created yet. Click 'Create Exam' to add one.");
                emptyLabel.setStyle("-fx-text-fill: #666688; -fx-font-size: 14; -fx-padding: 20;");
                examsContainer.getChildren().add(emptyLabel);
                return;
            }

            for (int i = exams.size() - 1; i >= 0; i--) {
                examsContainer.getChildren().add(createExamCard(exams.get(i), exams.size() - i));
            }
        } catch (IOException ex) {
            examsCountLabel.setText("Error");
            Label errLabel = new Label("Failed to load exams: " + ex.getMessage());
            errLabel.setStyle("-fx-text-fill: #c44536; -fx-font-size: 13; -fx-padding: 20;");
            examsContainer.getChildren().add(errLabel);
        }
    }

    private VBox createExamCard(Exam exam, int displayIndex) {
        VBox card = new VBox(10);
        card.setStyle("-fx-background-color: #0f1225; -fx-background-radius: 8; "
                + "-fx-border-color: #1e2050; -fx-border-radius: 8; "
                + "-fx-border-width: 1; -fx-padding: 14 16;");
        card.setMaxWidth(Double.MAX_VALUE);

        HBox header = new HBox(10);
        header.setAlignment(Pos.CENTER_LEFT);

        Label examNumber = new Label("Exam " + displayIndex);
        examNumber.setStyle("-fx-text-fill: #58eb34; -fx-font-size: 13; -fx-font-weight: bold;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label examId = new Label(exam.getExamId());
        examId.setStyle("-fx-text-fill: #8589a8; -fx-font-size: 12; -fx-font-weight: bold;");

        header.getChildren().addAll(examNumber, spacer, examId);

        Label examTitle = new Label(exam.getExamName());
        examTitle.setStyle("-fx-text-fill: white; -fx-font-size: 16; -fx-font-weight: bold;");
        examTitle.setWrapText(true);

        GridPane details = new GridPane();
        details.setHgap(10);
        details.setVgap(8);

        ColumnConstraints keyCol = new ColumnConstraints();
        keyCol.setPrefWidth(110);
        ColumnConstraints valCol = new ColumnConstraints();
        valCol.setHgrow(Priority.ALWAYS);
        details.getColumnConstraints().addAll(keyCol, valCol);

        addDetailRow(details, 0, "Subject", exam.getSubject());
        addDetailRow(details, 1, "Class", exam.getClassLevel());
        addDetailRow(details, 2, "Marks", String.valueOf(exam.getMarks()));
        addDetailRow(details, 3, "Duration", exam.getDurationMinutes() + " minutes");
        addDetailRow(details, 4, "Questions", String.valueOf(exam.getQuestionIds().size()));
        addDetailRow(details, 5, "Created By", exam.getCreatedBy());

        card.getChildren().addAll(header, examTitle, details);
        return card;
    }

    private void addDetailRow(GridPane grid, int row, String key, String value) {
        Label keyLabel = new Label(key + ":");
        keyLabel.setStyle("-fx-text-fill: #a8acce; -fx-font-size: 12; -fx-font-weight: bold;");

        Label valLabel = new Label(value == null || value.trim().isEmpty() ? "-" : value);
        valLabel.setStyle("-fx-text-fill: #d8d9e2; -fx-font-size: 12;");
        valLabel.setWrapText(true);

        grid.add(keyLabel, 0, row);
        grid.add(valLabel, 1, row);
    }

    @FXML
    public void openCreateExam(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("CreateExam.fxml"));
        Parent root = loader.load();

        CreateExamController controller = loader.getController();
        controller.setUser(user);

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }

    @FXML
    public void refreshExams() {
        loadExams();
    }

    @FXML
    public void goBack(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Teacher_DashBoard.fxml"));
        Parent root = loader.load();

        TeacherDashBoardController controller = loader.getController();
        controller.setUser(user);

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }
}
