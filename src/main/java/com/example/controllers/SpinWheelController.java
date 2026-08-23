package com.example.controllers;

import com.example.database.UserDAO;
import javafx.animation.RotateTransition;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Arc;
import javafx.scene.shape.ArcType;
import javafx.scene.shape.Polygon;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.util.Random;

public class SpinWheelController extends VBox {
    private Pane wheelGroup;
    private Label resultLabel;
    private Button spinButton;
    private boolean isSpinning = false;
    private UserDAO userDAO = new UserDAO();

    private String[] rewards = { "+1 Амь", "+500 Зоос", "+2 Амь", "+100 Зоос", "+3 Амь", "+200 Зоос" };
    private Color[] colors = {
            Color.web("#E74C3C"), Color.web("#3498DB"),
            Color.web("#2ECC71"), Color.web("#F1C40F"),
            Color.web("#9B59B6"), Color.web("#E67E22")
    };

    public SpinWheelController() {
        setAlignment(Pos.CENTER);
        setSpacing(20);

        StackPane wheelContainer = new StackPane();
        wheelGroup = new Pane();
        wheelGroup.setPrefSize(260, 260);
        wheelGroup.setMaxSize(260, 260);

        double radius = 130;
        int numSegments = rewards.length;
        double angleStep = 360.0 / numSegments;

        // Хүрдийг салбар хэсгүүдэд хуваан өнгө, тексттэй зурах
        for (int i = 0; i < numSegments; i++) {
            Arc arc = new Arc(radius, radius, radius, radius, i * angleStep, angleStep);
            arc.setType(ArcType.ROUND);
            arc.setFill(colors[i % colors.length]);
            arc.setStroke(Color.WHITE);
            arc.setStrokeWidth(2);

            double angleRad = Math.toRadians((i + 0.5) * angleStep);
            double textX = radius + (radius * 0.6) * Math.cos(angleRad) - 20;
            double textY = radius - (radius * 0.6) * Math.sin(angleRad) + 5;

            Text label = new Text(rewards[i]);
            label.setFont(Font.font("Arial", FontWeight.BOLD, 12));
            label.setFill(Color.WHITE);
            label.setX(textX);
            label.setY(textY);

            wheelGroup.getChildren().addAll(arc, label);
        }

        // Заагч сум (Улаан гурвалжин)
        Polygon pointer = new Polygon();
        pointer.getPoints().addAll(0.0, 0.0, 20.0, 0.0, 10.0, 25.0);
        pointer.setFill(Color.DARKRED);
        pointer.setStroke(Color.WHITE);
        pointer.setStrokeWidth(1);

        wheelContainer.getChildren().addAll(wheelGroup, pointer);
        StackPane.setAlignment(pointer, Pos.TOP_CENTER);

        resultLabel = new Label("Азын хүрдээ эргүүлээрэй!");
        resultLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));

        spinButton = new Button("SPIN!");
        spinButton.setStyle(
                "-fx-background-color: #2ecc71; -fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold; -fx-padding: 8 25 8 25; -fx-background-radius: 20;");
        spinButton.setOnAction(e -> spin());

        getChildren().addAll(wheelContainer, resultLabel, spinButton);
    }

    private void spin() {
        if (isSpinning)
            return;
        isSpinning = true;
        spinButton.setDisable(true);

        Random random = new Random();
        int randomIndex = random.nextInt(rewards.length);
        double angleStep = 360.0 / rewards.length;

        // Сум яг орой дээрээс (90 градус) зааж байгаа тул өнцгийн тооцооллыг тааруулах
        double segmentCenterAngle = (randomIndex + 0.5) * angleStep;
        double targetRotation = (360 * 5) + (90.0 - segmentCenterAngle);

        RotateTransition rotate = new RotateTransition(Duration.seconds(3.5), wheelGroup);
        rotate.setByAngle(targetRotation - (wheelGroup.getRotate() % 360));
        rotate.setInterpolator(javafx.animation.Interpolator.EASE_OUT);

        rotate.setOnFinished(e -> {
            isSpinning = false;
            spinButton.setDisable(false);
            String reward = rewards[randomIndex];
            resultLabel.setText("Шагнал: " + reward);

            // Арын Thread дээр бааз руу хадгалах
            new Thread(() -> {
                try {
                    if (reward.contains("Амь")) {
                        int lives = Integer.parseInt(reward.replaceAll("[^0-9]", ""));
                        userDAO.updateUserStats("Amaraa", lives, 0);
                    } else if (reward.contains("Зоос")) {
                        int coins = Integer.parseInt(reward.replaceAll("[^0-9]", ""));
                        userDAO.updateUserStats("Amaraa", 0, coins);
                    }
                } catch (Exception ex) {
                    System.err.println("Бааз руу хадгалахад алдаа гарлаа: " + ex.getMessage());
                }
            }).start();
        });

        rotate.play();
    }
}