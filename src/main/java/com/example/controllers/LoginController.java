package com.example.controllers;

import com.example.database.DatabaseConnection;
import com.example.models.User;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

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
    public void handleLogin() {
        String username = usernameField.getText();
        String pass = passwordField.getText();
        if (username.isEmpty() || pass.isEmpty()) {
            messageLabel.setText("Бүх талбарыг бөглөнө үү.");
            return;
        }
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement("SELECT * FROM users WHERE username = ?")) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String hashed = rs.getString("password_hash");
                if (BCrypt.checkpw(pass, hashed)) {
                    User loggedInUser = new User(rs.getInt("id"), rs.getString("username"), hashed, rs.getInt("score"));
                    loadGameScene(loggedInUser);
                } else {
                    messageLabel.setText("Нууц үг буруу!");
                }
            } else {
                messageLabel.setText("Хэрэглэгч олдсонгүй!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            messageLabel.setText("Алдаа гарлаа: " + e.getMessage());
        }
    }

    @FXML
    public void handleRegister() {
        String username = usernameField.getText();
        String pass = passwordField.getText();
        if (username.isEmpty() || pass.isEmpty()) {
            messageLabel.setText("Бүх талбарыг бөглөнө үү.");
            return;
        }
        try (Connection conn = DatabaseConnection.getConnection()) {
            try (PreparedStatement checkStmt = conn.prepareStatement("SELECT id FROM users WHERE username = ?")) {
                checkStmt.setString(1, username);
                if (checkStmt.executeQuery().next()) {
                    messageLabel.setText("Энэ нэр аль хэдийн бүртгэгдсэн!");
                    return;
                }
            }
            String hashed = BCrypt.hashpw(pass, BCrypt.gensalt());
            try (PreparedStatement insertStmt = conn
                    .prepareStatement("INSERT INTO users (username, password_hash, score) VALUES (?, ?, 0)")) {
                insertStmt.setString(1, username);
                insertStmt.setString(2, hashed);
                insertStmt.executeUpdate();
                messageLabel.setText("✅ Амжилттай бүртгэгдлээ. Нэвтрэнэ үү!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            messageLabel.setText("Алдаа гарлаа: " + e.getMessage());
        }
    }

    private void loadGameScene(User user) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/game.fxml"));
            Parent root = loader.load();
            GameController controller = loader.getController();
            controller.setUser(user);
            controller.setStage(stage);
            // ❌ ЭНД initGame() ДУУДАХГҮЙ! setUser() дотор автоматаар дуудагдана
            stage.setScene(new Scene(root, 900, 700));
            stage.setTitle("🎡 Асуултын Хүрд");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}