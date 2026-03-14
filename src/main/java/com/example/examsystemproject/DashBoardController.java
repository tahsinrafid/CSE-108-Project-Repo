package com.example.examsystemproject;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.animation.Timeline;
import javafx.animation.KeyFrame;
import javafx.util.Duration;

import java.io.IOException;

public class DashBoardController {
    public Button logoutBtn;
    @FXML
    private Button dashboardBtn;
    @FXML
    private Button questionBankBtn;
    @FXML
    private Button speedTestBtn;
    @FXML
    private Button leaderboardBtn;
    @FXML
    private Button communityBtn;
    @FXML
    private Button qnaBtn;
    @FXML
    private Button aboutUsBtn;
    @FXML
    private Button minimizeBtn;
    @FXML
    private VBox sidebarVBox;

    private boolean sidebarExpanded = true;

    @FXML
    public void initialize() {
        System.out.println("Dashboard loaded successfully!");
    }

    public void displayName(String username) {
        System.out.println("Welcome, " + username + "!");
    }

//    @FXML
//    public void onDashboardClick(ActionEvent e) {
//        System.out.println("Dashboard clicked");
//    }

    @FXML
    public void onQuestionBankClick(ActionEvent e) {
        System.out.println("Question Bank clicked");
    }

    @FXML
    public void onSpeedTestClick(ActionEvent e) {
        System.out.println("Speed Test clicked");
    }

    @FXML
    public void onLeaderboardClick(ActionEvent e) {
        System.out.println("Leaderboard clicked");
    }

    @FXML
    public void onCommunityClick(ActionEvent e) {
        System.out.println("Community clicked");
    }

    @FXML
    public void onQnaClick(ActionEvent e) {
        System.out.println("QNA clicked");
    }

    @FXML
    public void onAboutUsClick(ActionEvent e) {
        System.out.println("About Us clicked");
    }

    @FXML
    public void onDashboardClick(ActionEvent e) {
        if (sidebarExpanded) {
            // Collapse sidebar
            sidebarVBox.setPrefWidth(50.0);
            sidebarVBox.setStyle("-fx-background-color: #2d5a8c; -fx-padding: 10px 0px; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 5, 0, 0, 1); -fx-alignment: center-left;");

            // Hide button text
            dashboardBtn.setText("");
            questionBankBtn.setText("");
            speedTestBtn.setText("");
            leaderboardBtn.setText("");
            communityBtn.setText("");
            qnaBtn.setText("");
            aboutUsBtn.setText("");

            // Adjust padding
            dashboardBtn.setStyle("-fx-padding: 15px 5px; -fx-text-fill: white; -fx-font-size: 14; -fx-font-weight: bold; -fx-background-color: #1e3a5f; -fx-border-color: #1e3a5f; -fx-cursor: hand;");
            questionBankBtn.setStyle("-fx-padding: 15px 5px; -fx-text-fill: #d0d0d0; -fx-font-size: 13; -fx-background-color: transparent; -fx-border-color: transparent; -fx-cursor: hand;");
            speedTestBtn.setStyle("-fx-padding: 15px 5px; -fx-text-fill: #d0d0d0; -fx-font-size: 13; -fx-background-color: transparent; -fx-border-color: transparent; -fx-cursor: hand;");
            leaderboardBtn.setStyle("-fx-padding: 15px 5px; -fx-text-fill: #d0d0d0; -fx-font-size: 13; -fx-background-color: transparent; -fx-border-color: transparent; -fx-cursor: hand;");
            communityBtn.setStyle("-fx-padding: 15px 5px; -fx-text-fill: #d0d0d0; -fx-font-size: 13; -fx-background-color: transparent; -fx-border-color: transparent; -fx-cursor: hand;");
            qnaBtn.setStyle("-fx-padding: 15px 5px; -fx-text-fill: #d0d0d0; -fx-font-size: 13; -fx-background-color: transparent; -fx-border-color: transparent; -fx-cursor: hand;");
            aboutUsBtn.setStyle("-fx-padding: 15px 5px; -fx-text-fill: #d0d0d0; -fx-font-size: 13; -fx-background-color: transparent; -fx-border-color: transparent; -fx-cursor: hand;");

            sidebarExpanded = false;
            System.out.println("Sidebar minimized");
        } else {
            // Expand sidebar
            sidebarVBox.setPrefWidth(220.0);
            sidebarVBox.setStyle("-fx-background-color: #2d5a8c; -fx-padding: 10px 0px; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 5, 0, 0, 1);");

            // Show button text
            dashboardBtn.setText("Dashboard");
            questionBankBtn.setText("Question Bank");
            speedTestBtn.setText("Speed test");
            leaderboardBtn.setText("Leaderboard");
            communityBtn.setText("Community");
            qnaBtn.setText("QNA");
            aboutUsBtn.setText("About us");

            // Reset padding
            dashboardBtn.setStyle("-fx-padding: 15px 20px; -fx-text-fill: white; -fx-font-size: 14; -fx-font-weight: bold; -fx-background-color: #1e3a5f; -fx-border-color: #1e3a5f; -fx-cursor: hand;");
            questionBankBtn.setStyle("-fx-padding: 15px 20px; -fx-text-fill: #d0d0d0; -fx-font-size: 13; -fx-background-color: transparent; -fx-border-color: transparent; -fx-cursor: hand;");
            speedTestBtn.setStyle("-fx-padding: 15px 20px; -fx-text-fill: #d0d0d0; -fx-font-size: 13; -fx-background-color: transparent; -fx-border-color: transparent; -fx-cursor: hand;");
            leaderboardBtn.setStyle("-fx-padding: 15px 20px; -fx-text-fill: #d0d0d0; -fx-font-size: 13; -fx-background-color: transparent; -fx-border-color: transparent; -fx-cursor: hand;");
            communityBtn.setStyle("-fx-padding: 15px 20px; -fx-text-fill: #d0d0d0; -fx-font-size: 13; -fx-background-color: transparent; -fx-border-color: transparent; -fx-cursor: hand;");
            qnaBtn.setStyle("-fx-padding: 15px 20px; -fx-text-fill: #d0d0d0; -fx-font-size: 13; -fx-background-color: transparent; -fx-border-color: transparent; -fx-cursor: hand;");
            aboutUsBtn.setStyle("-fx-padding: 15px 20px; -fx-text-fill: #d0d0d0; -fx-font-size: 13; -fx-background-color: transparent; -fx-border-color: transparent; -fx-cursor: hand;");

            sidebarExpanded = true;
            System.out.println("Sidebar expanded");
        }
    }

    @FXML
    public void logout(ActionEvent e) throws IOException {
        // Load the login page
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("login.fxml"));
        Parent root = fxmlLoader.load();

        Stage stage = (Stage)((Node)e.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public void onViewStudentClick(ActionEvent actionEvent) {
    }

    public void onTakeExamClick(ActionEvent actionEvent) {
    }

    public void OnMessegesClick(ActionEvent actionEvent) {
    }

    public void onViewSubClick(ActionEvent actionEvent) {
    }

    public void onQuestionBankViewClick(ActionEvent actionEvent) {
    }
}