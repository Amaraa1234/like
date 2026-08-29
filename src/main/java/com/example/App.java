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
        com.example.controllers.LoginController controller = loader.getController();
        controller.setStage(primaryStage);
        primaryStage.setScene(new Scene(root, 500, 400));
        primaryStage.setTitle("🔐 Нэвтрэх - Асуултын Хүрд");
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
package com.example;

import com.example.utils.SceneManager;
import javafx.application.Application;
import javafx.stage.Stage;

public class App extends Application {

    @Override
    public void start(Stage stage) {
        // 1. Үндсэн stage-ийг SceneManager-т тохируулна
        SceneManager.setPrimaryStage(stage);

        // 2. Эхний нэвтрэх дэлгэцийг ачаална
        SceneManager.switchScene("/login.fxml", "🔐 Нэвтрэх");
    }

    public static void main(String[] args) {
        launch(args);
    }
}