package com.example.examsystemproject;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;

public class StartExamController {

    @FXML
    private Label dueExamCountLabel;

    @FXML
    public void initialize() {
        // TODO: load real due-exam count from backend
        System.out.println("StartExam page loaded.");
    }

    /** Called when user clicks "Sit for Exam" on any due exam card */
    @FXML
    public void onSitForExam(ActionEvent e) {
        System.out.println("Sit for exam clicked");
        // TODO: navigate to exam-taking screen with the selected exam's data
    }

    /** Called when user clicks "Practice Now" on any practice card */
    @FXML
    public void onPracticeExam(ActionEvent e) {
        System.out.println("Practice exam clicked");
        // TODO: navigate to practice exam screen with the selected subject
    }

    /** Navigate back to the Student Dashboard */
    @FXML
    public void onBackToDashboard(ActionEvent e) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("DashBoard.fxml"));
        Parent root = loader.load();
        Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }
}

