package com.example.examsystemproject;

import java.io.Serializable;

public class User implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String name;
    private String username;
    private String password;
    private String role;

    public User(String name, String username, String password, String role){
        this.name = name;
        this.username = username;
        this.password = password;
        this.role = role;
    }
    public void setName(String name){
        this.name = name;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public void setRole(String role) {
        this.role = role;
    }
    public String getName() {
        return name;
    }
    public String getPassword() {
        return password;
    }
    public String getUsername() {
        return username;
    }
    public String getRole() {
        return role;
    }
}
