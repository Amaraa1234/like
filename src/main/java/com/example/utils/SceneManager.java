package com.example.utils;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;

public class SceneManager {

    private static final Logger logger = LoggerFactory.getLogger(SceneManager.class);
    private static Stage primaryStage;

    private SceneManager() {
    }

    public static void setPrimaryStage(Stage stage) {
        primaryStage = stage;
    }

    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    public static void switchScene(String fxmlPath, String title) {
        if (primaryStage == null) {
            logger.error(
                    "Primary Stage тохируулагдаагүй байна! App.java дээр SceneManager.setPrimaryStage(stage) дуудсан эсэхийг шалгана уу.");
            return;
        }

        try {
            // 1. FXML файлын URL авч, null эсэхийг тусгайлан шалгах
            URL resource = SceneManager.class.getResource(fxmlPath);
            if (resource == null) {
                logger.error("❌ FXML файл олдсонгүй! Файлын зам буруу байна: {}", fxmlPath);
                return;
            }

            FXMLLoader loader = new FXMLLoader(resource);
            Parent root = loader.load();

            Scene scene = new Scene(root, 900, 750);

            // CSS файл байгаа бол автоматаар уншуулна
            URL cssResource = SceneManager.class.getResource("/css/style.css");
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
        } catch (Exception e) {
            // FXML доторх контроллерт (GameController) гарсан бусад Exception-ийг барьж
            // авах
            logger.error("Дэлгэцийн контроллер ачааллахад алдаа гарлаа: " + fxmlPath, e);
        }
    }
}