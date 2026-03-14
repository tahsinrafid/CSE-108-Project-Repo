package com.example.examsystemproject;

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

    public void signUp(ActionEvent e) throws IOException {
        String name = newNameField.getText();
        String username = newUserField.getText();
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

        if (UserFileManager.userNameExists(username)) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Signup Error");
            alert.setHeaderText(null);
            alert.setContentText("Username already exists!");
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

        User user = new User(name, username, password, role);
        UserFileManager.saveUser(user);
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("login.fxml"));
        Parent root = fxmlLoader.load();
        Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
        System.out.println("Signup successful");
        System.out.println("Back to login page");
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
