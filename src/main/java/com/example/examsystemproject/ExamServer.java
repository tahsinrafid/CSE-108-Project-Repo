package com.example.examsystemproject;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ExamServer {

    private static final int PORT = 5000;
    private static final ExecutorService POOL = Executors.newFixedThreadPool(10);

    public static void main(String[] args) throws Exception {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Exam server running on port " + PORT);

            while (true) {
                Socket socket = serverSocket.accept();
                POOL.submit(() -> handleClient(socket));
            }
        }
    }

    private static void handleClient(Socket socket) {
        try (
                socket;
                ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                ObjectInputStream in = new ObjectInputStream(socket.getInputStream())
        ) {
            Request request = (Request) in.readObject();
            Response response = processRequest(request);

            out.writeObject(response);
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static Response processRequest(Request request) {
        Response response = new Response();

        try {
            if (request == null || request.getType() == null) {
                response.setOk(false);
                response.setMessage("Invalid request.");
                return response;
            }

            switch (request.getType()) {
                case LOGIN -> {
                    String username = request.getData().get("username");
                    String password = request.getData().get("password");

                    User user = UserFileManager.validateLogin(username, password);
                    if (user == null) {
                        response.setOk(false);
                        response.setMessage("Invalid username or password.");
                    } else {
                        response.setOk(true);
                        response.setMessage("Login successful.");
                        response.setUser(user);
                    }
                }

                case SIGNUP -> {
                    String name = request.getData().get("name");
                    String username = request.getData().get("username");
                    String password = request.getData().get("password");
                    String role = request.getData().get("role");

                    if (UserFileManager.userNameExists(username)) {
                        response.setOk(false);
                        response.setMessage("Username already exists.");
                    } else {
                        User user = new User(name, username, password, role);
                        UserFileManager.saveUser(user);

                        response.setOk(true);
                        response.setMessage("Signup successful.");
                    }
                }
            }
        } catch (Exception e) {
            response.setOk(false);
            response.setMessage("Server error: " + e.getMessage());
        }

        return response;
    }
}

