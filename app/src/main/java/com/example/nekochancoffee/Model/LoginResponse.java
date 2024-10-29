package com.example.nekochancoffee.Model;

public class LoginResponse {
    private boolean success;
    private String message;
    private String role;

    public String getRole() {
        return role;
    }


    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }
}

