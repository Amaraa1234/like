package com.example.utils;

import com.example.models.User;

public class UserSession {

    private static UserSession instance;
    private User currentUser;

    // Гаднаас шууд 'new UserSession()' гэж дуудахаас сэргийлж private болгоно
    private UserSession(User user) {
        this.currentUser = user;
    }

    /**
     * Шинэ сесс үүсгэх (Хэрэглэгч нэвтрэх үед дуудагдна)
     */
    public static void createSession(User user) {
        instance = new UserSession(user);
    }

    /**
     * Одоогийн идэвхтэй сессийг авах
     */
    public static UserSession getInstance() {
        return instance;
    }

    /**
     * Нэвтэрсэн байгаа хэрэглэгчийн объектыг авах
     */
    public User getCurrentUser() {
        return currentUser;
    }

    /**
     * Хэрэглэгчийн мэдээллийг шинэчлэх (Жишээ нь: Тоглоомын оноо өөрчлөгдөхөд)
     */
    public void setCurrentUser(User user) {
        this.currentUser = user;
    }

    /**
     * Хэрэглэгч нэвтэрсэн эсэхийг шалгах
     */
    public static boolean isLoggedIn() {
        return instance != null && instance.getCurrentUser() != null;
    }

    /**
     * Сессийг цэвэрлэх (Logout хийхэд дуудагдна)
     */
    public static void cleanUserSession() {
        instance = null;
    }
}