package com.example.controllers;

import javafx.animation.RotateTransition;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcType;
import javafx.stage.Stage;
import javafx.util.Duration;

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

    private int score = 0;
    private int rank = 1;
    private boolean isNewUser = true;
    private String currentUsername = "";
    private Stage stage;

    // 10 асуулт
    private String[] questions = {
            "Java-г ямар компани бүтээсэн бэ?",
            "Монгол улсын нийслэл аль вэ?",
            "Дэлхийн хамгийн өндөр уул аль вэ?",
            "Нарны аймгийн хамгийн том гараг аль вэ?",
            "Усны химийн томъёо юу вэ?",
            "Дэлхийн хамгийн том далай аль вэ?",
            "Монгол улсын төрийн дуулал юу вэ?",
            "Гэрлийн хурд хэд вэ?",
            "Компьютерийн үндсэн хэл аль вэ?",
            "Дэлхийн хамгийн урт гол аль вэ?"
    };

    // Хариултууд
    private String[][] options = {
            { "Microsoft", "Oracle", "Sun Microsystems", "Google" },
            { "Улаанбаатар", "Дархан", "Эрдэнэт", "Ховд" },
            { "Эверест", "К2", "Канченжанга", "Лхоцзе" },
            { "Бархасбадь", "Санчир", "Дэлхий", "Сугар" },
            { "H2O", "CO2", "NaCl", "HCl" },
            { "Номхон далай", "Атлантын далай", "Энэтхэгийн далай", "Арктикийн далай" },
            { "Монгол Улсын төрийн дуулал", "Монгол Улсын төрийн сүлд", "Монгол Улсын төрийн далбаа",
                    "Монгол Улсын төрийн цол" },
            { "299,792,458 м/с", "300,000,000 м/с", "299,792,458 км/с", "300,000,000 км/с" },
            { "Machine Code", "Assembly", "C", "Java" },
            { "Нил мөрөн", "Амазон мөрөн", "Хар мөрөн", "Ганга мөрөн" }
    };

    // Зөв хариултын индексүүд
    private int[] correctAnswers = { 2, 0, 0, 0, 0, 0, 0, 0, 0, 0 };

    private int currentQuestionIndex = 0;
    private boolean isAnswered = false;
    private int currentAngle = 0;

    @FXML
    public void initialize() {
        drawWheel();
        if (isNewUser) {
            score = 0;
            updateScoreAndRank();
        }
        loadQuestion(0);
        optionA.setDisable(false);
        optionB.setDisable(false);
        optionC.setDisable(false);
        optionD.setDisable(false);
    }

    public void setUsername(String username) { // ← ЭНЭ МЕТОД
        this.currentUsername = username;
        kingLabel.setText("👑 " + username + " - Таны зэрэглэл");
    }

    /**
     * Stage-г тохируулах
     */
    public void setStage(Stage stage) {
        this.stage = stage;
    }

    /**
     * Тоглоомыг эхлүүлэх
     */
    public void initGame() {
        score = 0;
        isNewUser = false;
        updateScoreAndRank();
        loadQuestion(0);
        questionArea.setText("🎯 Хүрд эргүүлээд асуулт сонгоно уу!");
        resetButtons();
        drawWheel();
    }

    /**
     * Хүрд зурах (1-ээс 10 хүртэлх тоотой)
     */
    private void drawWheel() {
        GraphicsContext gc = wheelCanvas.getGraphicsContext2D();
        double width = wheelCanvas.getWidth();
        double height = wheelCanvas.getHeight();
        double centerX = width / 2;
        double centerY = height / 2;
        double radius = Math.min(width, height) / 2 - 10;

        int sections = 10; // 1-ээс 10 хүртэл
        String[] labels = { "1", "2", "3", "4", "5", "6", "7", "8", "9", "10" };

        // 10 өөр өнгө
        Color[] colors = {
                Color.rgb(255, 107, 107),
                Color.rgb(255, 193, 7),
                Color.rgb(76, 175, 80),
                Color.rgb(33, 150, 243),
                Color.rgb(156, 39, 176),
                Color.rgb(255, 152, 0),
                Color.rgb(0, 188, 212),
                Color.rgb(233, 30, 99),
                Color.rgb(139, 195, 74),
                Color.rgb(96, 125, 139)
        };

        double angleStep = 360.0 / sections;

        // Хүрдийг эргүүлсэн өнцгөөр зурах
        for (int i = 0; i < sections; i++) {
            double startAngle = i * angleStep + currentAngle;
            double endAngle = startAngle + angleStep;

            gc.setFill(colors[i % colors.length]);
            gc.fillArc(
                    centerX - radius, centerY - radius,
                    radius * 2, radius * 2,
                    startAngle, endAngle - startAngle,
                    ArcType.ROUND);

            // Текст зурах
            gc.setFill(Color.WHITE);
            gc.setFont(javafx.scene.text.Font.font(20));
            double midAngle = Math.toRadians(startAngle + angleStep / 2);
            double textX = centerX + radius * 0.65 * Math.cos(midAngle);
            double textY = centerY + radius * 0.65 * Math.sin(midAngle);
            gc.fillText(labels[i], textX - 10, textY + 8);
        }

        // Гадна тойрог
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(2);
        gc.strokeOval(centerX - radius, centerY - radius, radius * 2, radius * 2);

        // Дотор тойрог
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(2);
        gc.strokeOval(centerX - 5, centerY - 5, 10, 10);

        // Төв цэг
        gc.setFill(Color.WHITE);
        gc.fillOval(centerX - 15, centerY - 15, 30, 30);
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(2);
        gc.strokeOval(centerX - 15, centerY - 15, 30, 30);

        // Сум (дээд талд)
        gc.setFill(Color.rgb(255, 0, 0));
        double[] xPoints = { centerX - 15, centerX + 15, centerX };
        double[] yPoints = { centerY - radius - 5, centerY - radius - 5, centerY - radius - 20 };
        gc.fillPolygon(xPoints, yPoints, 3);
    }

    /**
     * Хүрд эргүүлэх анимейшн
     */
    @FXML
    private void spinWheel() {
        Random rand = new Random();
        int randomNumber = rand.nextInt(10) + 1; // 1-ээс 10 хүртэлх тоо
        int questionIndex = randomNumber - 1;

        // Хүрдийг эргүүлэх анимейшн
        RotateTransition rotate = new RotateTransition(Duration.millis(1500), wheelCanvas);
        double targetAngle = 360 * 3 + (randomNumber - 1) * 36; // 3 бүтэн эргэлт + хүссэн хэсэг
        rotate.setByAngle(targetAngle);
        rotate.setCycleCount(1);

        // Анимейшн дууссаны дараа
        rotate.setOnFinished(e -> {
            currentAngle = (currentAngle + (int) targetAngle) % 360;
            drawWheel();

            // Сонгогдсон асуултыг харуулах
            loadQuestion(questionIndex);
            isAnswered = false;
            resetButtons();
            questionArea.setText("🎯 " + questions[questionIndex] + " (Асуулт №" + randomNumber + ")");
        });

        rotate.play();
    }

    private void loadQuestion(int index) {
        if (index < questions.length) {
            currentQuestionIndex = index;
            questionArea.setText(questions[index]);
            optionA.setText("A. " + options[index][0]);
            optionB.setText("B. " + options[index][1]);
            optionC.setText("C. " + options[index][2]);
            optionD.setText("D. " + options[index][3]);
            isAnswered = false;
        }
    }

    @FXML
    private void handleAnswer(javafx.event.ActionEvent event) {
        if (isAnswered) {
            questionArea.setText("⏳ Энэ асуултад хариулсан байна! Хүрд эргүүлээд шинэ асуулт сонго.");
            return;
        }

        Button clicked = (Button) event.getSource();
        int selectedIndex = -1;

        if (clicked == optionA)
            selectedIndex = 0;
        else if (clicked == optionB)
            selectedIndex = 1;
        else if (clicked == optionC)
            selectedIndex = 2;
        else if (clicked == optionD)
            selectedIndex = 3;

        if (selectedIndex == -1)
            return;

        // Өмнөх оноог хадгалах
        int oldScore = score;

        if (selectedIndex == correctAnswers[currentQuestionIndex]) {
            score++;
            questionArea.setText("✅ Зөв хариулт! +1 оноо\n" + questions[currentQuestionIndex]);
            showCorrectAnswer(selectedIndex);
        } else {
            score--;
            questionArea.setText("❌ Буруу хариулт! -1 оноо\nЗөв хариулт: "
                    + options[currentQuestionIndex][correctAnswers[currentQuestionIndex]]);
            showWrongAnswer(selectedIndex);
        }

        isAnswered = true;
        isNewUser = false;
        updateScoreAndRank();
    }

    private void showCorrectAnswer(int selectedIndex) {
        Button[] buttons = { optionA, optionB, optionC, optionD };
        for (int i = 0; i < buttons.length; i++) {
            if (i == selectedIndex) {
                buttons[i].setStyle(
                        "-fx-background-color: #4caf50; -fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold; -fx-padding: 12 40; -fx-background-radius: 12; -fx-border-color: #2e7d32; -fx-border-radius: 12; -fx-border-width: 2; -fx-cursor: hand;");
            }
        }
    }

    private void showWrongAnswer(int selectedIndex) {
        Button[] buttons = { optionA, optionB, optionC, optionD };
        int correctIndex = correctAnswers[currentQuestionIndex];

        for (int i = 0; i < buttons.length; i++) {
            if (i == selectedIndex) {
                buttons[i].setStyle(
                        "-fx-background-color: #f44336; -fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold; -fx-padding: 12 40; -fx-background-radius: 12; -fx-border-color: #c62828; -fx-border-radius: 12; -fx-border-width: 2; -fx-cursor: hand;");
            } else if (i == correctIndex) {
                buttons[i].setStyle(
                        "-fx-background-color: #4caf50; -fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold; -fx-padding: 12 40; -fx-background-radius: 12; -fx-border-color: #2e7d32; -fx-border-radius: 12; -fx-border-width: 2; -fx-cursor: hand;");
            }
        }
    }

    private void updateScoreAndRank() {
        // Оноо гэж нэрлэх
        scoreLabel.setText("⭐ Оноо: " + score);

        // Ранкын тооцоо
        if (score >= 10)
            rank = 1;
        else if (score >= 7)
            rank = 2;
        else if (score >= 4)
            rank = 3;
        else if (score >= 1)
            rank = 4;
        else
            rank = 5;

        // Ранк гэдгийг Оноо гэж солих
        rankLabel.setText("🏆 Ранк: " + rank);

        String rankName;
        switch (rank) {
            case 1:
                rankName = "АЛТАН 🥇";
                break;
            case 2:
                rankName = "МӨНГӨН 🥈";
                break;
            case 3:
                rankName = "ХҮРЭЛ 🥉";
                break;
            case 4:
                rankName = "ХҮРЭЛЗЭМ ⭐";
                break;
            case 5:
                rankName = "ГЭРЭЛТЭЙ 🌟";
                break;
            default:
                rankName = "ШИНЭ";
        }

        kingLabel.setText("👑 " + currentUsername + " - " + rankName);
    }

    private void resetButtons() {
        optionA.setStyle(
                "-fx-background-color: #c8e6c9; -fx-text-fill: #1b5e20; -fx-font-size: 16px; -fx-font-weight: bold; -fx-padding: 12 40; -fx-background-radius: 12; -fx-border-color: #388e3c; -fx-border-radius: 12; -fx-border-width: 2; -fx-cursor: hand;");
        optionB.setStyle(
                "-fx-background-color: #fff9c4; -fx-text-fill: #f57f17; -fx-font-size: 16px; -fx-font-weight: bold; -fx-padding: 12 40; -fx-background-radius: 12; -fx-border-color: #f9a825; -fx-border-radius: 12; -fx-border-width: 2; -fx-cursor: hand;");
        optionC.setStyle(
                "-fx-background-color: #b3e5fc; -fx-text-fill: #01579b; -fx-font-size: 16px; -fx-font-weight: bold; -fx-padding: 12 40; -fx-background-radius: 12; -fx-border-color: #0288d1; -fx-border-radius: 12; -fx-border-width: 2; -fx-cursor: hand;");
        optionD.setStyle(
                "-fx-background-color: #ffccbc; -fx-text-fill: #bf360c; -fx-font-size: 16px; -fx-font-weight: bold; -fx-padding: 12 40; -fx-background-radius: 12; -fx-border-color: #d84315; -fx-border-radius: 12; -fx-border-width: 2; -fx-cursor: hand;");
    }

    @FXML
    private void logout() {
        if (stage != null) {
            stage.close();
        }
    }

    @FXML
    private void openHelp() {
        questionArea.setText("❓ ТУСЛАМЖ\n\n"
                + "🎡 Хүрд эргүүлэн асуулт сонгоно уу!\n"
                + "✅ Зөв хариулт → +1 оноо\n"
                + "❌ Буруу хариулт → -1 оноо\n"
                + "🏆 Ранк нь онооноос хамаарна.\n"
                + "👑 Өндөр зэрэглэлд хүрэхийг хүсье!");
    }

}