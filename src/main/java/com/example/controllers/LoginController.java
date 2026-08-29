package com.example.controllers;

import com.example.database.UserDAO;
import com.example.models.User;
import com.example.utils.SceneManager;
import com.example.utils.UserSession;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class LoginController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label messageLabel;

    private final UserDAO userDAO = new UserDAO();

    @FXML
    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();

        if (username.isEmpty() || password.isEmpty()) {
            showError("⚠️ Хэрэглэгчийн нэр болон нууц үгээ оруулна уу!");
            return;
        }

        // 1. Өгөгдлийн сангаас хэрэглэгчийг шалгаж нэвтрүүлнэ (BCrypt шалгалт DAO дотор
        // хийгдэнэ)
        User user = userDAO.login(username, password);

        if (user != null) {
            // 2. Нэвтэрсэн хэрэглэгчийг глобал Сессэд хадгална
            UserSession.getInstance().setCurrentUser(user);

            // 3. SceneManager-ээр Тоглоомын дэлгэц рүү шилжинэ
            SceneManager.switchScene("/game.fxml", "🎡 Асуултын Хүрд - " + user.getUsername());
        } else {
            showError("❌ Хэрэглэгчийн нэр эсвэл нууц үг буруу байна!");
        }
    }

    @FXML
    private void handleRegister() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();

        if (username.isEmpty() || password.isEmpty()) {
            showError("⚠️ Хэрэглэгчийн нэр болон нууц үгээ оруулна уу!");
            return;
        }

        if (username.length() < 3) {
            showError("⚠️ Хэрэглэгчийн нэр 3-аас дээш тэмдэгттэй байх ёстой!");
            return;
        }

        if (password.length() < 4) {
            showError("⚠️ Нууц үг 4-өөс дээш тэмдэгттэй байх ёстой!");
            return;
        }

        // Өгөгдлийн санд шинэ хэрэглэгчийг хадгалах
        boolean isRegistered = userDAO.register(username, password);

        if (isRegistered) {
            showSuccess("✅ Бүртгэл амжилттай! Нэвтрэх товч дарна уу.");
            passwordField.clear();
        } else {
            showError("⚠️ Энэ хэрэглэгчийн нэр аль хэдийн бүртгэгдсэн эсвэл баазын алдаа гарлаа!");
        }
    }

    private void showError(String text) {
        messageLabel.getStyleClass().removeAll("success");
        if (!messageLabel.getStyleClass().contains("error")) {
            messageLabel.getStyleClass().add("error");
        }
        messageLabel.setText(text);
    }

    private void showSuccess(String text) {
        messageLabel.getStyleClass().removeAll("error");
        if (!messageLabel.getStyleClass().contains("success")) {
            messageLabel.getStyleClass().add("success");
        }
        messageLabel.setText(text);
    }
}