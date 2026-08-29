package com.example.database;

import com.example.models.Option;
import com.example.models.Question;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class QuestionDAO {

    private static final Logger logger = LoggerFactory.getLogger(QuestionDAO.class);

    public List<Question> getAllQuestions() {
        List<Question> questions = new ArrayList<>();
        String sql = "SELECT id, question_text, option_a, option_b, option_c, option_d, correct_option FROM questions";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Question question = mapRowToQuestion(rs);
                if (question != null) {
                    questions.add(question);
                }
            }
        } catch (SQLException e) {
            logger.error("Асуултуудыг татахад мэдээллийн сангийн алдаа гарлаа", e);
        }
        return questions;
    }

    public Question getQuestionById(int id) {
        String sql = "SELECT id, question_text, option_a, option_b, option_c, option_d, correct_option FROM questions WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapRowToQuestion(rs);
                }
            }
        } catch (SQLException e) {
            logger.error("ID=" + id + " бүхий асуултыг татахад алдаа гарлаа", e);
        }
        return null;
    }

    /**
     * ResultSet-ийн мөрийг Question объект руу аюулгүй хөрвүүлэх туслах метод
     */
    private Question mapRowToQuestion(ResultSet rs) throws SQLException {
        String correctOptionStr = rs.getString("correct_option");
        Option correctOption = parseOption(correctOptionStr);

        if (correctOption == null) {
            logger.warn("Асуулт ID={}: Хүчингүй зөв хариултын утга ('{}')", rs.getInt("id"), correctOptionStr);
            return null;
        }

        return new Question(
                rs.getInt("id"),
                rs.getString("question_text"),
                rs.getString("option_a"),
                rs.getString("option_b"),
                rs.getString("option_c"),
                rs.getString("option_d"),
                correctOption);
    }

    /**
     * String-ийг Option Enum руу хөрвүүлэх
     */
    private Option parseOption(String str) {
        if (str == null || str.trim().isEmpty()) {
            return null;
        }
        try {
            return Option.valueOf(str.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}