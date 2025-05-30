package com.end.finalproject.model;

public class User {
    private String userid;
    private String email;
    private String password;
    private String phoneNumber;
    private String role;
    private String name;  // Có thể null đối với customer & admin

    public User() {
        // Required for Firebase deserialization
    }

    public User(String userid, String email, String password, String phoneNumber, String role, String name) {
        this.userid = userid;
        this.email = email;
        this.password = password;
        this.phoneNumber = phoneNumber;
        this.role = role;
        this.name = name;
    }

    // Getters and Setters

    public String getUserId() {
        return userid;
    }

    public void setUserId(String userid) {
        this.userid = userid;
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

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
