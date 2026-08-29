package com.example.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label messageLabel;

    private Stage stage;

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @FXML
    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();

        if (username.isEmpty() || password.isEmpty()) {
            messageLabel.setText("⚠️ Хэрэглэгчийн нэр болон нууц үгээ оруулна уу!");
            return;
        }

        loadGameScene(username);
    }

    @FXML
    private void handleRegister() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();

        if (username.isEmpty() || password.isEmpty()) {
            messageLabel.setText("⚠️ Хэрэглэгчийн нэр болон нууц үгээ оруулна уу!");
            return;
        }

        messageLabel.setText("✅ Бүртгэл амжилттай! Нэвтрэх товч дарна уу.");
    }

    /**
     * Тоглоомын дэлгэц рүү шилжих
     */
    private void loadGameScene(String username) { // ← String параметртэй
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/game.fxml"));
            Parent root = loader.load();

            GameController controller = loader.getController();

            // Хэрэглэгчийн нэрийг дамжуулах (setUser биш setUsername)
            controller.setUsername(username); // ← ЭНД АНХААР!
            controller.setStage(stage);
            controller.initGame();

            Scene scene = new Scene(root, 900, 750);
            stage.setScene(scene);
            stage.setTitle("🎡 Асуултын Хүрд - " + username);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            messageLabel.setText("❌ Тоглоомын дэлгэц ачааллаж чадсангүй!");
        }
    }
}