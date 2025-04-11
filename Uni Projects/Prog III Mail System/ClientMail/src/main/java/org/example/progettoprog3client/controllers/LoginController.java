package org.example.progettoprog3client.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.example.progettoprog3client.MailApplication;
import org.example.progettoprog3client.utils.ClientHandler;
import org.example.progettoprog3client.models.User;
import java.io.IOException;

public class LoginController {

    @FXML
    private Label errorMessage;

    public static final String EMAIL_PATTERN = "^[\\w-\\.]+@[\\w-]+\\.[a-zA-Z]{2,7}$";

    @FXML
    private TextField loginInsert;

    public String userMail;

    ClientHandler handy = new ClientHandler();

    @FXML
    public void onLoginClick(ActionEvent actionEvent) {
        userMail = loginInsert.getText();
        if (userMail.matches(EMAIL_PATTERN)) {
            switch (handy.loginCheck(userMail)) {
                case SUCCESS:
                    errorMessage.setVisible(false);
                    switchToHome(actionEvent);
                    break;
                case FAILURE:
                    loginInsert.setStyle("-fx-border-color: red");
                    errorMessage.setText("Email non trovata. Riprova");
                    errorMessage.setVisible(true);
                    break;
                case NULL:
                    Alert nullAlert = new Alert(Alert.AlertType.ERROR);
                    nullAlert.setTitle("Errore");
                    nullAlert.setHeaderText("ERROR OCCURED");
                    nullAlert.setContentText("Ricezione valore nullo");
                    nullAlert.showAndWait();
                    break;
                case ALREADY_LOGGED:
                    loginInsert.setStyle("-fx-border-color: red");
                    errorMessage.setText("Email già attiva in questo momento");
                    errorMessage.setVisible(true);
                    break;
                case Error:
                    Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                    errorAlert.setTitle("Errore");
                    errorAlert.setHeaderText("ERROR OCCURED");
                    errorAlert.setContentText("Server offline");
                    errorAlert.showAndWait();
            }
        } else {
            loginInsert.setStyle("-fx-border-color: red");
            errorMessage.setText("Email pattern non valido. Riprova");
            errorMessage.setVisible(true);
        }
    }


    private void switchToHome(ActionEvent actionEvent) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(MailApplication.class.getResource("home-view.fxml"));
            Parent homeRoot = fxmlLoader.load();
            HomeController homeController  = fxmlLoader.getController();

            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();

            Scene homeScene = new Scene(homeRoot);

            stage.setScene(homeScene);

            stage.setMaximized(true);

            stage.setTitle("Home - MailUniTO - " + User.getUserMail());

            stage.setOnCloseRequest(event -> homeController.stopUpdateThread());
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Errore");
            alert.setHeaderText("Impossibile caricare la vista");
            alert.setContentText("Si è verificato un errore durante il caricamento della home.");
            alert.showAndWait();
        }
    }
}
