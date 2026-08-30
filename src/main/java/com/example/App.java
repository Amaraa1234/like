package com.example;

import com.example.utils.SceneManager;
import javafx.application.Application;
import javafx.stage.Stage;

public class App extends Application {

    @Override
    public void start(Stage stage) {
        // 1. Үндсэн Stage-ийг SceneManager-т тохируулна
        SceneManager.setPrimaryStage(stage);

        // 2. Эхний нэвтрэх дэлгэцийг ачаална
        SceneManager.switchScene("/login.fxml", "🔐 Нэвтрэх - Асуултын Хүрд");
    }

    public static void main(String[] args) {
        launch(args);
    }
}