package com.example.nekochancoffee.Model;

import java.io.Serializable;

public class User implements Serializable {
    private int user_id;
    private String username;
    private String password;
    private String role;

    // Constructor
    public User(int id, String username, String password, String role) {
        this.user_id = id;
        this.username = username;
        this.password = password;
        this.role = role;
    }

    public User( String username, String password, String role) {
        this.username = username;
        this.password = password;
        this.role = role;
    }
    public User(){}

    public int getId() {
        return user_id;
    }

    public void setId(int id) {
        this.user_id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}