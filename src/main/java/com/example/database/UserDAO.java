package com.example.database;

import com.example.models.User;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class UserDAO {

    // Login хийх - User буцаана, эсвэл null
    public User login(String username, String password) {
        String sql = "SELECT * FROM users WHERE username = ?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String hashed = rs.getString("password_hash");
                if (BCrypt.checkpw(password, hashed)) {
                    return new User(rs.getInt("id"), rs.getString("username"), hashed, rs.getInt("score"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null; // Хэрэглэгч олдохгүй эсвэл нууц үг буруу
    }

    // Register хийх - boolean буцаана
    public boolean register(String username, String password) {
        String checkSql = "SELECT id FROM users WHERE username = ?";
        String insertSql = "INSERT INTO users (username, password_hash, score) VALUES (?, ?, 0)";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
            checkStmt.setString(1, username);
            if (checkStmt.executeQuery().next()) {
                return false; // Хэрэглэгч аль хэдийн байна
            }
            String hashed = BCrypt.hashpw(password, BCrypt.gensalt());
            try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                insertStmt.setString(1, username);
                insertStmt.setString(2, hashed);
                insertStmt.executeUpdate();
                return true; // Амжилттай
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Оноо шинэчлэх
    public void updateScore(int userId, int newScore) {
        String sql = "UPDATE users SET score = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, newScore);
            stmt.setInt(2, userId);
            stmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}