package com.example.examsystemproject;

import java.io.*;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class TeacherDashBoardController {
    private User user;
    @FXML
    private VBox sidebarVBox;
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
    private Button LogOutBtn;
    @FXML
    private Label helloLabel;
    @FXML
    private Label studentLabel;
    @FXML
    private Label examLabel;
    @FXML
    public void initialize() {
        System.out.println("Teacher dashboard loaded successfully.");
        showTotalStudents();
        showTotalExams();
    }

    @FXML
    public void onDashboardClick(ActionEvent e) {
        System.out.println("Dashboard clicked");
    }

    @FXML
    public void onQuestionBankClick(ActionEvent e) throws IOException {
        navigateToQuestionBank(e);
    }

    @FXML
    public void onViewExamsClick(ActionEvent e) throws IOException {
        navigateToViewExams(e);
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
    public void onTakeExamClick(ActionEvent e) throws IOException {
        navigateToCreateExam(e);
    }

    @FXML
    public void onCreateExamClick(ActionEvent e) throws IOException {
        navigateToCreateExam(e);
    }

    @FXML
    public void OnMessegesClick(ActionEvent e) throws IOException {
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
        AboutUsController controller = loader.getController();
        controller.setUser(user);
        Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }

    @FXML
    public void onViewSubClick(ActionEvent e) throws IOException {
        navigateToCreateExam(e);
    }

    @FXML
    public void onQuestionBankViewClick(ActionEvent e) throws IOException {
        navigateToQuestionBank(e);
    }

    private void navigateToQuestionBank(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("qbankView.fxml"));
        Parent root = loader.load();
        QbankViewController controller = loader.getController();
        controller.setUser(user);

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }

    private void navigateToCreateExam(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("CreateExam.fxml"));
        Parent root = loader.load();
        CreateExamController controller = loader.getController();
        controller.setUser(user);

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }

    private void navigateToViewExams(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("ViewExams.fxml"));
        Parent root = loader.load();
        ViewExamsController controller = loader.getController();
        controller.setUser(user);

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }

    @FXML
    public void logout(ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("LandingPage.fxml"));
        Parent root = fxmlLoader.load();

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }

    public void setUser(User user) {
        this.user = user;
        if (helloLabel != null && user != null) {
            helloLabel.setText("Hello " + user.getUsername() + "!");
        }
    }

    public void showTotalStudents() {
        int studentCount = 0;

        try (BufferedReader reader = new BufferedReader(new FileReader("users.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 4 && "student".equalsIgnoreCase(parts[3].trim())) {
                    studentCount++;
                }
            }
        } catch (IOException e) {
            System.out.println("Could not read users.txt: " + e.getMessage());
        }
        if(studentLabel != null){
            studentLabel.setText(Integer.toString(studentCount));
        }
    }
    public void showTotalExams() {
        int examCount = 0;

        try (BufferedReader reader = new BufferedReader(new FileReader("exams"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                examCount++;
            }
        } catch (IOException e) {
            System.out.println("Could not read users.txt: " + e.getMessage());
        }
        if(examLabel != null) {
            examLabel.setText(Integer.toString(examCount));
        }
    }
}
