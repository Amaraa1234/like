package com.example.controllers;

import javafx.animation.AnimationTimer;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcType;
import javafx.stage.Stage;

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
    private Button spinButton; // fx:id="spinButton" гэж FXML-д нэмнэ үү

    private int score = 0;
    private int rank = 1;
    private boolean isNewUser = true;
    private String currentUsername = "";
    private Stage stage;

    private boolean isSpinning = false; // Хүрд эргэж байгаа эсэхийг хянана

    // 10 асуулт
    private final String[] questions = {
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
    private final String[][] options = {
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
    private final int[] correctAnswers = { 2, 0, 0, 0, 0, 0, 0, 0, 0, 0 };

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
        disableAnswerButtons(true); // Эхэндээ хүрд эргүүлэх хүртэл хариулах боломжгүй
    }

    public void setUsername(String username) {
        this.currentUsername = username;
        kingLabel.setText("👑 " + username + " - Таны зэрэглэл");
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void initGame() {
        score = 0;
        isNewUser = false;
        updateScoreAndRank();
        loadQuestion(0);
        questionArea.setText("🎯 Хүрд эргүүлээд асуулт сонгоно уу!");
        resetButtons();
        disableAnswerButtons(true);
        currentAngle = 0;
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

        gc.clearRect(0, 0, width, height); // Өмнөх зургийг цэвэрлэх

        int sections = 10;
        String[] labels = { "1", "2", "3", "4", "5", "6", "7", "8", "9", "10" };

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

        for (int i = 0; i < sections; i++) {
            double startAngle = i * angleStep + currentAngle;
            double endAngle = startAngle + angleStep;

            gc.setFill(colors[i % colors.length]);
            gc.fillArc(
                    centerX - radius, centerY - radius,
                    radius * 2, radius * 2,
                    startAngle, endAngle - startAngle,
                    ArcType.ROUND);

            gc.setFill(Color.WHITE);
            gc.setFont(javafx.scene.text.Font.font(20));
            double midAngle = Math.toRadians(startAngle + angleStep / 2);
            double textX = centerX + radius * 0.65 * Math.cos(midAngle);
            double textY = centerY + radius * 0.65 * Math.sin(midAngle);
            gc.fillText(labels[i], textX - 10, textY + 8);
        }

        gc.setStroke(Color.BLACK);
        gc.setLineWidth(2);
        gc.strokeOval(centerX - radius, centerY - radius, radius * 2, radius * 2);

        gc.setFill(Color.WHITE);
        gc.fillOval(centerX - 15, centerY - 15, 30, 30);
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(2);
        gc.strokeOval(centerX - 15, centerY - 15, 30, 30);

        gc.setFill(Color.rgb(255, 0, 0));
        double[] xPoints = { centerX - 15, centerX + 15, centerX };
        double[] yPoints = { centerY - radius - 5, centerY - radius - 5, centerY - radius - 20 };
        gc.fillPolygon(xPoints, yPoints, 3);
    }

    /**
     * Хүрд эргүүлэх анимейшн — ЗӨВХӨН drawWheel-ээр дүрсэлнэ.
     * Node.setRotate ашиглахгүй тул анхны кодод байсан давхар эргэлт үүсэхгүй.
     */
    @FXML
    private void spinWheel() {
        if (isSpinning) {
            return; // Эргэж байх үед дахин дарахаас сэргийлнэ
        }
        isSpinning = true;
        if (spinButton != null) {
            spinButton.setDisable(true);
        }

        Random rand = new Random();
        int randomNumber = rand.nextInt(10) + 1;
        int questionIndex = randomNumber - 1;

        int startAngle = currentAngle;
        int totalRotation = 360 * 3 + (randomNumber - 1) * 36; // 3 бүтэн эргэлт + хүссэн хэсэг
        int endAngle = (startAngle + totalRotation) % 360;

        long durationMs = 1500;
        long startTime = System.currentTimeMillis();

        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                long elapsed = System.currentTimeMillis() - startTime;
                double progress = Math.min(1.0, elapsed / (double) durationMs);
                double eased = 1 - Math.pow(1 - progress, 3); // Ease-out: сүүлдээ удаашрана

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
        if (spinButton != null) {
            spinButton.setDisable(false);
        }
        loadQuestion(questionIndex);
        isAnswered = false;
        resetButtons();
        disableAnswerButtons(false);
        questionArea.setText("🎯 " + questions[questionIndex] + " (Асуулт №" + randomNumber + ")");
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
        int selectedIndex;

        if (clicked == optionA)
            selectedIndex = 0;
        else if (clicked == optionB)
            selectedIndex = 1;
        else if (clicked == optionC)
            selectedIndex = 2;
        else if (clicked == optionD)
            selectedIndex = 3;
        else
            return;

        if (selectedIndex == correctAnswers[currentQuestionIndex]) {
            score++;
            questionArea.setText("✅ Зөв хариулт! +1 оноо\n" + questions[currentQuestionIndex]);
            showCorrectAnswer(selectedIndex);
        } else {
            score = Math.max(0, score - 1); // Оноо 0-ээс доош орохгүй
            questionArea.setText("❌ Буруу хариулт! -1 оноо\nЗөв хариулт: "
                    + options[currentQuestionIndex][correctAnswers[currentQuestionIndex]]);
            showWrongAnswer(selectedIndex);
        }

        isAnswered = true;
        isNewUser = false;
        updateScoreAndRank();
        disableAnswerButtons(true); // Дараагийн spin хүртэл хариулах боломжгүй
    }

    private void showCorrectAnswer(int selectedIndex) {
        Button[] buttons = { optionA, optionB, optionC, optionD };
        buttons[selectedIndex].getStyleClass().add("option-correct");
    }

    private void showWrongAnswer(int selectedIndex) {
        Button[] buttons = { optionA, optionB, optionC, optionD };
        int correctIndex = correctAnswers[currentQuestionIndex];

        buttons[selectedIndex].getStyleClass().add("option-wrong");
        buttons[correctIndex].getStyleClass().add("option-correct");
    }

    private void updateScoreAndRank() {
        scoreLabel.setText("⭐ Оноо: " + score);

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