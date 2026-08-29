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
import java.util.HashMap;
import java.util.Map;

public class LoginController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label messageLabel;

    private Stage stage;

    // Санах ойд хадгалагдах хэрэглэгчийн сан (username -> password).
    // ⚠️ Программ дахин асаахад устана. Бодит систем бол файл/DB ашиглана.
    private static final Map<String, String> registeredUsers = new HashMap<>();

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @FXML
    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();

        if (username.isEmpty() || password.isEmpty()) {
            showError("⚠️ Хэрэглэгчийн нэр болон нууц үгээ оруулна уу!");
            return;
        }

        if (!registeredUsers.containsKey(username)) {
            showError("❌ Энэ хэрэглэгч бүртгэлгүй байна. Эхлээд бүртгүүлнэ үү!");
            return;
        }

        if (!registeredUsers.get(username).equals(password)) {
            showError("❌ Нууц үг буруу байна!");
            return;
        }

        loadGameScene(username);
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

        if (registeredUsers.containsKey(username)) {
            showError("⚠️ Энэ хэрэглэгчийн нэр аль хэдийн бүртгэгдсэн байна!");
            return;
        }

        registeredUsers.put(username, password);
        showSuccess("✅ Бүртгэл амжилттай! Нэвтрэх товч дарна уу.");
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

    /**
     * Тоглоомын дэлгэц рүү шилжих
     */
    private void loadGameScene(String username) {
        if (stage == null) {
            showError("❌ Дотоод алдаа: Stage тохируулагдаагүй байна.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/game.fxml"));
            Parent root = loader.load();

            GameController controller = loader.getController();

            controller.setUsername(username);
            controller.setStage(stage);
            controller.initGame();

            Scene scene = new Scene(root, 900, 750);
            stage.setScene(scene);
            stage.setTitle("🎡 Асуултын Хүрд - " + username);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            showError("❌ Тоглоомын дэлгэц ачааллаж чадсангүй!");
        }
    }
}