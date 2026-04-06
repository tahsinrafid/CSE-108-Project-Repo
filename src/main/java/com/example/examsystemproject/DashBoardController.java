package com.example.examsystemproject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
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
    private BarChart<String, Number> performanceChart;

    @FXML
    private CategoryAxis performanceXAxis;

    @FXML
    private NumberAxis performanceYAxis;

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
        AboutUsController controller = loader.getController();
        controller.setUser(user);
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
        loadPerformanceOverview();
    }

    private void loadPerformanceOverview() {
        if (performanceChart == null || performanceXAxis == null || performanceYAxis == null) {
            return;
        }

        performanceChart.getData().clear();
        performanceXAxis.setLabel("");
        performanceXAxis.setCategories(FXCollections.observableArrayList());
        performanceYAxis.setLabel("Percentage (%)");
        performanceYAxis.setAutoRanging(false);
        performanceYAxis.setLowerBound(0);
        performanceYAxis.setTickUnit(10);
        performanceYAxis.setUpperBound(100);
        performanceChart.setCategoryGap(3);
        performanceChart.setBarGap(0.5);

        if (user == null || user.getUsername() == null || user.getUsername().trim().isEmpty()) {
            return;
        }

        try {
            List<Integer> recentScores = loadRecentScoresOutOf20(user.getUsername().trim());
            System.out.println("DEBUG: Loaded scores for " + user.getUsername() + ": " + recentScores);

            if (recentScores.isEmpty()) {
                System.out.println("DEBUG: No scores found for user");
                return;
            }

            int attemptCount = recentScores.size();

            String[] colors = {
                "#FF6B6B", // Red
                "#4ECDC4", // Teal
                "#45B7D1", // Blue
                "#FFA07A", // Light Salmon
                "#98D8C8", // Light Teal
                "#FFD93D", // Yellow
                "#FF9FF3"  // Pink
            };

            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.setName("Performance");
            
            List<String> categories = new ArrayList<>();

            for (int i = 0; i < attemptCount; i++) {
                int scoreOutOf20 = Math.max(0, recentScores.get(i));
                int percentage = (int) Math.round((scoreOutOf20 * 100.0) / 20);
                String xLabel = String.valueOf(i + 1);
                categories.add(xLabel);
                series.getData().add(new XYChart.Data<>(xLabel, percentage));
            }

            performanceXAxis.setCategories(FXCollections.observableArrayList(categories));
            performanceChart.getData().add(series);

            javafx.application.Platform.runLater(() -> {
                for (int i = 0; i < series.getData().size(); i++) {
                    XYChart.Data<String, Number> data = series.getData().get(i);
                    if (data.getNode() != null) {
                        String colorStyle = "-fx-bar-fill: " + colors[i % colors.length] + ";";
                        data.getNode().setStyle(colorStyle);
                    }
                }
            });
            
        } catch (IOException ex) {
            System.out.println("DEBUG: IOException in loadPerformanceOverview: " + ex.getMessage());
            ex.printStackTrace();
            performanceYAxis.setUpperBound(20);
        }
    }

    private List<Integer> loadRecentScoresOutOf20(String username) throws IOException {
        List<Integer> matched = new ArrayList<>();
        if (username == null || username.trim().isEmpty()) {
            return matched;
        }

        String targetUsername = username.trim();

        File file = new File("exam_results");
        if (!file.exists()) {
            file = new File("../exam_results");
        }
        if (!file.exists()) {
            file = new File("../../exam_results");
        }
        if (!file.exists()) {
            file = new File(System.getProperty("user.dir") + File.separator + "exam_results");
        }
        
        System.out.println("DEBUG: Looking for file at: " + file.getAbsolutePath() + ", exists: " + file.exists());
        
        if (!file.exists()) {
            System.out.println("DEBUG: exam_results file not found at any location");
            return matched;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            int lineNumber = 0;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                if (line == null || line.trim().isEmpty()) {
                    continue;
                }

                String[] parts = line.trim().split(",", 6);
                System.out.println("DEBUG: Line " + lineNumber + ": " + line + " -> Parts: " + parts.length);
                
                if (parts.length < 5) {
                    System.out.println("DEBUG: Skipping line - not enough parts");
                    continue;
                }

                String rowUsername = parts[2] == null ? "" : parts[2].trim();
                System.out.println("DEBUG: Checking username: '" + rowUsername + "' against '" + targetUsername + "'");
                
                if (!targetUsername.equalsIgnoreCase(rowUsername)) {
                    continue;
                }

                int score;
                int total;
                try {
                    score = Integer.parseInt(parts[3].trim());
                    total = Integer.parseInt(parts[4].trim());
                    System.out.println("DEBUG: Found matching user - Score: " + score + ", Total: " + total);
                } catch (NumberFormatException ex) {
                    System.out.println("DEBUG: NumberFormatException for score/total");
                    continue;
                }

                if (total <= 0) {
                    continue;
                }

                int normalizedOutOf20 = (int) Math.round((score * 20.0) / total);
                System.out.println("DEBUG: Normalized score: " + normalizedOutOf20);
                matched.add(Math.max(0, normalizedOutOf20));
            }
        }

        System.out.println("DEBUG: Total matched scores: " + matched.size());
        int fromIndex = Math.max(0, matched.size() - 5);
        List<Integer> recent = new ArrayList<>(matched.subList(fromIndex, matched.size()));
        System.out.println("DEBUG: Returning recent scores: " + recent);
        return recent;
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