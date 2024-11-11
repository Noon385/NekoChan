package com.example.nekochancoffee.Model;

public class LoginResponse {
    private boolean success;
    private String message;
    private String role;

    private int id;


    public String getRole() {
        return role;
    }
    public int getId() {
        return id;
    }
    public void setId(int user_id){
        this.id=id;

    }



    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }
}

