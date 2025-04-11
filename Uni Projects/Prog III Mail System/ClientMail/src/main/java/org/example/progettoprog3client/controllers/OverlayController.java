package org.example.progettoprog3client.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.stage.Stage;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import org.example.progettoprog3client.models.Mail;
import org.example.progettoprog3client.utils.ClientHandler;
import org.example.progettoprog3client.models.User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.example.progettoprog3client.controllers.LoginController.EMAIL_PATTERN;

public class OverlayController {

    @FXML
    private TextField toField;

    @FXML
    private TextField subjectField;

    @FXML
    private TextArea bodyField = new TextArea();

    HomeController homeController;

    public void setHomeController(HomeController homeController) {
        this.homeController = homeController;
    }


    @FXML
    public void onSendMailClick(ActionEvent event){
        Collection<String> to = new ArrayList<>();
        for (String email : List.of(toField.getText().split(","))){
            to.add(email.trim());
        }

        String subject = subjectField.getText();
        String body = bodyField.getText();

        if (to.isEmpty() || subject.isEmpty() || body.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Errore");
            alert.setHeaderText("Campi obbligatori mancanti");
            alert.setContentText("I campi 'A', 'Oggetto' e 'Testo' sono obbligatori.");
            alert.showAndWait();
        } else {
            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            ClientHandler handy = new ClientHandler();
            boolean req = true;
            for (String a : to)
                if (!a.matches(EMAIL_PATTERN)) {
                    req = false;
                    break;
                }

            if (req) {
                switch (handy.sendEmail(to, subject, body)) {
                    case SUCCESS:
                        stage.close();
                        break;
                    case FAILURE:
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Errore");
                        alert.setHeaderText("ERROR OCCURED");
                        alert.setContentText("Invio mail fallito: una o più mail 'destinatari' non trovate.");
                        alert.showAndWait();
                        break;
                    case NULL:
                        Alert nullAlert = new Alert(Alert.AlertType.ERROR);
                        nullAlert.setTitle("Errore");
                        nullAlert.setHeaderText("ERROR OCCURED");
                        nullAlert.setContentText("Ricezione valore nullo");
                        nullAlert.showAndWait();
                        break;
                    case Error:
                        Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                        errorAlert.setTitle("Errore");
                        errorAlert.setHeaderText("ERROR OCCURED");
                        errorAlert.setContentText("Server non connesso!");
                        errorAlert.showAndWait();
                }
            }else{
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Errore");
                alert.setHeaderText("ERROR OCCURED");
                alert.setContentText("Invio mail fallito: pattern 'destinatari' errato.");
                alert.showAndWait();
            }
        }




    }

    public void prefillEmail(Mail mail, boolean replyAll, boolean forward) {
        if (forward) {
            // Per l'inoltro, lascia il destinatario vuoto e formatta il messaggio
            toField.setText("");
            subjectField.setText("Fwd: " + mail.getOggetto());
            bodyField.setText("\n\n---------- Forwarded message ----------\n" +
                    "From: " + mail.getMittente() + "\n" +
                    "To: " + String.join(", ", mail.getDestinatario()) + "\n" +
                    "Subject: " + mail.getOggetto() + "\n\n" +
                    mail.getContenuto());
        } else {
            // Per rispondere, imposta il destinatario corretto
            if (replyAll) {
                // Rispondi a tutti, escludendo il mittente attuale
                List<String> recipients = new ArrayList<>(mail.getDestinatario());
                recipients.remove(User.getUserMail()); // Rimuove il proprio indirizzo se presente
                boolean tmp = false;
                for (String me : recipients)
                    if (mail.getMittente().equals(me)) {
                        tmp = true;
                        break;
                    }
                if (!tmp) recipients.add(0, mail.getMittente()); // Aggiunge il mittente all'inizio
                toField.setText(String.join(", ", recipients));
            } else {
                // Rispondi solo al mittente
                toField.setText(mail.getMittente());
            }

            subjectField.setText("Re: " + mail.getOggetto());
            bodyField.setText("\n\nOn " + mail.getDateAndTime() + ", " + mail.getMittente() + " wrote:\n" +
                    "---------------------------------\n" + mail.getContenuto());
        }
    }


    @FXML
    public void onCancelClick(ActionEvent event) {
        Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        stage.close();
    }
}
