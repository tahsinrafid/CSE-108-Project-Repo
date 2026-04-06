package com.example.examsystemproject;

import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;

public class signupController {
    private String role;
    @FXML
    TextField newNameField;
    @FXML
    TextField newUserField;
    @FXML
    PasswordField newPassField;
    @FXML
    PasswordField confirmPassField;
    @FXML
    Button signupButton;
    @FXML
    Hyperlink loginLink;

    public void signUp(ActionEvent e) {
        String name = newNameField.getText().trim();
        String username = newUserField.getText().trim();
        String password = newPassField.getText();
        String confirm = confirmPassField.getText();

        if (name.isEmpty() || username.isEmpty() || password.isEmpty() || confirm.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Signup Error");
            alert.setHeaderText(null);
            alert.setContentText("All fields must be filled!");
            alert.showAndWait();
            return;
        }

        if (!password.equals(confirm)) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Signup Error");
            alert.setHeaderText(null);
            alert.setContentText("Passwords do not match!");
            alert.showAndWait();
            return;
        }

        Task<Response> task = new Task<>() {
            @Override
            protected Response call() throws Exception {
                NetworkClient client = new NetworkClient("localhost", 5000);

                Request request = new Request();
                request.setType(RequestType.SIGNUP);
                request.getData().put("name", name);
                request.getData().put("username", username);
                request.getData().put("password", password);
                request.getData().put("role", role != null ? role : "student");

                return client.send(request);
            }
        };

        task.setOnSucceeded(event -> {
            Response response = task.getValue();

            if (response == null || !response.isOk()) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Signup Error");
                alert.setHeaderText(null);
                alert.setContentText(response != null ? response.getMessage() : "Signup failed.");
                alert.showAndWait();
                return;
            }

            try {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("login.fxml"));
                Parent root = fxmlLoader.load();

                Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.show();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });

        task.setOnFailed(event -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Network Error");
            alert.setHeaderText(null);
            alert.setContentText("Could not connect to server. Make sure ExamServer is running.");
            alert.showAndWait();
        });

        new Thread(task).start();
    }

    public void handleLogin(ActionEvent e) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("login.fxml"));
        Parent root = fxmlLoader.load();
        Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
        System.out.println("Navigate to login page");
    }

    public void setRole(String role) {
        this.role = role;
    }
}
