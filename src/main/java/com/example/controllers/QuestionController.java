package com.example.controllers;

import com.example.database.QuestionDAO;
import com.example.database.UserDAO;
import com.example.models.Question;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.util.List;

public class QuestionController extends VBox {
    private Label questionLabel;
    private Button btnA, btnB, btnC, btnD;
    private Label statusLabel;

    private QuestionDAO questionDAO = new QuestionDAO();
    private UserDAO userDAO = new UserDAO();
    private List<Question> questions;
    private int currentIndex = 0;

    public QuestionController() {
        setAlignment(Pos.CENTER);
        setSpacing(15);
        setStyle("-fx-padding: 20;");

        questionLabel = new Label("Асуулт ачаалж байна...");
        questionLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        questionLabel.setWrapText(true);

        btnA = createOptionButton("A");
        btnB = createOptionButton("B");
        btnC = createOptionButton("C");
        btnD = createOptionButton("D");

        statusLabel = new Label();
        statusLabel.setFont(Font.font("Arial", 14));

        getChildren().addAll(questionLabel, btnA, btnB, btnC, btnD, statusLabel);

        loadQuestions();
    }

    private Button createOptionButton(String optionKey) {
        Button btn = new Button();
        btn.setMaxWidth(350);
        btn.setStyle("-fx-font-size: 14px; -fx-padding: 8;");
        btn.setOnAction(e -> checkAnswer(optionKey));
        return btn;
    }

    private void loadQuestions() {
        questions = questionDAO.getAllQuestions();
        if (questions != null && !questions.isEmpty()) {
            displayQuestion(questions.get(currentIndex));
        } else {
            questionLabel.setText("Баазад асуулт олдсонгүй!");
        }
    }

    private void displayQuestion(Question q) {
        questionLabel.setText((currentIndex + 1) + ". " + q.getQuestionText());
        btnA.setText("A. " + q.getOptionA());
        btnB.setText("B. " + q.getOptionB());
        btnC.setText("C. " + q.getOptionC());
        btnD.setText("D. " + q.getOptionD());
        statusLabel.setText("");
    }

    private void checkAnswer(String selectedOption) {
        if (questions == null || currentIndex >= questions.size())
            return;

        Question currentQuestion = questions.get(currentIndex);
        if (currentQuestion.getCorrectOption().equalsIgnoreCase(selectedOption)) {
            statusLabel.setText("Зөв хариуллаа! (+100 оноо)");
            statusLabel.setStyle("-fx-text-fill: green;");

            // Бааз руу оноо нэмэх
            new Thread(() -> userDAO.updateScore("Amaraa", 100)).start();
        } else {
            statusLabel.setText("Буруу хариуллаа! Зөв нь: " + currentQuestion.getCorrectOption());
            statusLabel.setStyle("-fx-text-fill: red;");
        }

        // Дараагийн асуулт руу шилжих
        currentIndex++;
        if (currentIndex < questions.size()) {
            questionLabel.postSceneCompletion(() -> displayQuestion(questions.get(currentIndex)));
        } else {
            statusLabel.setText(statusLabel.getText() + " | Тэмцээн дууслаа!");
        }
    }
}