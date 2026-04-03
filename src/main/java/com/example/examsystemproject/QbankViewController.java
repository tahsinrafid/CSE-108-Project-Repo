package com.example.examsystemproject;

import java.io.IOException;
import java.util.List;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class QbankViewController {

    @FXML
    private Button addQuesBtn;
    @FXML
    private Label quesCountLabel;
    @FXML
    private VBox quesContainer;

    private User user;
    private boolean isTeacher;

    @FXML
    public void initialize() {

    }

    public void setUser(User user) {
        this.user = user;
        this.isTeacher = user != null && "teacher".equalsIgnoreCase(user.getRole());

        if (isTeacher) {
            addQuesBtn.setVisible(true);
            addQuesBtn.setManaged(true);
        }
        loadQuestions();
    }

    private void loadQuestions() {
        quesContainer.getChildren().clear();
        try {
            List<Question> questions = QuesFileManager.loadAllQuestions();

            if (questions.isEmpty()) {
                Label emptyLabel = new Label(isTeacher
                        ? "No questions yet. Click '+ Add Question' to get started."
                        : "No questions available yet. Check back later.");
                emptyLabel.setStyle("-fx-text-fill: #6d6780; -fx-font-size: 14; -fx-padding: 20; -fx-font-family: 'Trebuchet MS';");
                quesContainer.getChildren().add(emptyLabel);
                quesCountLabel.setText("0 Questions");
            } else {
                int count = questions.size();
                quesCountLabel.setText(count + " Question" + (count != 1 ? "s" : ""));
                for (int i = 0; i < count; i++) {
                    quesContainer.getChildren().add(createQuestionCard(questions.get(i), i + 1));
                }
            }
        } catch (IOException e) {
            Label errLabel = new Label("Failed to load questions: " + e.getMessage());
            errLabel.setStyle("-fx-text-fill: #b53a3a; -fx-font-size: 13; -fx-padding: 20; -fx-font-family: 'Trebuchet MS';");
            quesContainer.getChildren().add(errLabel);
            quesCountLabel.setText("Error");
        }
    }

    private VBox createQuestionCard(Question q, int index) {
        VBox card = new VBox(10);
        card.setStyle("-fx-background-color: #f7f6fb; -fx-background-radius: 8; "
                + "-fx-border-color: #dedbea; -fx-border-radius: 8; "
                + "-fx-border-width: 1; -fx-padding: 14 16;");
        card.setMaxWidth(Double.MAX_VALUE);

        HBox header = new HBox(8);
        header.setAlignment(Pos.TOP_LEFT);

        Label qNum = new Label("Q" + index + ".");
        qNum.setStyle("-fx-text-fill: #3d2b69; -fx-font-weight: bold; -fx-font-size: 15; -fx-font-family: 'Trebuchet MS';");
        qNum.setMinWidth(36);

        Label qText = new Label(q.getQuesText());
        qText.setStyle("-fx-text-fill: #2f2349; -fx-font-size: 14; -fx-font-weight: bold; -fx-font-family: 'Trebuchet MS';");
        qText.setWrapText(true);
        HBox.setHgrow(qText, Priority.ALWAYS);
        qText.setMaxWidth(Double.MAX_VALUE);

        header.getChildren().addAll(qNum, qText);

        if (isTeacher) {
            Button editBtn = new Button("Edit");
            editBtn.setStyle("-fx-background-color: #f3b53f; -fx-text-fill: #2f2349; -fx-font-weight: bold; "
                    + "-fx-background-radius: 12; -fx-cursor: hand; -fx-font-size: 11; -fx-padding: 4 12; -fx-font-family: 'Trebuchet MS';");
            editBtn.setOnAction(e -> openEditQuestion(q));

            Button deleteBtn = new Button("Delete");
            deleteBtn.setStyle("-fx-background-color: #dc4b4b; -fx-text-fill: white; -fx-font-weight: bold; "
                    + "-fx-background-radius: 12; -fx-cursor: hand; -fx-font-size: 11; -fx-padding: 4 12; -fx-font-family: 'Trebuchet MS';");
            deleteBtn.setOnAction(e -> deleteQuestion(q));

            header.getChildren().addAll(editBtn, deleteBtn);
        }

        GridPane optGrid = new GridPane();
        optGrid.setHgap(10);
        optGrid.setVgap(8);
        optGrid.setMaxWidth(Double.MAX_VALUE);

        ColumnConstraints colA = new ColumnConstraints();
        colA.setPercentWidth(50);
        ColumnConstraints colB = new ColumnConstraints();
        colB.setPercentWidth(50);
        optGrid.getColumnConstraints().addAll(colA, colB);

        String[] letters = {"A", "B", "C", "D"};
        String[] optTexts = {q.getOption1(), q.getOption2(), q.getOption3(), q.getOption4()};

        for (int i = 0; i < 4; i++) {
            boolean correct = letters[i].equalsIgnoreCase(q.getCorrectAns());
            Label optLabel = new Label(letters[i] + ".  " + optTexts[i]);
            optLabel.setWrapText(true);
            optLabel.setMaxWidth(Double.MAX_VALUE);

            if (correct) {
                optLabel.setStyle("-fx-text-fill: #214f2a; -fx-background-color: #dff5e6; "
                        + "-fx-padding: 6 12; -fx-background-radius: 5; -fx-font-size: 13; -fx-font-weight: bold; -fx-font-family: 'Trebuchet MS';");
            } else {
                optLabel.setStyle("-fx-text-fill: #4e4a60; -fx-background-color: #efedf6; "
                        + "-fx-padding: 6 12; -fx-background-radius: 5; -fx-font-size: 13; -fx-font-family: 'Trebuchet MS';");
            }
            optGrid.add(optLabel, i % 2, i / 2);
        }
        card.getChildren().addAll(header, optGrid);

        if (isTeacher) {
            Label correctHint = new Label("✓  Correct Answer: " + q.getCorrectAns());
            correctHint.setStyle("-fx-text-fill: #2a6d36; -fx-font-size: 11; -fx-font-weight: bold; -fx-opacity: 0.85; -fx-font-family: 'Trebuchet MS';");
            card.getChildren().add(correctHint);
        } else {
            Label correctHint = new Label("Correct Answer: " + q.getCorrectAns());
            correctHint.setStyle("-fx-text-fill: #2a6d36; -fx-font-size: 11; -fx-font-weight: bold; -fx-opacity: 0.9; -fx-font-family: 'Trebuchet MS';");

            Button reportBtn = new Button("Report Doubt");
            reportBtn.setStyle("-fx-background-color: #f3b53f; -fx-text-fill: #2f2349; -fx-font-weight: bold; -fx-background-radius: 12; -fx-cursor: hand; -fx-font-size: 11; -fx-padding: 4 12; -fx-font-family: 'Trebuchet MS';");
            reportBtn.setOnAction(event -> openReportDialog(q));

            HBox reportRow = new HBox(10, correctHint, reportBtn);
            reportRow.setAlignment(Pos.CENTER_LEFT);
            card.getChildren().add(reportRow);
        }

        return card;
    }

    private void openReportDialog(Question q) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Report Question");
        dialog.setHeaderText("Report a doubt about this question");
        dialog.setContentText("Write your issue or clarification request:");

        dialog.showAndWait().ifPresent(text -> {
            String details = text.trim();
            if (details.isEmpty()) {
                return;
            }
            String username = user != null ? user.getUsername() : "student";
            try {
                QuestionReportFileManager.addReport(new QuestionReport(q.getQuesID(), username, details));
                Alert info = new Alert(Alert.AlertType.INFORMATION);
                info.setTitle("Reported");
                info.setHeaderText(null);
                info.setContentText("Your report was submitted.");
                info.showAndWait();
            } catch (IOException ex) {
                Alert err = new Alert(Alert.AlertType.ERROR);
                err.setTitle("Report failed");
                err.setHeaderText(null);
                err.setContentText(ex.getMessage());
                err.showAndWait();
            }
        });
    }


    @FXML
    public void openAddQuestion(ActionEvent e) {
        showQuestionDialog(null);
    }

    private void openEditQuestion(Question q) {
        showQuestionDialog(q);
    }

    private void showQuestionDialog(Question existing) {
        boolean isEdit = (existing != null);
        Question editableQuestion = existing;

        Stage dialog = new Stage();
        dialog.setTitle(isEdit ? "Edit Question" : "Add New Question");
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setResizable(false);

        VBox form = new VBox(10);
        form.setPadding(new Insets(24));
        form.setStyle("-fx-background-color: #f7f6fb;");
        form.setPrefWidth(520);

        Label titleLabel = new Label(isEdit ? "Edit Question" : "Add New Question");
        titleLabel.setStyle("-fx-text-fill: #2f2349; -fx-font-size: 17; -fx-font-weight: bold; -fx-font-family: 'Trebuchet MS';");

        Label qLabel = sectionLabel("Question Text");
        TextField quesField = dialogField("Type the question here…");
        quesField.setPrefWidth(472);
        if (isEdit) {
            if (editableQuestion == null) {
                return;
            }
            quesField.setText(editableQuestion.getQuesText());
        }

        Label optLabel = sectionLabel("Answer Options");
        TextField opt1 = dialogField("Option A");
        TextField opt2 = dialogField("Option B");
        TextField opt3 = dialogField("Option C");
        TextField opt4 = dialogField("Option D");
        if (isEdit) {
            if (editableQuestion == null) {
                return;
            }
            opt1.setText(editableQuestion.getOption1());
            opt2.setText(editableQuestion.getOption2());
            opt3.setText(editableQuestion.getOption3());
            opt4.setText(editableQuestion.getOption4());
        }

        GridPane optGrid = new GridPane();
        optGrid.setHgap(10);
        optGrid.setVgap(8);

        String[] letLabels = {"A.", "B.", "C.", "D."};
        TextField[] fields = {opt1, opt2, opt3, opt4};
        for (int i = 0; i < 4; i++) {
            Label l = new Label(letLabels[i]);
            l.setStyle("-fx-text-fill: #3d2b69; -fx-font-weight: bold; -fx-font-size: 13; -fx-font-family: 'Trebuchet MS';");
            l.setMinWidth(22);
            fields[i].setPrefWidth(420);
            optGrid.add(l, 0, i);
            optGrid.add(fields[i], 1, i);
        }
        ColumnConstraints labelCol = new ColumnConstraints(28);
        ColumnConstraints fieldCol = new ColumnConstraints();
        fieldCol.setHgrow(Priority.ALWAYS);
        optGrid.getColumnConstraints().addAll(labelCol, fieldCol);

        Label correctLabel = sectionLabel("Correct Answer (A / B / C / D)");
        TextField correctField = dialogField("Enter A, B, C, or D");
        correctField.setPrefWidth(472);
        if (isEdit) {
            if (editableQuestion == null) {
                return;
            }
            correctField.setText(editableQuestion.getCorrectAns());
        }

        Label errLabel = new Label("");
        errLabel.setStyle("-fx-text-fill: #b53a3a; -fx-font-size: 12; -fx-font-family: 'Trebuchet MS';");
        errLabel.setWrapText(true);

        HBox btnRow = new HBox(10);
        btnRow.setAlignment(Pos.CENTER_RIGHT);
        VBox.setMargin(btnRow, new Insets(6, 0, 0, 0));

        Button cancelBtn = new Button("Cancel");
        cancelBtn.setStyle("-fx-background-color: #e7e4f0; -fx-text-fill: #43395f; -fx-font-weight: bold; "
                + "-fx-background-radius: 14; -fx-cursor: hand; -fx-padding: 8 20; -fx-font-size: 13; -fx-font-family: 'Trebuchet MS';");
        cancelBtn.setOnAction(ev -> dialog.close());

        Button saveBtn = new Button(isEdit ? "Save Changes" : "Add Question");
        saveBtn.setStyle("-fx-background-color: #111111; -fx-text-fill: white; -fx-font-weight: bold; "
                + "-fx-background-radius: 14; -fx-cursor: hand; -fx-padding: 8 20; -fx-font-size: 13; -fx-font-family: 'Trebuchet MS';");
        saveBtn.setOnAction(ev -> {
            String qText = quesField.getText().trim();
            String o1 = opt1.getText().trim();
            String o2 = opt2.getText().trim();
            String o3 = opt3.getText().trim();
            String o4 = opt4.getText().trim();
            String correct = correctField.getText().trim().toUpperCase();

            if (qText.isEmpty() || o1.isEmpty() || o2.isEmpty() || o3.isEmpty() || o4.isEmpty() || correct.isEmpty()) {
                errLabel.setText("All fields are required.");
                return;
            }
            if (!correct.equals("A") && !correct.equals("B") && !correct.equals("C") && !correct.equals("D")) {
                errLabel.setText("Correct answer must be A, B, C, or D.");
                return;
            }
            try {
                if (isEdit) {
                    if (editableQuestion == null) {
                        errLabel.setText("Could not load the question for editing.");
                        return;
                    }
                    Question updated = new Question(editableQuestion.getQuesID(), qText, o1, o2, o3, o4, correct);
                    QuesFileManager.updateQues(updated);
                } else {
                    String id = QuesFileManager.nextSequentialQuesId();
                    Question nq = new Question(id, qText, o1, o2, o3, o4, correct);
                    QuesFileManager.addQues(nq);
                }
                dialog.close();
                loadQuestions();
            } catch (IOException ex) {
                errLabel.setText("Error saving: " + ex.getMessage());
            }
        });

        btnRow.getChildren().addAll(cancelBtn, saveBtn);

        form.getChildren().addAll(
                titleLabel,
                qLabel, quesField,
                optLabel, optGrid,
                correctLabel, correctField,
                errLabel, btnRow
        );

        Scene scene = new Scene(form);
        dialog.setScene(scene);
        dialog.showAndWait();
    }


    private void deleteQuestion(Question q) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Delete Question");
        confirm.setHeaderText("Delete this question?");
        confirm.setContentText("\"" + q.getQuesText() + "\"\n\nThis cannot be undone.");
        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    QuesFileManager.deleteQues(q.getQuesID());
                    loadQuestions();
                } catch (IOException ex) {
                    Alert err = new Alert(Alert.AlertType.ERROR);
                    err.setTitle("Error");
                    err.setHeaderText("Could not delete question");
                    err.setContentText(ex.getMessage());
                    err.showAndWait();
                }
            }
        });
    }


    @FXML
    public void goBack(ActionEvent event) throws IOException {
        boolean teacher = user != null && "teacher".equalsIgnoreCase(user.getRole());
        String fxml = teacher ? "Teacher_DashBoard.fxml" : "DashBoard.fxml";

        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
        Parent root = loader.load();

        if (teacher) {
            ((TeacherDashBoardController) loader.getController()).setUser(user);
        } else {
            ((DashBoardController) loader.getController()).setUser(user);
        }

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }


    private TextField dialogField(String prompt) {
        TextField tf = new TextField();
        tf.setPromptText(prompt);
        tf.setStyle("-fx-background-color: #ffffff; -fx-text-fill: #2f2349; "
                + "-fx-prompt-text-fill: #9d98ad; -fx-background-radius: 8; "
                + "-fx-border-color: #d3cfdd; -fx-border-radius: 8; -fx-border-width: 1; -fx-padding: 8; -fx-font-family: 'Trebuchet MS';");
        return tf;
    }

    private Label sectionLabel(String text) {
        Label l = new Label(text);
        l.setStyle("-fx-text-fill: #5f5a73; -fx-font-size: 12; -fx-font-weight: bold; -fx-font-family: 'Trebuchet MS';");
        VBox.setMargin(l, new Insets(4, 0, 0, 0));
        return l;
    }
}
