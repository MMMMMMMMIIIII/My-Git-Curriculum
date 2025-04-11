package org.example.progettoprog3client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class MailApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(MailApplication.class.getResource("login-view.fxml"));
        Scene login = new Scene(fxmlLoader.load(), 320, 240);
        stage.setTitle("MailUniTO");
        stage.setHeight(500);
        stage.setWidth(800);
        stage.setMinHeight(500);
        stage.setMinWidth(800);
        stage.setScene(login);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}