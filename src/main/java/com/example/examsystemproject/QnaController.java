package com.example.examsystemproject;

import java.io.IOException;
import java.util.List;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class QnaController {
    @FXML
    private TextArea questionInput;
    @FXML
    private ChoiceBox<String> audienceChoice;
    @FXML
    private TextField teacherTargetField;
    @FXML
    private VBox postContainer;
    @FXML
    private Label postCountLabel;

    private User user;
    private boolean teacherMode;

    @FXML
    public void initialize() {
        if (audienceChoice != null) {
            audienceChoice.getItems().setAll("Everyone", "Teachers", "Private teacher");
            audienceChoice.setValue("Everyone");
        }
    }

    public void setUser(User user) {
        this.user = user;
        this.teacherMode = user != null && "teacher".equalsIgnoreCase(user.getRole());
        loadPosts();
    }

    @FXML
    public void onSubmitQuestion() {
        String text = questionInput.getText() == null ? "" : questionInput.getText().trim();
        if (text.isEmpty()) {
            showAlert("Question needed", "Write a question before submitting.");
            return;
        }

        String audience = audienceChoice.getValue() == null ? "Everyone" : audienceChoice.getValue();
        String assignedTeacher = teacherTargetField != null && teacherTargetField.getText() != null
                ? teacherTargetField.getText().trim()
                : "";
        if ("Private teacher".equalsIgnoreCase(audience) && assignedTeacher.isEmpty()) {
            showAlert("Teacher needed", "Enter the dedicated teacher username for a private message.");
            return;
        }
        String username = user != null ? user.getUsername() : "student";
        String role = user != null ? user.getRole() : "student";

        try {
            String id = QnaFileManager.nextSequentialPostId();
            QnaFileManager.addPost(new QnaPost(id, text, username, role, audience, assignedTeacher, "", ""));
            questionInput.clear();
            audienceChoice.setValue("Everyone");
            if (teacherTargetField != null) {
                teacherTargetField.clear();
            }
            loadPosts();
        } catch (IOException ex) {
            showAlert("Save failed", "Could not submit your question: " + ex.getMessage());
        }
    }

    @FXML
    public void onBack() throws IOException {
        boolean isTeacher = teacherMode;
        String fxml = isTeacher ? "Teacher_DashBoard.fxml" : "DashBoard.fxml";
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
        Parent root = loader.load();
        if (isTeacher) {
            TeacherDashBoardController controller = loader.getController();
            controller.setUser(user);
        } else {
            DashBoardController controller = loader.getController();
            controller.setUser(user);
        }
        Stage stage = (Stage) questionInput.getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }

    private void loadPosts() {
        if (postContainer == null || postCountLabel == null) {
            return;
        }

        postContainer.getChildren().clear();
        try {
            List<QnaPost> posts = QnaFileManager.loadAllPosts();
            String currentRole = user != null ? user.getRole() : "student";
            String currentUsername = user != null ? user.getUsername() : "student";
            int visibleCount = 0;

            for (QnaPost post : posts) {
                if (!canSeePost(post, currentRole, currentUsername)) {
                    continue;
                }
                postContainer.getChildren().add(createPostCard(post));
                visibleCount++;
            }

            postCountLabel.setText(visibleCount + " visible post" + (visibleCount != 1 ? "s" : ""));
            if (visibleCount == 0) {
                postContainer.getChildren().add(infoLabel("No QNA posts yet."));
            }
        } catch (IOException ex) {
            postContainer.getChildren().add(infoLabel("Failed to load QNA posts: " + ex.getMessage()));
            postCountLabel.setText("Error");
        }
    }

    private boolean canSeePost(QnaPost post, String role, String username) {
        String visibility = post.getVisibility();
        if ("Private teacher".equalsIgnoreCase(visibility)) {
            boolean isAuthor = username != null && username.equalsIgnoreCase(post.getAuthorUsername());
            boolean isAssignedTeacher = "teacher".equalsIgnoreCase(role)
                    && username != null
                    && username.equalsIgnoreCase(post.getAssignedTeacherUsername());
            return isAuthor || isAssignedTeacher;
        }
        return true;
    }

    private VBox createPostCard(QnaPost post) {
        VBox card = new VBox(10);
        card.setPadding(new Insets(16));
        card.setStyle("-fx-background-color: rgba(15, 18, 37, 0.96); -fx-background-radius: 10; -fx-border-color: rgba(88, 235, 52, 0.18); -fx-border-radius: 10;");

        HBox header = new HBox(10);
        header.setAlignment(Pos.CENTER_LEFT);

        Label title = new Label(post.getQuestionText());
        title.setWrapText(true);
        title.setStyle("-fx-text-fill: white; -fx-font-size: 14; -fx-font-weight: bold; -fx-font-family: 'Trebuchet MS';");
        HBox.setHgrow(title, Priority.ALWAYS);

        Label scope = new Label(post.getVisibility());
        scope.setStyle("-fx-text-fill: #9ed6ff; -fx-font-size: 11; -fx-background-color: rgba(45, 90, 140, 0.28); -fx-padding: 4 10; -fx-background-radius: 14;");

        header.getChildren().addAll(title, scope);

        Label meta = new Label("Asked by " + post.getAuthorUsername() + " (" + post.getAuthorRole() + ")");
        meta.setStyle("-fx-text-fill: #8e9ab6; -fx-font-size: 11; -fx-font-family: 'Trebuchet MS';");

        HBox detailRow = new HBox(10);
        detailRow.setAlignment(Pos.CENTER_LEFT);
        if ("Private teacher".equalsIgnoreCase(post.getVisibility())) {
            Label privateTag = new Label("Private to: " + post.getAssignedTeacherUsername());
            privateTag.setStyle("-fx-text-fill: #ffd7a8; -fx-font-size: 11; -fx-background-color: rgba(245, 156, 26, 0.16); -fx-padding: 4 10; -fx-background-radius: 14;");
            detailRow.getChildren().add(privateTag);
        }

        VBox answerBox = new VBox(6);
        Label answerTitle = new Label(post.isAnswered() ? "Answer" : "No answer yet");
        answerTitle.setStyle("-fx-text-fill: #58eb34; -fx-font-size: 12; -fx-font-weight: bold; -fx-font-family: 'Trebuchet MS';");
        Label answerText = new Label(post.isAnswered() ? post.getAnswerText() : "Waiting for an answer.");
        answerText.setWrapText(true);
        answerText.setStyle("-fx-text-fill: #d6e5f3; -fx-font-size: 12; -fx-font-family: 'Trebuchet MS';");
        answerBox.getChildren().addAll(answerTitle, answerText);

        card.getChildren().addAll(header, meta, detailRow, answerBox);

        boolean canAnswer = canAnswerPost(post, user != null ? user.getRole() : "student", user != null ? user.getUsername() : "student");
        if (canAnswer && !post.isAnswered()) {
            Button answerBtn = new Button("Answer");
            answerBtn.setStyle("-fx-background-color: #58eb34; -fx-text-fill: #0b1220; -fx-font-weight: bold; -fx-background-radius: 5; -fx-cursor: hand; -fx-font-size: 11; -fx-padding: 5 12;");
            answerBtn.setOnAction(event -> openAnswerDialog(post));
            card.getChildren().add(answerBtn);
        } else if (!canAnswer) {
            Label readOnly = new Label("Read only for your role.");
            readOnly.setStyle("-fx-text-fill: #8e9ab6; -fx-font-size: 11; -fx-font-style: italic;");
            card.getChildren().add(readOnly);
        }

        return card;
    }

    private boolean canAnswerPost(QnaPost post, String role, String username) {
        if ("Private teacher".equalsIgnoreCase(post.getVisibility())) {
            return "teacher".equalsIgnoreCase(role)
                    && username != null
                    && username.equalsIgnoreCase(post.getAssignedTeacherUsername());
        }
        if ("Teachers".equalsIgnoreCase(post.getVisibility())) {
            return "teacher".equalsIgnoreCase(role);
        }
        return true;
    }

    private void openAnswerDialog(QnaPost post) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Answer QNA");
        dialog.setHeaderText(post.getQuestionText());
        dialog.setContentText("Write the answer:");

        dialog.showAndWait().ifPresent(text -> {
            String answer = text.trim();
            if (answer.isEmpty()) {
                return;
            }
            String answeredBy = user != null ? user.getUsername() : "teacher";
            try {
                QnaFileManager.updatePost(new QnaPost(
                        post.getPostId(),
                        post.getQuestionText(),
                        post.getAuthorUsername(),
                        post.getAuthorRole(),
                        post.getVisibility(),
                    post.getAssignedTeacherUsername(),
                        answer,
                        answeredBy));
                loadPosts();
            } catch (IOException ex) {
                showAlert("Answer failed", "Could not save the answer: " + ex.getMessage());
            }
        });
    }

    private Label infoLabel(String text) {
        Label label = new Label(text);
        label.setWrapText(true);
        label.setStyle("-fx-text-fill: #d7e8f6; -fx-font-size: 12; -fx-font-family: 'Trebuchet MS';");
        return label;
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
