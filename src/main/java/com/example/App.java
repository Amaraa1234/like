package com.example;

import com.example.controllers.QuestionController;
import com.example.controllers.SpinWheelController;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class App extends Application {

    @Override
    public void start(Stage stage) {
        BorderPane root = new BorderPane();

        // Дэлгэцүүдийг үүсгэх
        SpinWheelController spinView = new SpinWheelController();
        QuestionController questionView = new QuestionController();

        // Навигацийн товчлуурууд
        Button btnSpin = new Button("Азын хүрд");
        Button btnQuiz = new Button("Асуулт хариулт");

        // Товчлууруудын стилийг тохируулах
        String btnStyle = "-fx-font-size: 14px; -fx-font-weight: bold; -fx-padding: 8 15; -fx-background-radius: 8;";
        btnSpin.setStyle(btnStyle);
        btnQuiz.setStyle(btnStyle);

        // Товчлуурын эвент
        btnSpin.setOnAction(e -> root.setCenter(spinView));
        btnQuiz.setOnAction(e -> root.setCenter(questionView));

        // Цэсийг дээд хэсэгт байрлуулах
        HBox navBar = new HBox(15, btnSpin, btnQuiz);
        navBar.setAlignment(Pos.CENTER);
        navBar.setStyle("-fx-padding: 12; -fx-background-color: #2c3e50;");

        root.setTop(navBar);
        root.setCenter(spinView); // Эхлээд Азын хүрд харагдана

        Scene scene = new Scene(root, 500, 550);
        stage.setTitle("Trivia Battle");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
