package com.example.examsystemproject;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class loginController {

    @FXML
    private Button loginButton;
    private Stage stage;
    private Parent root;
    private Scene scene;
    @FXML
    private TextField nameTextField;
    @FXML
    private PasswordField passfield;
    public void login(ActionEvent event) throws IOException {
        String username = nameTextField.getText();
        String password = passfield.getText();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("DashBoard.fxml"));
        root = fxmlLoader.load();

        DashBoardController dashBoardController = fxmlLoader.getController();
        dashBoardController.displayName(username);

        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
}
