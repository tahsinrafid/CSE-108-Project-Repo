package com.example.examsystemproject;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class DashBoardController {
    @FXML
    private Label nameLabel;
    public void displayName(String username)
    {
        nameLabel.setText("Hello, " + username + "!");
    }
}
