package com.example.database;

import com.example.models.User;
import org.mindrot.jbcrypt.BCrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDAO {

    private static final Logger logger = LoggerFactory.getLogger(UserDAO.class);

    public User login(String username, String password) {
        String sql = "SELECT id, username, password_hash, score FROM users WHERE username = ?";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String hashed = rs.getString("password_hash");
                    if (BCrypt.checkpw(password, hashed)) {
                        return new User(
                                rs.getInt("id"),
                                rs.getString("username"),
                                hashed,
                                rs.getInt("score"));
                    }
                }
            }
        } catch (SQLException e) {
            logger.error("Нэвтрэх үед баазын алдаа гарлаа: ", e);
        }
        return null;
    }

    public boolean register(String username, String password) {
        String checkSql = "SELECT id FROM users WHERE username = ?";
        String insertSql = "INSERT INTO users (username, password_hash, score) VALUES (?, ?, 0)";

        try (Connection conn = DatabaseConnection.getConnection()) {
            // 1. Давхардаж буй эсэхийг шалгах
            try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
                checkStmt.setString(1, username);
                try (ResultSet rs = checkStmt.executeQuery()) {
                    if (rs.next()) {
                        return false; // Хэрэглэгч хэдийн бүртгэлтэй байна
                    }
                }
            }

            // 2. Нууц үгийг хэшлэх ба хадгалах
            String hashed = BCrypt.hashpw(password, BCrypt.gensalt(12));
            try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                insertStmt.setString(1, username);
                insertStmt.setString(2, hashed);
                return insertStmt.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            logger.error("Бүртгэх үед баазын алдаа гарлаа: ", e);
            return false;
        }
    }

    /**
     * Тоглоомын оноог MySQL баазад хадгалах/шинэчлэх метод (НЭМЭГДСЭН)
     */
    public boolean updateScore(int userId, int newScore) {
        String sql = "UPDATE users SET score = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, newScore);
            stmt.setInt(2, userId);

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            logger.error("Оноо шинэчлэхэд баазын алдаа гарлаа (userId: " + userId + "): ", e);
            return false;
        }
    }
}