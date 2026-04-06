package com.example.examsystemproject;

import java.io.IOException;

import javafx.concurrent.Task;
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
    public void login(ActionEvent e) {
        String username = userNameField.getText().trim();
        String password = passField.getText();

        Task<Response> task = new Task<>() {
            @Override
            protected Response call() throws Exception {
                NetworkClient client = new NetworkClient("localhost", 5000);

                Request request = new Request();
                request.setType(RequestType.LOGIN);
                request.getData().put("username", username);
                request.getData().put("password", password);

                return client.send(request);
            }
        };

        task.setOnSucceeded(event -> {
            Response response = task.getValue();

            if (response == null || !response.isOk()) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Login Error");
                alert.setHeaderText(null);
                alert.setContentText(response != null ? response.getMessage() : "Login failed.");
                alert.showAndWait();
                return;
            }

            try {
                User user = response.getUser();
                FXMLLoader fxmlLoader;
                Parent root;

                if ("student".equalsIgnoreCase(user.getRole())) {
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
                stage.setScene(new Scene(root));
                stage.show();
            } catch (Exception ex) {
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

    @FXML
    public void goBackToLanding(ActionEvent e) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("LandingPage.fxml"));
        Parent root = fxmlLoader.load();

        Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }

    public void setRole(String role) {
        this.role = role;
    }
}



