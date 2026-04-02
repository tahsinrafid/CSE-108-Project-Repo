package com.example.examsystemproject;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class loginController {
    private String role;
    @FXML
    private TextField userNameField;
    @FXML
    private PasswordField passField;
    @FXML
    private Button loginButton;
    @FXML
    private Hyperlink signupLink;

    @FXML
    public void login(ActionEvent e) throws IOException {
        String username = userNameField.getText();
        String password = passField.getText();
        User user = UserFileManager.validateLogin(username, password);
        if (user == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Login Error");
            alert.setHeaderText(null);
            alert.setContentText("Invalid username or password");
            alert.showAndWait();
            return;
        }

        FXMLLoader fxmlLoader;
        Parent root;

        if (user.getRole().equals("student")) {
            fxmlLoader = new FXMLLoader(getClass().getResource("DashBoard.fxml"));
            root = fxmlLoader.load();

            DashBoardController controller = fxmlLoader.getController();
            controller.setUser(user);
        } else {
            fxmlLoader = new FXMLLoader(getClass().getResource("Teacher_DashBoard.fxml"));
            root = fxmlLoader.load();

            TeacherDashBoardController controller = fxmlLoader.getController();
            controller.setUser(user);
        }
        Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    public void handleSignup(ActionEvent e) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("signup.fxml"));
        Parent root = fxmlLoader.load();

        signupController controller = fxmlLoader.getController();
        controller.setRole(role);

        Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
        System.out.println("Navigate to signup page");
    }

    public void setRole(String role) {
        this.role = role;
    }
}



