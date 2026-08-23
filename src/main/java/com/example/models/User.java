package com.example.models;

public class User {
    private int id;
    private String username;
    private int totalScore;
    private int lives;
    private int coins;

    public User(int id, String username, int totalScore, int lives, int coins) {
        this.id = id;
        this.username = username;
        this.totalScore = totalScore;
        this.lives = lives;
        this.coins = coins;
    }

    public User(String username, int totalScore, int lives, int coins) {
        this.username = username;
        this.totalScore = totalScore;
        this.lives = lives;
        this.coins = coins;
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

    public int getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(int totalScore) {
        this.totalScore = totalScore;
    }

    public int getLives() {
        return lives;
    }

    public void setLives(int lives) {
        this.lives = lives;
    }

    public int getCoins() {
        return coins;
    }

    public void setCoins(int coins) {
        this.coins = coins;
    }

    @Override
    public String toString() {
        return "User{" +
                "username='" + username + '\'' +
                ", totalScore=" + totalScore +
                ", lives=" + lives +
                ", coins=" + coins +
                '}';
    }

}
