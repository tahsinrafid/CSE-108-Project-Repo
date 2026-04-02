package com.example.examsystemproject;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class CreateExamController {

    @FXML
    private TextField examNameField;
    @FXML
    private TextField subjectField;
    @FXML
    private TextField durationField;
    @FXML
    private Label selectedCountLabel;
    @FXML
    private VBox questionsContainer;
    @FXML
    private Button saveExamBtn;

    private User user;
    private final Set<String> selectedQuestionIds = new LinkedHashSet<>();

    @FXML
    public void initialize() {
        selectedCountLabel.setText("0 selected");
    }

    public void setUser(User user) {
        this.user = user;
        loadQuestions();
    }

    private void loadQuestions() {
        questionsContainer.getChildren().clear();
        selectedQuestionIds.clear();
        updateSelectedCount();

        try {
            List<Question> questions = QuesFileManager.loadAllQuestions();
            if (questions.isEmpty()) {
                Label emptyLabel = new Label("No questions found in question bank.");
                emptyLabel.setStyle("-fx-text-fill: #b5b8cf; -fx-font-size: 14; -fx-padding: 20;");
                questionsContainer.getChildren().add(emptyLabel);
                saveExamBtn.setDisable(true);
                return;
            }

            saveExamBtn.setDisable(false);
            for (int i = 0; i < questions.size(); i++) {
                questionsContainer.getChildren().add(createQuestionCard(questions.get(i), i + 1));
            }
        } catch (IOException ex) {
            Label errLabel = new Label("Failed to load questions: " + ex.getMessage());
            errLabel.setStyle("-fx-text-fill: #ff7373; -fx-font-size: 13; -fx-padding: 20;");
            questionsContainer.getChildren().add(errLabel);
            saveExamBtn.setDisable(true);
        }
    }

    private VBox createQuestionCard(Question question, int index) {
        VBox card = new VBox(10);
        card.setStyle("-fx-background-color: #0f1225; -fx-background-radius: 8; "
                + "-fx-border-color: #1f2445; -fx-border-radius: 8; "
                + "-fx-border-width: 1; -fx-padding: 12 14;");
        card.setMaxWidth(Double.MAX_VALUE);

        HBox topRow = new HBox(10);
        topRow.setAlignment(Pos.CENTER_LEFT);

        CheckBox includeBox = new CheckBox("Include");
        includeBox.setStyle("-fx-text-fill: white; -fx-font-size: 13; -fx-font-weight: bold;");
        includeBox.selectedProperty().addListener((obs, oldVal, isNowSelected) -> {
            if (isNowSelected) {
                selectedQuestionIds.add(question.getQuesID());
            } else {
                selectedQuestionIds.remove(question.getQuesID());
            }
            updateSelectedCount();
        });

        Label title = new Label("Q" + index);
        title.setStyle("-fx-text-fill: #58eb34; -fx-font-size: 13; -fx-font-weight: bold;");

        Label qText = new Label(question.getQuesText());
        qText.setStyle("-fx-text-fill: white; -fx-font-size: 14; -fx-font-weight: bold;");
        qText.setWrapText(true);
        HBox.setHgrow(qText, Priority.ALWAYS);

        topRow.getChildren().addAll(includeBox, title);

        GridPane optionsGrid = new GridPane();
        optionsGrid.setHgap(10);
        optionsGrid.setVgap(8);

        String[] letters = {"A", "B", "C", "D"};
        String[] options = {question.getOption1(), question.getOption2(), question.getOption3(), question.getOption4()};

        for (int i = 0; i < 4; i++) {
            Label option = new Label(letters[i] + ". " + options[i]);
            option.setWrapText(true);
            option.setMaxWidth(Double.MAX_VALUE);
            option.setStyle("-fx-text-fill: #d8d9e2; -fx-background-color: rgba(255,255,255,0.05); "
                    + "-fx-padding: 6 10; -fx-background-radius: 5; -fx-font-size: 12;");
            optionsGrid.add(option, i % 2, i / 2);
            GridPane.setHgrow(option, Priority.ALWAYS);
        }

        card.getChildren().addAll(topRow, qText, optionsGrid);
        return card;
    }

    @FXML
    public void saveExam(ActionEvent event) {
        String examName = examNameField.getText().trim();
        String subject = subjectField.getText().trim();
        String durationText = durationField.getText().trim();

        if (examName.isEmpty() || subject.isEmpty() || durationText.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Missing fields", "Please fill exam name, subject and duration.");
            return;
        }

        int durationMinutes;
        try {
            durationMinutes = Integer.parseInt(durationText);
        } catch (NumberFormatException ex) {
            showAlert(Alert.AlertType.ERROR, "Invalid duration", "Duration must be a number in minutes.");
            return;
        }

        if (durationMinutes <= 0 || durationMinutes > 300) {
            showAlert(Alert.AlertType.ERROR, "Invalid duration", "Duration must be between 1 and 300 minutes.");
            return;
        }

        if (selectedQuestionIds.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "No questions selected", "Select at least one question for this exam.");
            return;
        }

        try {
            String examId = ExamFileManager.nextSequentialExamId();
            List<String> selectedIds = new ArrayList<>(selectedQuestionIds);
            String createdBy = user != null ? user.getUsername() : "teacher";

            Exam exam = new Exam(examId, examName, subject, durationMinutes, selectedIds, createdBy);
            ExamFileManager.addExam(exam);

            showAlert(Alert.AlertType.INFORMATION, "Exam created", "Exam " + examId + " saved with " + selectedIds.size() + " questions.");
            clearForm();
        } catch (IOException ex) {
            showAlert(Alert.AlertType.ERROR, "Save failed", "Could not save exam: " + ex.getMessage());
        }
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

    private void clearForm() {
        examNameField.clear();
        subjectField.clear();
        durationField.clear();
        loadQuestions();
    }

    private void updateSelectedCount() {
        selectedCountLabel.setText(selectedQuestionIds.size() + " selected");
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
