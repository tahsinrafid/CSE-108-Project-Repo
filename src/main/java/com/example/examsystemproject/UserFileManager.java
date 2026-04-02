package com.example.examsystemproject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class UserFileManager {
    public static void saveUser(User user) throws IOException {
        try {
            File file = new File("users.txt");
            boolean FileExists = file.exists() && file.length() > 0;
            FileWriter writer = new FileWriter(file, true);

            if (FileExists) {
                writer.write("\n");
            }
            writer.write(user.getName() + "," + user.getUsername() + "," + user.getPassword() + "," + user.getRole());
            writer.close();
        } catch (IOException e) {
            System.out.println("An error occurred");
        }
    }

    public static boolean userNameExists(String username) throws IOException {
        try {
            File file = new File("users.txt");
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
                String[] parts = line.split(",");
                if (parts[1].equals(username)) {
                    return true;
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading from file");
        }
        return false;
    }

    public static User validateLogin(String username, String password) throws IOException {
        try {
            File file = new File("users.txt");
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
                String[] parts = line.split(",");
                if (parts[1].equals(username) && parts[2].equals(password)) {
                    return new User(parts[0],parts[1],parts[2],parts[3]);
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading from file");
        }
        return null;
    }
 }
