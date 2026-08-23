package com.example.database;

import com.example.models.User;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDAO {

    // Хэрэглэгчийн мэдээллийг хэрэглэгчийн нэрээр татаж авах
    public User getUserByUsername(String username) {
        String sql = "SELECT * FROM users WHERE username = ?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new User(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getInt("total_score"),
                        rs.getInt("lives"),
                        rs.getInt("coins"));
            }
        } catch (SQLException e) {
            System.err.println("Хэрэглэгчийн мэдээлэл татахад алдаа гарлаа: " + e.getMessage());
        }
        return null;
    }

    // Азын хүрд болон тоглоомоос авсан амь, зоосыг баазад шинэчлэх
    public boolean updateUserStats(String username, int addLives, int addCoins) {
        String sql = "UPDATE users SET lives = lives + ?, coins = coins + ? WHERE username = ?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, addLives);
            stmt.setInt(2, addCoins);
            stmt.setString(3, username);

            int rowsUpdated = stmt.executeUpdate();
            return rowsUpdated > 0;
        } catch (SQLException e) {
            System.err.println("Хэрэглэгчийн мэдээлэл шинэчлэхэд алдаа гарлаа: " + e.getMessage());
            return false;
        }
    }

    // Хэрэглэгчийн нийт оноог шинэчлэх
    public boolean updateScore(String username, int addScore) {
        String sql = "UPDATE users SET total_score = total_score + ? WHERE username = ?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, addScore);
            stmt.setString(2, username);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Оноо шинэчлэхэд алдаа гарлаа: " + e.getMessage());
            return false;
        }
    }
}