package com.example;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/login.fxml"));
        Parent root = loader.load();

        // LoginController-г аваад stage-г дамжуулах
        com.example.controllers.LoginController controller = loader.getController();
        controller.setStage(primaryStage); // ← ЭНД setStage() ДУУДАЖ БАЙНА

        primaryStage.setScene(new Scene(root, 500, 400));
        primaryStage.setTitle("🔐 Нэвтрэх - Асуултын Хүрд");
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}