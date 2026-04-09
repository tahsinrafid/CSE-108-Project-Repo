package com.example.examsystemproject;

import javafx.application.Application;

public class Launcher {
    public static void main(String[] args) {
        // Start the ExamServer in a separate background thread
        Thread serverThread = new Thread(() -> {
            try {
                ExamServer.main(args);
            } catch (Exception e) {
                System.err.println("Failed to start ExamServer: " + e.getMessage());
                e.printStackTrace();
            }
        });
        
        // Set as daemon thread so it stops when the application closes
        serverThread.setDaemon(false);
        serverThread.setName("ExamServerThread");
        serverThread.start();
        
        // Give the server a moment to initialize and start listening
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        // Now launch the GUI application
        Application.launch(HelloApplication.class, args);
    }
}
