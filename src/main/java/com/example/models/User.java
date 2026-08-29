package com.example.models;

public class User {
    private int id;
    private String username;
    private String passwordHash;
    private int score;

    // Энэ constructor-г нэмэх (ID, Username, PasswordHash, Score)
    public User(int id, String username, String passwordHash, int score) {
        this.id = id;
        this.username = username;
        this.passwordHash = passwordHash;
        this.score = score;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }
}