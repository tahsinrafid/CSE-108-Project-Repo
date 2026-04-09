package com.example.examsystemproject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class UserFileManager {
    private static final String USERS_FILE = System.getProperty("user.home") + File.separator + "ExamSystem" + File.separator + "users.txt";
    
    static {
        File dir = new File(System.getProperty("user.home") + File.separator + "ExamSystem");
        if (!dir.exists()) {
            dir.mkdirs();
            System.out.println("Created ExamSystem directory at: " + dir.getAbsolutePath());
        }

        try {
            File userFile = new File(USERS_FILE);
            File projectUsers = new File("users.txt");
            
            // Try multiple locations for the source file
            if (!projectUsers.exists()) {
                projectUsers = new File(System.getProperty("user.dir") + File.separator + "users.txt");
            }

            System.out.println("Looking for users.txt at: " + projectUsers.getAbsolutePath());
            
            if (projectUsers.exists() && !userFile.exists()) {
                System.out.println("Copying users.txt from " + projectUsers.getAbsolutePath() + " to " + userFile.getAbsolutePath());
                Files.copy(projectUsers.toPath(), userFile.toPath());
                System.out.println("Successfully copied users.txt");
            } else if (projectUsers.exists()) {
                System.out.println("User file already exists at: " + userFile.getAbsolutePath());
            } else {
                System.out.println("Source users.txt not found at: " + projectUsers.getAbsolutePath());
            }
        } catch (Exception e) {
            System.out.println("Could not copy initial users.txt: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void saveUser(User user) throws IOException {
        try {
            File file = new File(USERS_FILE);
            boolean FileExists = file.exists() && file.length() > 0;
            try (FileWriter writer = new FileWriter(file, true)) {
                if (FileExists) {
                    writer.write("\n");
                }
                writer.write(user.getName() + "," + user.getUsername() + "," + user.getPassword() + "," + user.getRole());
            }
        } catch (IOException e) {
            System.out.println("An error occurred while saving user: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static boolean userNameExists(String username) throws IOException {
        try {
            File file = new File(USERS_FILE);
            if (!file.exists()) {
                System.out.println("Users file does not exist at: " + USERS_FILE);
                return false;
            }
            
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println("Checking line: " + line);
                    String[] parts = line.trim().split(",");
                    if (parts.length >= 2 && parts[1].trim().equals(username.trim())) {
                        return true;
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading from file: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    public static User validateLogin(String username, String password) throws IOException {
        try {
            File file = new File(USERS_FILE);
            if (!file.exists()) {
                System.out.println("Users file does not exist at: " + USERS_FILE);
                return null;
            }
            
            System.out.println("Validating login for username: " + username + " at file: " + USERS_FILE);
            
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println("Read line: " + line);
                    String[] parts = line.trim().split(",");
                    
                    if (parts.length >= 4) {
                        String fileUsername = parts[1].trim();
                        String filePassword = parts[2].trim();
                        
                        System.out.println("Comparing - Input: [" + username + "/" + password + "] vs File: [" + fileUsername + "/" + filePassword + "]");
                        
                        if (fileUsername.equals(username.trim()) && filePassword.equals(password.trim())) {
                            System.out.println("Login successful for user: " + username);
                            return new User(parts[0].trim(), parts[1].trim(), parts[2].trim(), parts[3].trim());
                        }
                    }
                }
            }
            System.out.println("No matching user found for username: " + username);
        } catch (IOException e) {
            System.out.println("Error reading from file: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
}
