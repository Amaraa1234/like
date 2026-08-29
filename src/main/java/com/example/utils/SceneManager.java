package com.example.utils;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class SceneManager {

    private static final Logger logger = LoggerFactory.getLogger(SceneManager.class);
    private static Stage primaryStage;

    private SceneManager() {
    }

    /**
     * Програм эхлэхэд үндсэн Stage-ийг тохируулна (App.java-аас дуудна)
     */
    public static void setPrimaryStage(Stage stage) {
        primaryStage = stage;
    }

    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    /**
     * Дэлгэц солих үндсэн метод
     * 
     * @param fxmlPath FXML файлын зам (жишээ нь: "/login.fxml", "/game.fxml")
     * @param title    Цонхны гарчиг
     */
    public static void switchScene(String fxmlPath, String title) {
        if (primaryStage == null) {
            logger.error(
                    "Primary Stage тохируулагдаагүй байна! App.java дээр SceneManager.setPrimaryStage(stage) дуудсан эсэхийг шалгана уу.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(SceneManager.class.getResource(fxmlPath));
            Parent root = loader.load();

            Scene scene = new Scene(root, 900, 750);

            // CSS файл байгаа бол автоматаар уншуулна
            var cssResource = SceneManager.class.getResource("/css/style.css");
            if (cssResource != null) {
                scene.getStylesheets().add(cssResource.toExternalForm());
            }

            primaryStage.setTitle(title);
            primaryStage.setScene(scene);
            primaryStage.centerOnScreen();
            primaryStage.show();

            logger.info("Дэлгэц амжилттай солигдлоо: {}", fxmlPath);

        } catch (IOException e) {
            logger.error("Дэлгэц ачааллахад алдаа гарлаа: " + fxmlPath, e);
        }
    }
}