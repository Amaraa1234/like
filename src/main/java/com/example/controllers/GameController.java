package com.example.controllers;

import com.example.database.QuestionDAO;
import com.example.database.UserDAO;
import com.example.models.Option;
import com.example.models.Question;
import com.example.models.User;
import com.example.utils.SceneManager;
import com.example.utils.UserSession;
import javafx.animation.AnimationTimer;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.VPos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcType;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;

import java.util.List;
import java.util.Random;

public class GameController {

    @FXML
    private Label scoreLabel;
    @FXML
    private Label rankLabel;
    @FXML
    private Label kingLabel;
    @FXML
    private Canvas wheelCanvas;
    @FXML
    private TextArea questionArea;
    @FXML
    private Button optionA;
    @FXML
    private Button optionB;
    @FXML
    private Button optionC;
    @FXML
    private Button optionD;
    @FXML
    private Button spinButton;

    private final QuestionDAO questionDAO = new QuestionDAO();
    private final UserDAO userDAO = new UserDAO();

    private List<Question> questionList;
    private Question currentQuestion;
    private User currentUser;

    private boolean isSpinning = false;
    private boolean isAnswered = false;
    private int currentAngle = 0;

    @FXML
    public void initialize() {
        // Session-оос одоо нэвтэрсэн хэрэглэгчийг авна
        currentUser = UserSession.getInstance().getCurrentUser();

        // Асуултуудыг баазаас ачаална
        questionList = questionDAO.getAllQuestions();

        drawWheel();
        updateScoreAndRank();
        disableAnswerButtons(true);

        if (currentUser != null) {
            kingLabel.setText("👑 " + currentUser.getUsername());
        }
        questionArea.setText("🎯 Хүрд эргүүлээд асуултаа сонгоно уу!");
    }

    /**
     * Хүрд зурах хэсэг (Зөв координатын систем болон тод тексттэй)
     */
    private void drawWheel() {
        GraphicsContext gc = wheelCanvas.getGraphicsContext2D();
        double width = wheelCanvas.getWidth();
        double height = wheelCanvas.getHeight();
        double centerX = width / 2;
        double centerY = height / 2;
        double radius = Math.min(width, height) / 2 - 10;

        gc.clearRect(0, 0, width, height);

        int sections = 10;
        String[] labels = { "1", "2", "3", "4", "5", "6", "7", "8", "9", "10" };

        Color[] colors = {
                Color.rgb(255, 107, 107), Color.rgb(255, 193, 7),
                Color.rgb(76, 175, 80), Color.rgb(33, 150, 243),
                Color.rgb(156, 39, 176), Color.rgb(255, 152, 0),
                Color.rgb(0, 188, 212), Color.rgb(233, 30, 99),
                Color.rgb(139, 195, 74), Color.rgb(96, 125, 139)
        };

        double angleStep = 360.0 / sections;

        for (int i = 0; i < sections; i++) {
            double startAngle = i * angleStep + currentAngle;

            // 1. Сектор будах
            gc.setFill(colors[i % colors.length]);
            gc.fillArc(
                    centerX - radius, centerY - radius,
                    radius * 2, radius * 2,
                    startAngle, angleStep,
                    ArcType.ROUND);

            // 2. Сектор хоорондын тусгаарлагч шугам
            gc.setStroke(Color.BLACK);
            gc.setLineWidth(1.5);
            gc.strokeArc(
                    centerX - radius, centerY - radius,
                    radius * 2, radius * 2,
                    startAngle, angleStep,
                    ArcType.ROUND);

            // 3. Тоо / Текст зурах хэсэг
            double midAngleDeg = startAngle + angleStep / 2.0;
            // JavaFX Y-тэнхлэг доошоо чиглэдэг тул сайн тааруулахын тулд хасах заагч
            // ашиглана
            double midAngleRad = Math.toRadians(-midAngleDeg);

            double textX = centerX + (radius * 0.65) * Math.cos(midAngleRad);
            double textY = centerY + (radius * 0.65) * Math.sin(midAngleRad);

            gc.save();
            gc.setTextAlign(TextAlignment.CENTER);
            gc.setTextBaseline(VPos.CENTER);
            gc.setFont(Font.font("Arial", FontWeight.BOLD, 18));

            // Хар хүрээ (Тоог илүү тод болгоно)
            gc.setLineWidth(3);
            gc.setStroke(Color.BLACK);
            gc.strokeText(labels[i], textX, textY);

            // Цагаан дотор тал
            gc.setFill(Color.WHITE);
            gc.fillText(labels[i], textX, textY);
            gc.restore();
        }

        // Хүрдний гадна хүрээ
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(2);
        gc.strokeOval(centerX - radius, centerY - radius, radius * 2, radius * 2);

        // Хүрдний төв болон заагч зүү
        gc.setFill(Color.WHITE);
        gc.fillOval(centerX - 15, centerY - 15, 30, 30);
        gc.strokeOval(centerX - 15, centerY - 15, 30, 30);

        gc.setFill(Color.rgb(255, 0, 0));
        double[] xPoints = { centerX - 12, centerX + 12, centerX };
        double[] yPoints = { centerY - radius - 5, centerY - radius - 5, centerY - radius - 20 };
        gc.fillPolygon(xPoints, yPoints, 3);
    }

    @FXML
    private void spinWheel() {
        if (isSpinning || questionList == null || questionList.isEmpty())
            return;

        isSpinning = true;
        if (spinButton != null)
            spinButton.setDisable(true);

        Random rand = new Random();
        int randomNumber = rand.nextInt(Math.min(10, questionList.size())) + 1;
        int questionIndex = randomNumber - 1;

        int startAngle = currentAngle;
        int totalRotation = 360 * 3 + (randomNumber - 1) * 36;
        int endAngle = (startAngle + totalRotation) % 360;

        long durationMs = 1500;
        long startTime = System.currentTimeMillis();

        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                long elapsed = System.currentTimeMillis() - startTime;
                double progress = Math.min(1.0, elapsed / (double) durationMs);
                double eased = 1 - Math.pow(1 - progress, 3);

                currentAngle = (int) (startAngle + totalRotation * eased) % 360;
                drawWheel();

                if (progress >= 1.0) {
                    stop();
                    currentAngle = endAngle;
                    drawWheel();
                    onSpinFinished(questionIndex, randomNumber);
                }
            }
        };
        timer.start();
    }

    private void onSpinFinished(int questionIndex, int randomNumber) {
        isSpinning = false;
        if (spinButton != null)
            spinButton.setDisable(false);

        currentQuestion = questionList.get(questionIndex);
        loadQuestionData(currentQuestion, randomNumber);

        isAnswered = false;
        resetButtons();
        disableAnswerButtons(false);
    }

    private void loadQuestionData(Question q, int number) {
        questionArea.setText("🎯 (Асуулт №" + number + ") " + q.text());
        optionA.setText("A. " + q.optionA());
        optionB.setText("B. " + q.optionB());
        optionC.setText("C. " + q.optionC());
        optionD.setText("D. " + q.optionD());
    }

    @FXML
    private void handleAnswer(ActionEvent event) {
        if (isAnswered || currentQuestion == null)
            return;

        Button clicked = (Button) event.getSource();
        Option selectedOption;

        if (clicked == optionA)
            selectedOption = Option.A;
        else if (clicked == optionB)
            selectedOption = Option.B;
        else if (clicked == optionC)
            selectedOption = Option.C;
        else if (clicked == optionD)
            selectedOption = Option.D;
        else
            return;

        if (selectedOption == currentQuestion.correctOption()) {
            if (currentUser != null) {
                currentUser.setScore(currentUser.getScore() + 1);
            }
            questionArea.setText("✅ Зөв хариуллаа! (+1 оноо)\n" + currentQuestion.text());
            highlightButton(clicked, "option-correct");
        } else {
            if (currentUser != null) {
                currentUser.setScore(Math.max(0, currentUser.getScore() - 1));
            }
            questionArea.setText("❌ Буруу хариуллаа! (-1 оноо)\nЗөв хариулт: " + currentQuestion.correctOption());
            highlightButton(clicked, "option-wrong");
            highlightCorrectOption(currentQuestion.correctOption());
        }

        // Өгөгдлийн санд одоогийн оноог хадгалах
        if (currentUser != null) {
            userDAO.updateScore(currentUser.getId(), currentUser.getScore());
        }

        isAnswered = true;
        updateScoreAndRank();
        disableAnswerButtons(true);
    }

    private void highlightButton(Button btn, String styleClass) {
        btn.getStyleClass().add(styleClass);
    }

    private void highlightCorrectOption(Option correct) {
        switch (correct) {
            case A -> optionA.getStyleClass().add("option-correct");
            case B -> optionB.getStyleClass().add("option-correct");
            case C -> optionC.getStyleClass().add("option-correct");
            case D -> optionD.getStyleClass().add("option-correct");
        }
    }

    private void updateScoreAndRank() {
        int score = currentUser != null ? currentUser.getScore() : 0;
        scoreLabel.setText("⭐ Оноо: " + score);

        int rank = 5;
        if (score >= 10)
            rank = 1;
        else if (score >= 7)
            rank = 2;
        else if (score >= 4)
            rank = 3;
        else if (score >= 1)
            rank = 4;

        rankLabel.setText("🏆 Байр: №" + rank);
    }

    private void resetButtons() {
        Button[] buttons = { optionA, optionB, optionC, optionD };
        for (Button b : buttons) {
            b.getStyleClass().removeAll("option-correct", "option-wrong");
        }
    }

    private void disableAnswerButtons(boolean disable) {
        optionA.setDisable(disable);
        optionB.setDisable(disable);
        optionC.setDisable(disable);
        optionD.setDisable(disable);
    }

    @FXML
    private void logout() {
        UserSession.getInstance().cleanUserSession();
        SceneManager.switchScene("/login.fxml", "🔐 Нэвтрэх");
    }

    @FXML
    private void openHelp() {
        questionArea.setText("❓ ТУСЛАМЖ\n\n"
                + "🎡 Хүрд эргүүлэн асуулт сонгоно уу!\n"
                + "✅ Зөв хариулт → +1 оноо\n"
                + "❌ Буруу хариулт → -1 оноо\n"
                + "🏆 Баазад оноо автоматаар хадгалагдана.");
    }
}