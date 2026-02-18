package com.example.examsystemproject;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class signupController {
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

    public void signUp(ActionEvent e) throws IOException{


    }
    public void handleLogin(ActionEvent e) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("login.fxml"));
        Parent root = fxmlLoader.load();
        Stage stage = (Stage)((Node)e.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
        System.out.println("Navigate to login page");
    }
}
