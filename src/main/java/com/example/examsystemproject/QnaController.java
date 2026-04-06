package com.example.examsystemproject;

import java.io.IOException;
import java.net.URL;
import java.util.List;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class QnaController {
    @FXML
    private VBox postContainer;
    @FXML
    private Label postCountLabel;

    private User user;
    private boolean teacherMode;

    @FXML
    public void initialize() {}

    public void setUser(User user) {
        this.user = user;
        this.teacherMode = user != null && "teacher".equalsIgnoreCase(user.getRole());
        loadPosts();
    }

    @FXML
    public void onOpenQuestionModal() {
        Stage owner = (Stage) postContainer.getScene().getWindow();

        TextArea questionEditor = new TextArea();
        questionEditor.setPromptText("Post your doubt publicly. Teachers and students can comment.");
        questionEditor.setPrefRowCount(6);
        questionEditor.setWrapText(true);
        questionEditor.getStyleClass().add("qna-question-input");

        Button cancelButton = new Button("Cancel");
        cancelButton.getStyleClass().add("qna-secondary-btn");

        Button postButton = new Button("Post Question");
        postButton.getStyleClass().add("qna-primary-btn");

        HBox actions = new HBox(10, cancelButton, postButton);
        actions.setAlignment(Pos.CENTER_RIGHT);

        Label helper = new Label("All questions and comments are public in this board.");
        helper.getStyleClass().add("qna-info-text");

        VBox dialogRoot = new VBox(10,
                createSectionTitle("Ask the community"),
                questionEditor,
                helper,
                actions);
        dialogRoot.getStyleClass().add("qna-compose-card");
        dialogRoot.getStyleClass().add("qna-modal-card");
        dialogRoot.setMaxWidth(560);

        StackPane overlayRoot = new StackPane(dialogRoot);
        overlayRoot.getStyleClass().add("qna-modal-overlay");

        Scene scene = new Scene(overlayRoot, owner.getWidth(), owner.getHeight());
        scene.setFill(Color.TRANSPARENT);
        URL cssUrl = getClass().getResource("Dashboard.css");
        if (cssUrl != null) {
            scene.getStylesheets().add(cssUrl.toExternalForm());
        }

        Stage modal = new Stage();
        modal.initOwner(owner);
        modal.initModality(Modality.WINDOW_MODAL);
        modal.initStyle(StageStyle.TRANSPARENT);
        modal.setResizable(false);
        modal.setTitle("Post Questions");
        modal.setScene(scene);
        modal.setX(owner.getX());
        modal.setY(owner.getY());

        cancelButton.setOnAction(event -> modal.close());
        postButton.setOnAction(event -> {
            if (submitQuestionText(questionEditor.getText())) {
                modal.close();
            }
        });
        overlayRoot.setOnMouseClicked(event -> modal.close());
        dialogRoot.setOnMouseClicked(event -> event.consume());

        modal.showAndWait();
    }

    private boolean submitQuestionText(String questionText) {
        String text = questionText == null ? "" : questionText.trim();
        if (text.isEmpty()) {
            showAlert("Question needed", "Write a question before submitting.");
            return false;
        }

        String username = user != null ? user.getUsername() : "student";
        String role = user != null ? user.getRole() : "student";

        try {
            String id = QnaFileManager.nextSequentialPostId();
            QnaFileManager.addPost(new QnaPost(id, text, username, role, null));
            loadPosts();
            return true;
        } catch (IOException ex) {
            showAlert("Save failed", "Could not submit your question: " + ex.getMessage());
            return false;
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
        Stage stage = (Stage) postContainer.getScene().getWindow();
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
            int postCount = 0;

            for (int i = posts.size() - 1; i >= 0; i--) {
                postContainer.getChildren().add(createPostCard(posts.get(i)));
                postCount++;
            }

            postCountLabel.setText(postCount + " question" + (postCount != 1 ? "s" : ""));
            if (postCount == 0) {
                postContainer.getChildren().add(infoLabel("No community questions yet. Ask the first one."));
            }
        } catch (IOException ex) {
            postContainer.getChildren().add(infoLabel("Failed to load QNA posts: " + ex.getMessage()));
            postCountLabel.setText("Error");
        }
    }

    private VBox createPostCard(QnaPost post) {
        VBox card = new VBox(10);
        card.setPadding(new Insets(16));
        card.getStyleClass().add("qna-post-card");

        HBox header = new HBox(8);
        header.setAlignment(Pos.CENTER_LEFT);

        Label title = new Label(post.getQuestionText());
        title.setWrapText(true);
        title.getStyleClass().add("qna-post-title");
        HBox.setHgrow(title, Priority.ALWAYS);

        Label postTag = new Label("Public");
        postTag.getStyleClass().add("qna-public-tag");

        header.getChildren().addAll(title, postTag);

        HBox askerRow = new HBox(8);
        askerRow.setAlignment(Pos.CENTER_LEFT);
        Label askedBy = new Label("Asked by " + post.getAuthorUsername());
        askedBy.getStyleClass().add("qna-meta");
        Label askerRole = createRoleChip(post.getAuthorRole());
        askerRow.getChildren().addAll(askedBy, askerRole);

        VBox commentsBox = new VBox(8);
        commentsBox.getChildren().add(createSectionTitle("Comments (" + post.getCommentCount() + ")"));
        if (post.getComments().isEmpty()) {
            Label empty = new Label("No comments yet. Be the first to help.");
            empty.getStyleClass().add("qna-comment-empty");
            commentsBox.getChildren().add(empty);
        } else {
            for (int i = 0; i < post.getComments().size(); i++) {
                commentsBox.getChildren().add(createCommentCard(post, i, post.getComments().get(i)));
            }
        }

        VBox composer = new VBox(6);
        TextArea commentInput = new TextArea();
        commentInput.setPromptText("Write a public comment...");
        commentInput.setPrefRowCount(2);
        commentInput.setWrapText(true);
        commentInput.getStyleClass().add("qna-comment-input");

        HBox composerActions = new HBox();
        composerActions.setAlignment(Pos.CENTER_RIGHT);
        Button commentButton = new Button("Post Comment");
        commentButton.getStyleClass().add("qna-comment-btn");
        commentButton.setOnAction(event -> submitComment(post, commentInput));
        composerActions.getChildren().add(commentButton);
        composer.getChildren().addAll(commentInput, composerActions);

        card.getChildren().addAll(header, askerRow, commentsBox, composer);

        return card;
    }

    private VBox createCommentCard(QnaPost post, int commentIndex, QnaComment comment) {
        VBox commentCard = new VBox(5);
        commentCard.getStyleClass().add("qna-comment-card");

        HBox authorRow = new HBox(8);
        authorRow.setAlignment(Pos.CENTER_LEFT);
        Label author = new Label(comment.getAuthorUsername());
        author.getStyleClass().add("qna-comment-author");
        Label roleChip = createRoleChip(comment.getAuthorRole());
        authorRow.getChildren().addAll(author, roleChip);

        if (canDeleteComment(comment)) {
            HBox.setHgrow(author, Priority.ALWAYS);
            Button deleteButton = new Button("Delete");
            deleteButton.getStyleClass().add("qna-delete-comment-btn");
            deleteButton.setOnAction(event -> deleteComment(post, commentIndex));
            authorRow.getChildren().add(deleteButton);
        }

        Label text = new Label(comment.getCommentText());
        text.setWrapText(true);
        text.getStyleClass().add("qna-comment-text");

        commentCard.getChildren().addAll(authorRow, text);
        return commentCard;
    }

    private boolean canDeleteComment(QnaComment comment) {
        if (comment == null || user == null || user.getUsername() == null) {
            return false;
        }

        if (teacherMode) {
            return true;
        }

        return user.getUsername().equalsIgnoreCase(comment.getAuthorUsername());
    }

    private void deleteComment(QnaPost post, int commentIndex) {
        if (post == null || commentIndex < 0) {
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Delete Comment");
        confirm.setHeaderText("Remove this comment?");
        confirm.setContentText("This action cannot be undone.");

        if (confirm.showAndWait().orElse(ButtonType.CANCEL) != ButtonType.OK) {
            return;
        }

        try {
            boolean deleted = QnaFileManager.deleteComment(post.getPostId(), commentIndex);
            if (!deleted) {
                showAlert("Delete failed", "Could not delete the comment. Please refresh and try again.");
                return;
            }
            loadPosts();
        } catch (IOException ex) {
            showAlert("Delete failed", "Could not delete the comment: " + ex.getMessage());
        }
    }

    private Label createSectionTitle(String text) {
        Label label = new Label(text);
        label.getStyleClass().add("qna-section-title");
        return label;
    }

    private Label createRoleChip(String role) {
        String normalized = role == null ? "student" : role.trim().toLowerCase();
        boolean teacher = "teacher".equals(normalized);

        Label chip = new Label(teacher ? "Teacher" : "Student");
        chip.getStyleClass().add("qna-role-chip");
        chip.getStyleClass().add(teacher ? "qna-role-teacher" : "qna-role-student");
        return chip;
    }

    private void submitComment(QnaPost post, TextArea commentInput) {
        if (post == null || commentInput == null) {
            return;
        }

        String text = commentInput.getText() == null ? "" : commentInput.getText().trim();
        if (text.isEmpty()) {
            showAlert("Comment needed", "Write a comment before posting.");
            return;
        }

        String username = user != null ? user.getUsername() : "community_user";
        String role = user != null ? user.getRole() : "student";

        try {
            QnaFileManager.addComment(post.getPostId(), new QnaComment(username, role, text));
            loadPosts();
        } catch (IOException ex) {
            showAlert("Comment failed", "Could not save your comment: " + ex.getMessage());
        }
    }

    private Label infoLabel(String text) {
        Label label = new Label(text);
        label.setWrapText(true);
        label.getStyleClass().add("qna-info-text");
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
