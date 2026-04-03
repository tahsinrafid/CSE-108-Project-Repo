package com.example.examsystemproject;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class DashBoardController {
    private User user;

    @FXML
    private Label helloLabel;

    @FXML
    public void initialize() {
        System.out.println("Dashboard loaded successfully!");
    }

    public void displayName(String username) {
        System.out.println("Welcome, " + username + "!");
    }

    @FXML
    public void onQuestionBankClick(ActionEvent e) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("qbankView.fxml"));
        Parent root = loader.load();
        QbankViewController controller = loader.getController();
        controller.setUser(user);

        Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }

//    public void onSpeedTestClick(ActionEvent e) throws IOException {
//        FXMLLoader loader = new FXMLLoader(getClass().getResource("ViewResults.fxml"));
//        Parent root = loader.load();
//        ViewResultsController controller = loader.getController();
//        controller.setUser(user);
//
//        Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
//        stage.setScene(new Scene(root));
//        stage.show();
//    }

    @FXML
    public void onViewResultsClick(ActionEvent e) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("ViewResults.fxml"));
        Parent root = loader.load();
        ViewResultsController controller = loader.getController();
        controller.setUser(user);

        Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }

    @FXML
    public void onLeaderboardClick(ActionEvent e) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Leaderboard.fxml"));
        Parent root = loader.load();
        LeaderboardController controller = loader.getController();
        controller.setUser(user);

        Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }

    @FXML
    public void onCommunityClick(ActionEvent e) {
        System.out.println("Community clicked");
    }

    @FXML
    public void onQnaClick(ActionEvent e) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("QnaPage.fxml"));
        Parent root = loader.load();
        QnaController controller = loader.getController();
        controller.setUser(user);

        Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }

    @FXML
    public void onAboutUsClick(ActionEvent e) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("aboutUs.fxml"));
        Parent root = loader.load();

        Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }

    @FXML
    public void onDashboardClick(ActionEvent e) {
        System.out.println("Dashboard clicked");
    }

    @FXML
    public void logout(ActionEvent e) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("LandingPage.fxml"));
        Parent root = fxmlLoader.load();

        Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public void setUser(User user) {
        this.user = user;
        if (helloLabel != null && user != null) {
            helloLabel.setText("Hello " + user.getUsername() + "!");
        }
    }

    public void onStartExamClick(ActionEvent e) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("StudentStartExam.fxml"));
        Parent root = fxmlLoader.load();

        StartExamController controller = fxmlLoader.getController();
        controller.setUser(user);

        Stage stage = (Stage)((Node)e.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
}