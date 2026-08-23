package com.example.controllers;

import com.example.database.UserDAO;
import javafx.animation.RotateTransition;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import javafx.scene.text.Font;
import javafx.util.Duration;

import java.util.Random;

public class SpinWheelController extends VBox {
    private Circle wheel;
    private Label resultLabel;
    private Button spinButton;
    private boolean isSpinning = false;
    private UserDAO userDAO = new UserDAO();

    private String[] rewards = { "+1 Амь", "+500 Зоос", "+2 Амь", "+100 Зоос", "+3 Амь", "+200 Зоос" };

    public SpinWheelController() {
        setAlignment(Pos.CENTER);
        setSpacing(20);

        StackPane wheelStack = new StackPane();

        wheel = new Circle(120);
        wheel.setFill(Color.web("#3498db"));
        wheel.setStroke(Color.web("#2c3e50"));
        wheel.setStrokeWidth(6);

        Polygon pointer = new Polygon();
        pointer.getPoints().addAll(0.0, 0.0, 20.0, 0.0, 10.0, 25.0);
        pointer.setFill(Color.RED);
        StackPane.setAlignment(pointer, Pos.TOP_CENTER);

        wheelStack.getChildren().addAll(wheel, pointer);

        resultLabel = new Label("Азын хүрдээ эргүүлээрэй!");
        resultLabel.setFont(new Font("Arial", 16));

        spinButton = new Button("SPIN!");
        spinButton.setStyle(
                "-fx-background-color: #2ecc71; -fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold;");
        spinButton.setOnAction(e -> spin());

        getChildren().addAll(wheelStack, resultLabel, spinButton);
    }

    private void spin() {
        if (isSpinning)
            return;
        isSpinning = true;

        Random random = new Random();
        int randomIndex = random.nextInt(rewards.length);
        int anglePerSegment = 360 / rewards.length;
        int targetAngle = (360 * 5) + (randomIndex * anglePerSegment);

        RotateTransition rotate = new RotateTransition(Duration.seconds(3), wheel);
        rotate.setByAngle(targetAngle);
        rotate.setInterpolator(javafx.animation.Interpolator.EASE_OUT);
        rotate.setOnFinished(e -> {
            isSpinning = false;
            String reward = rewards[randomIndex];
            resultLabel.setText("Шагнал: " + reward);

            // MySQL рүү шагналыг хадгалах
            if (reward.contains("Амь")) {
                int lives = Integer.parseInt(reward.replaceAll("[^0-9]", ""));
                userDAO.updateUserStats("Amaraa", lives, 0);
            } else if (reward.contains("Зоос")) {
                int coins = Integer.parseInt(reward.replaceAll("[^0-9]", ""));
                userDAO.updateUserStats("Amaraa", 0, coins);
            }
        });
        rotate.play();
    }
}