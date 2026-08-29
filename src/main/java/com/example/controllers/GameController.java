package com.example.controllers;

import com.example.database.DatabaseConnection;
import com.example.models.Question;
import com.example.models.User;
import javafx.animation.RotateTransition;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcType;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class GameController {
    @FXML
    private Canvas wheelCanvas;
    @FXML
    private StackPane wheelContainer;
    @FXML
    private Label scoreLabel, rankLabel, kingLabel;
    @FXML
    private TextArea questionArea;
    @FXML
    private Button optionA, optionB, optionC, optionD;

    @FXML
    public void initialize() {
        // initialize-д юу ч хийхгүй
    }

    @FXML
    private void logout() {
        if (stage != null) {
            stage.close(); // Цонхыг хаах
        }
    }

    // Эсвэл тусламжийн цонх нээх
    @FXML
    private void openHelp() {
        try {
            Stage helpStage = new Stage();
            helpStage.setTitle("Тусламж");
            helpStage.setScene(new Scene(new Label("Тоглоомын заавар..."), 300, 200));
            helpStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Stage stage;
    private User currentUser;
    private List<Question> questions = new ArrayList<>();
    private int currentAngle = 0;
    private Question currentQuestion;
    private final int SECTOR_COUNT = 8;
    private final Color[] COLORS = {
            Color.rgb(255, 179, 186), Color.rgb(255, 223, 186), Color.rgb(255, 255, 186),
            Color.rgb(186, 255, 201), Color.rgb(186, 225, 255), Color.rgb(203, 186, 255),
            Color.rgb(255, 186, 255), Color.rgb(204, 204, 204)
    };

    public void setUser(User user) {
        this.currentUser = user;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void initGame() {
        if (currentUser == null) {
            System.out.println("Хэрэглэгч байхгүй байна!");
            return;
        }
        loadQuestions();
        drawWheel(0);
        updateUI();
        disableOptions(true);
        questionArea.setText("🎯 Эргүүлэх товч дээр дарна уу!");
    }

    private void loadQuestions() {
        // ✅ ЗАСВАР: DatabaseConnection.getInstance() -> DatabaseConnection
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement("SELECT * FROM questions LIMIT 8")) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                questions.add(new Question(
                        rs.getInt("id"),
                        rs.getString("question_text"),
                        rs.getString("option_a"),
                        rs.getString("option_b"),
                        rs.getString("option_c"),
                        rs.getString("option_d"),
                        rs.getString("correct_option").charAt(0)));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void drawWheel(int rotateAngle) {
        GraphicsContext gc = wheelCanvas.getGraphicsContext2D();
        double w = wheelCanvas.getWidth(), h = wheelCanvas.getHeight();
        double cx = w / 2, cy = h / 2, radius = Math.min(w, h) / 2 - 10;

        gc.clearRect(0, 0, w, h);
        double angleStep = 360.0 / SECTOR_COUNT;

        for (int i = 0; i < SECTOR_COUNT; i++) {
            double startAngle = rotateAngle + i * angleStep;
            gc.setFill(COLORS[i % COLORS.length]);
            gc.fillArc(cx - radius, cy - radius, radius * 2, radius * 2, startAngle, angleStep, ArcType.ROUND);

            gc.setFill(Color.BLACK);
            gc.setStroke(Color.WHITE);
            gc.setLineWidth(2);
            double mid = Math.toRadians(startAngle + angleStep / 2);
            double tx = cx + (radius * 0.6) * Math.cos(mid);
            double ty = cy + (radius * 0.6) * Math.sin(mid);
            gc.fillText(String.valueOf(i + 1), tx - 5, ty + 5);
        }
        gc.setFill(Color.RED);
        gc.fillPolygon(new double[] { cx - 10, cx + 10, cx }, new double[] { 10, 10, 25 }, 3);
    }

    @FXML
    public void spinWheel() {
        if (questions.isEmpty())
            return;
        disableOptions(true);
        questionArea.setText("🌀 Хүрд эргэж байна...");

        int randomSpin = 720 + (int) (Math.random() * 1080);
        int targetAngle = currentAngle + randomSpin;
        currentAngle = targetAngle % 360;

        RotateTransition rt = new RotateTransition(Duration.seconds(3), wheelContainer);
        rt.setByAngle(randomSpin);
        rt.setInterpolator(javafx.animation.Interpolator.EASE_OUT);
        rt.setOnFinished(e -> {
            int normalized = (360 - (currentAngle % 360)) % 360;
            int sectorIndex = (int) (normalized / (360.0 / SECTOR_COUNT));
            if (sectorIndex >= SECTOR_COUNT)
                sectorIndex = 0;
            currentQuestion = questions.get(sectorIndex);
            displayQuestion();
        });
        rt.play();
    }

    private void displayQuestion() {
        questionArea.setText(currentQuestion.getText());
        // ✅ ЗӨВ: getOptionA(), getOptionB() гэх мэт
        optionA.setText("A. " + currentQuestion.getOptionA());
        optionB.setText("B. " + currentQuestion.getOptionB());
        optionC.setText("C. " + currentQuestion.getOptionC());
        optionD.setText("D. " + currentQuestion.getOptionD());
        disableOptions(false);
    }

    @FXML
    public void handleAnswer(javafx.event.ActionEvent event) {
        Button clicked = (Button) event.getSource();
        String selected = clicked.getText().substring(0, 1).toUpperCase();
        char selectedChar = selected.charAt(0);

        boolean correct = selectedChar == currentQuestion.getCorrectOption();
        int change = correct ? 1 : -1;

        int newScore = currentUser.getScore() + change;
        if (newScore < 0)
            newScore = 0;
        currentUser.setScore(newScore);

        // ✅ ЗАСВАР: DatabaseConnection.getInstance() -> DatabaseConnection
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement("UPDATE users SET score = ? WHERE id = ?")) {
            stmt.setInt(1, newScore);
            stmt.setInt(2, currentUser.getId());
            stmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (correct) {
            questionArea.setText("✅ Зөв хариуллаа! +1 оноо");
            clicked.setStyle("-fx-background-color: #2ECC71; -fx-text-fill: white;");
        } else {
            questionArea.setText("❌ Буруу хариуллаа! -1 оноо. Зөв хариулт: " + currentQuestion.getCorrectOption());
            clicked.setStyle("-fx-background-color: #E74C3C; -fx-text-fill: white;");
        }
        disableOptions(true);
        updateUI();
        updateKing();

        new javafx.animation.Timeline(
                new javafx.animation.KeyFrame(Duration.seconds(1.5), e -> {
                    resetButtonStyles();
                    spinWheel();
                })).play();
    }

    private void updateUI() {
        scoreLabel.setText("⭐ " + currentUser.getScore());
        rankLabel.setText("🏆 Rank: " + getRank());
        updateKing();
    }

    private int getRank() {
        // ✅ ЗАСВАР: DatabaseConnection.getInstance() -> DatabaseConnection
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(
                        "SELECT COUNT(*) + 1 as rank_pos FROM users WHERE score > (SELECT score FROM users WHERE id = ?)")) {
            stmt.setInt(1, currentUser.getId());
            ResultSet rs = stmt.executeQuery();
            if (rs.next())
                return rs.getInt("rank_pos");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    private void updateKing() {
        // ✅ ЗАСВАР: DatabaseConnection.getInstance() -> DatabaseConnection
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(
                        "SELECT username, score FROM users ORDER BY score DESC LIMIT 1")) {
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String kingName = rs.getString("username");
                int kingScore = rs.getInt("score");
                if (kingName.equals(currentUser.getUsername())) {
                    kingLabel.setText("👑 ТА МОНГОЛЫН ХААН! (Оноо: " + kingScore + ")");
                } else {
                    kingLabel.setText("👑 Хаан: " + kingName + " (Оноо: " + kingScore + ")");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void disableOptions(boolean disable) {
        optionA.setDisable(disable);
        optionB.setDisable(disable);
        optionC.setDisable(disable);
        optionD.setDisable(disable);
    }

    private void resetButtonStyles() {
        optionA.setStyle("");
        optionB.setStyle("");
        optionC.setStyle("");
        optionD.setStyle("");
    }
}