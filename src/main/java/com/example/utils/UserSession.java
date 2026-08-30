package com.example.utils;

import com.example.models.User;

public class UserSession {

    private static UserSession instance;
    private User currentUser;

    // Private constructor (Анхны instance үүсэхээс сэргийлнэ)
    private UserSession() {
    }

    // Singleton instance авна
    public static UserSession getInstance() {
        if (instance == null) {
            instance = new UserSession(); // Хэрэв null байвал шинээр үүсгэнэ
        }
        return instance; // ЗААВАЛ instance-ийг буцаана (null буцааж болохгүй)
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;
    }

    public void cleanUserSession() {
        this.currentUser = null;
    }
}