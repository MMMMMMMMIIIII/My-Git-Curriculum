package org.example.progettoprog3server.controllers;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import org.example.progettoprog3server.model.ServerModel;


public class ServerController {
    @FXML
    private TextArea logArea;

    @FXML
    private Button startStop;


    @FXML
    public void initialize() {
        startStop.setOnAction(this::stateServer); // setto l'azione del bottone (richiama stateServer)
    }

    // stampa graficamente il cambiamento di stato e cambia la property del model
    private void stateServer(ActionEvent actionEvent) {
        if (ServerModel.isServerRunning()){
            startStop.setText("Start");
            startStop.setStyle("-fx-font-weight: bold; -fx-font-size: 16px; -fx-background-color: #33ff33; -fx-padding: 10; -fx-cursor: hand;");
        }
        else{
            startStop.setText("Stop");
            startStop.setStyle("-fx-font-weight: bold; -fx-font-size: 16px; -fx-background-color: #ff3333; -fx-padding: 10; -fx-cursor: hand;");
        }
        ServerModel.setServerRunning(!ServerModel.isServerRunning());
    }

    public void stopUpdateThread(){
        Platform.exit();
        System.exit(0);
    }

    // stampa il log sulla finestra del server
    public void addLog(String feed) {
            double pos = logArea.getScrollTop();
            int anchor = logArea.getAnchor();
            int caret = logArea.getCaretPosition();

            logArea.appendText(feed + '\n');

            logArea.setScrollTop(pos);
            logArea.selectRange(anchor, caret);
    }


}
