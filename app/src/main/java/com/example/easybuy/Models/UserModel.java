package com.example.easybuy.Models;

public class UserModel {
    private String name,email,password,userId;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public UserModel(){

    }

    public UserModel(String email, String password, String userId) {
        this.email = email;
        this.password = password;
        this.name = null;
        this.userId = userId;
    }
}
