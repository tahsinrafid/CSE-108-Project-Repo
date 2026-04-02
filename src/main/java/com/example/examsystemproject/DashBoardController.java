package com.example.examsystemproject;

import java.io.IOException;

import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

public class DashBoardController {
    private User user;
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
    @FXML
    private AnchorPane cardStartExam;
    @FXML
    private AnchorPane cardQuestionBank;
    @FXML
    private AnchorPane cardSpeedTest;
    @FXML
    private AnchorPane cardLeaderboard;
    @FXML
    private AnchorPane cardCommunity;
    @FXML
    private AnchorPane cardQna;
    @FXML
    private AnchorPane performanceCard;

    private boolean sidebarExpanded = true;

    @FXML
    public void initialize() {
        animateEntrance(cardStartExam, 0);
        animateEntrance(cardQuestionBank, 80);
        animateEntrance(cardSpeedTest, 140);
        animateEntrance(cardLeaderboard, 220);
        animateEntrance(cardCommunity, 280);
        animateEntrance(cardQna, 340);
        animateEntrance(performanceCard, 420);
        System.out.println("Dashboard loaded successfully!");
    }

    private void animateEntrance(AnchorPane node, int delayMs) {
        if (node == null) {
            return;
        }

        node.setOpacity(0);
        node.setTranslateY(18);

        FadeTransition fade = new FadeTransition(Duration.millis(450), node);
        fade.setFromValue(0);
        fade.setToValue(1);
        fade.setDelay(Duration.millis(delayMs));

        TranslateTransition slide = new TranslateTransition(Duration.millis(450), node);
        slide.setFromY(18);
        slide.setToY(0);
        slide.setDelay(Duration.millis(delayMs));

        fade.play();
        slide.play();
    }

    public void displayName(String username) {
        System.out.println("Welcome, " + username + "!");
    }

//    @FXML
//    public void onDashboardClick(ActionEvent e) {
//        System.out.println("Dashboard clicked");
//    }

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
    public void onAboutUsClick(ActionEvent e) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("aboutUs.fxml"));
        Parent root = loader.load();

        Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }

    @FXML
    public void onDashboardClick(ActionEvent e) {
        if (sidebarExpanded) {
            // Collapse sidebar
            sidebarVBox.setPrefWidth(50.0);

            // Hide button text
            dashboardBtn.setText("");
            questionBankBtn.setText("");
            speedTestBtn.setText("");
            leaderboardBtn.setText("");
            communityBtn.setText("");
            qnaBtn.setText("");
            aboutUsBtn.setText("");

            sidebarExpanded = false;
            System.out.println("Sidebar minimized");
        } else {
            // Expand sidebar
            sidebarVBox.setPrefWidth(220.0);

            // Show button text
            dashboardBtn.setText("Dashboard");
            questionBankBtn.setText("Question Bank");
            speedTestBtn.setText("Speed test");
            leaderboardBtn.setText("Leaderboard");
            communityBtn.setText("Community");
            qnaBtn.setText("QNA");
            aboutUsBtn.setText("About us");

            sidebarExpanded = true;
            System.out.println("Sidebar expanded");
        }
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
    }

    public void onStartExamClick(ActionEvent f) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("startExam.fxml"));
        Parent root = fxmlLoader.load();

        Stage stage = (Stage)((Node)f.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
}