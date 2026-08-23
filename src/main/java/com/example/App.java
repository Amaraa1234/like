package com.example;

import com.example.controllers.SpinWheelController;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {

    @Override
    public void start(Stage stage) {
        SpinWheelController mainUI = new SpinWheelController();
        Scene scene = new Scene(mainUI, 500, 450);

        stage.setTitle("Trivia Battle - Lucky Spin");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
