package org.example.progettoprog3client.controllers;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import org.example.progettoprog3client.MailApplication;
import org.example.progettoprog3client.models.*;
import org.example.progettoprog3client.utils.ClientHandler;
import org.example.progettoprog3client.utils.CommunicationStatus;
import org.example.progettoprog3client.utils.Operation;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class HomeController {

    public Button replyButton;
    public Button replyAllButton;
    public Button forwardButton;
    private HomeModel homeModel;

    @FXML
    private ListView<Mail> mailList;
    @FXML
    private Button deleteMail;
    @FXML
    private Label mittenteLabel, destinatariLabel, oggettoLabel;
    @FXML
    private TextFlow contenutoFlow;
    @FXML
    private Circle statusCircle;
    @FXML
    private VBox mailContent;

    private ScheduledExecutorService scheduler;


    private ClientHandler handy;


    @FXML
    public void initialize() {

        homeModel = new HomeModel();

        // Collega la lista di email al ListView
        mailList.setItems(homeModel.getEmails());

        List<Mail> mailLette = new ArrayList<>();

        mailList.setCellFactory(mail -> new ListCell<>() {
            @Override
            protected void updateItem(Mail item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setGraphic(null);
                    setText(null);
                    setStyle("");
                } else {
                    VBox contentBox = new VBox();
                    contentBox.setSpacing(5);
                    contentBox.setStyle("-fx-padding: 10;");

                    Label subjectLabel = new Label(item.getOggetto());
                    subjectLabel.setWrapText(false);
                    subjectLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #2a6594;");
                    subjectLabel.setMaxWidth(mailList.getWidth() - 40);


                    Label previewLabel = new Label(item.getContenuto().replaceAll("\n", " "));
                    previewLabel.setWrapText(false);
                    previewLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #555;");
                    previewLabel.setMaxWidth(mailList.getWidth() - 40);
                    previewLabel.setEllipsisString("..."); // Mostra i "..." se il testo è troppo lungo
                    previewLabel.setTextOverrun(OverrunStyle.ELLIPSIS);

                    mailList.widthProperty().addListener((obs, oldWidth, newWidth) -> {
                        previewLabel.setMaxWidth(newWidth.doubleValue() - 40);
                        subjectLabel.setMaxWidth(newWidth.doubleValue() - 40);
                    });

                    contentBox.getChildren().addAll(subjectLabel, previewLabel);
                    setGraphic(contentBox);

                    Mail mail = mailList.getSelectionModel().getSelectedItem();

                    if (mail != null && mail.equals(item)) {
                        setStyle("-fx-background-color: #d0e7ff;"); // Mail selezionata
                        if (!item.getIsRead()) {
                            mailList.getSelectionModel().getSelectedItem().setIsRead(true);
                            Converter conv = new Converter(User.getUserMail(), mailList.getSelectionModel().getSelectedItem().getId(), Operation.READ);
                            handy.updateRead(conv);
                        }
                    } else if (item.getIsRead()) {
                        setStyle("-fx-background-color: #ffffff;"); // Mail già letta
                    } else {
                        setStyle("-fx-background-color: #d5cdfc;"); // Mail non letta
                    }

                    mailList.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
                        if (newSelection != null && newSelection.equals(item) && !mailLette.contains(item)) {
                            mailLette.add(item);
                            mailList.refresh();
                        }
                    });
                }
            }
        });






        // Listener per selezione della mail
        mailList.getSelectionModel().selectedItemProperty().addListener((obs, oldMail, selectedMail) -> {
            if (selectedMail != null) {
                Mail mailCorrente = homeModel.getEmails().stream()
                        .filter(mail -> mail.getId() == selectedMail.getId())
                        .findFirst()
                        .orElse(null);

                if (mailCorrente != null) {

                    mittenteLabel.setText(mailCorrente.getMittente());
                    destinatariLabel.setText(String.join(", ", mailCorrente.getDestinatario()));
                    oggettoLabel.setText(mailCorrente.getOggetto());


                    contenutoFlow.getChildren().clear();
                    contenutoFlow.getChildren().add(new Text(mailCorrente.getContenuto()));


                    replyButton.setOnAction(e -> handleReply(mailCorrente));
                    replyAllButton.setOnAction(e -> handleReplyAll(mailCorrente));
                    forwardButton.setOnAction(e -> handleForward(mailCorrente));


                    mailContent.setVisible(true);
                    deleteMail.setVisible(true);
                }
                else{
                    mailContent.setVisible(false);
                    deleteMail.setVisible(false);
                }
            }
        });


        homeModel.isActiveProperty().addListener((observable, oldValue, newValue) -> updateStatus(newValue));

        // Imposta il valore iniziale del pallino
        updateStatus(homeModel.getIsActive());

        deleteMail.setOnAction(this::deleteSelectedEmail);

        handy = new ClientHandler();

        scheduler = Executors.newScheduledThreadPool(1);

        Runnable task = () -> {
            List<Mail> responseList = handy.refresh(homeModel);
            if (responseList != null) {
                Platform.runLater(() -> {
                    ObservableList<Mail> currentItems = mailList.getItems();

                    List<Mail> newMails = responseList.stream().toList();

                    boolean tmp = false;
                    for (Mail newMail : newMails) {
                        boolean alreadyExists = currentItems.stream().anyMatch(existingMail -> existingMail.equals(newMail));
                        if (!alreadyExists) {
                            tmp = true;
                            currentItems.add(0, newMail);
                        }
                    }

                    if (tmp) playNotificationSound();
                    mailList.refresh();
                });
            }
        };

        scheduler.scheduleAtFixedRate(task, 0, 1, TimeUnit.SECONDS);
    }

    // Metodo per gestire la risposta a una mail
    private void handleReply(Mail mail) {
        OverlayModel.setOp(Operation.REPLY);
        openComposeWindow(mail, false, false);
    }

    // Metodo per gestire la risposta a tutti
    private void handleReplyAll(Mail mail) {
        OverlayModel.setOp(Operation.REPLY_ALL);
        openComposeWindow(mail, true, false);
    }

    // Metodo per inoltrare la mail
    private void handleForward(Mail mail) {
        OverlayModel.setOp(Operation.FORWARD);
        openComposeWindow(mail, false, true);
    }

    // Metodo per aprire la finestra di composizione della mail
    private void openComposeWindow(Mail mail, boolean replyAll, boolean forward) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(MailApplication.class.getResource("overlay-view.fxml"));
            Parent overlayRoot = fxmlLoader.load();
            OverlayController overlayController = fxmlLoader.getController();

            // Configura i dati per il reply o forward
            overlayController.prefillEmail(mail, replyAll, forward);

            Stage modalStage = new Stage();
            modalStage.setTitle(forward ? "Forward Email" : "Reply Email");
            modalStage.setScene(new Scene(overlayRoot));
            modalStage.setMaximized(true);
            modalStage.setMinWidth(600);
            modalStage.setMinHeight(Screen.getPrimary().getVisualBounds().getHeight());
            modalStage.initModality(Modality.APPLICATION_MODAL);
            modalStage.initOwner(mailList.getScene().getWindow());

            overlayController.setHomeController(this);

            modalStage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void updateStatus(boolean isActive) {
        if (isActive) {
            statusCircle.setFill(Color.GREEN);
        } else {
            statusCircle.setFill(Color.RED);
        }
    }


    public void playNotificationSound() {
        try {
            URL soundURL = getClass().getResource("/org/example/progettoprog3client/sounds/notify.wav");
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(soundURL);
            Clip clip = AudioSystem.getClip();
            clip.open(audioStream);
            clip.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    public void stopUpdateThread(){
        if(homeModel.getIsActive()){
            CommunicationStatus tmp;
            do {
                tmp = handy.logout(User.getUserMail());
            }while(tmp != CommunicationStatus.SUCCESS);
        }
        scheduler.shutdown();
        Platform.exit();
        System.exit(0);
   }




    private void deleteSelectedEmail(ActionEvent e) {
        Mail selectedMail = mailList.getSelectionModel().getSelectedItem();
        if (selectedMail != null) {
            Converter conv = new Converter(User.getUserMail(), selectedMail.getId(), Operation.DELETE);
            switch (handy.deleteManager(conv)){
                case SUCCESS:
                    mailList.getSelectionModel().clearSelection();
                    homeModel.removeEmail(selectedMail);
                    mailContent.setVisible(false);
                    deleteMail.setVisible(false);
                    Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                    successAlert.setTitle("Fatto!");
                    successAlert.setHeaderText("Email eliminata");
                    successAlert.setContentText("L'email è stata eliminata correttamente!");
                    successAlert.showAndWait();
                    break;
                case FAILURE:
                    Alert failureAlert = new Alert(Alert.AlertType.ERROR);
                    failureAlert.setTitle("Fallimento");
                    failureAlert.setHeaderText("Email non trovata");
                    failureAlert.setContentText("L'email non esiste e non può essere cancellata");
                    failureAlert.showAndWait();
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
                    errorAlert.setContentText("Il server è offline");
                    errorAlert.showAndWait();
            }
        }
    }


    public void showOverlay(ActionEvent event) {
        try {
            OverlayModel.setOp(Operation.MAIL);
            FXMLLoader fxmlLoader = new FXMLLoader(MailApplication.class.getResource("overlay-view.fxml"));
            Parent overlayRoot = fxmlLoader.load();
            OverlayController overlayController = fxmlLoader.getController();

            Stage modalStage = new Stage();
            modalStage.setTitle("Nuova mail");

            Scene scene = new Scene(overlayRoot);
            modalStage.setScene(scene);
            modalStage.setMaximized(true);

            modalStage.setMinWidth(600);
            modalStage.setMinHeight(Screen.getPrimary().getVisualBounds().getHeight());

            modalStage.initModality(Modality.APPLICATION_MODAL);
            modalStage.initOwner(((Node) event.getSource()).getScene().getWindow());


            overlayController.setHomeController(this);

            modalStage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}