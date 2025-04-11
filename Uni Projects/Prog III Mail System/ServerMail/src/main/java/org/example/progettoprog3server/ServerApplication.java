package org.example.progettoprog3server;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.example.progettoprog3server.Utils.ClientHandler;
import org.example.progettoprog3server.Utils.LogManager;
import org.example.progettoprog3server.Utils.SessionManager;
import org.example.progettoprog3server.controllers.ServerController;
import org.example.progettoprog3server.model.ServerModel;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executors;

public class ServerApplication extends Application {

    private static ServerController homeController;

    @Override
    public void start(Stage primaryStage) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/progettoprog3Server/home-view.fxml"));
            VBox root = loader.load();

            homeController = loader.getController();
            new Thread(ServerApplication::stateServer).start();

            // listener per il cambiamento di stato del server
            ServerModel.getServerRunningProperty().addListener((observable, oldValue, newValue) -> {
                if (!oldValue.equals(newValue)) {
                    stateServer();
                }
            });


            primaryStage.setTitle("Server - Log Eventi");
            primaryStage.setScene(new Scene(root, 600, 400));

            primaryStage.setResizable(false);
            primaryStage.setOnCloseRequest(event -> homeController.stopUpdateThread());  // stoppa il thread server in quando viene chiusa la finestra
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // starta - stoppa il server in base al cambiamento della property
    private static void stateServer() {
        if (!ServerModel.isServerRunning()) {
            ServerApplication.stopServer();
        } else {
            ServerApplication.startServer();
        }
    }

    public static void stopServer(){
        System.out.println("Stop Server: " + ServerModel.isServerRunning());
        if (!ServerModel.isServerRunning()) {
            try {
                if (SessionManager.serverMail != null) SessionManager.serverMail.close(); // chiude il socket
                if (SessionManager.threadPool != null) SessionManager.threadPool.shutdown(); // stoppa i thread "gestori" dei client
                LogManager.log("Server spento", homeController);
                SessionManager.emailsAddr.forEach(user -> user.setActive(false)); // tutti gli user non attivi
            } catch (IOException e) {
                LogManager.log("Errore durante l'arresto del server", homeController);
            }
        }
    }

    public static void startServer(){
        System.out.println("Start Server: " + ServerModel.isServerRunning());
        if (ServerModel.isServerRunning()){
            SessionManager.threadPool = Executors.newFixedThreadPool(SessionManager.THREAD_POOL_SIZE); // creo la thread pool di "gestori" dei client

            // creo il thread server
            new Thread(() -> {
                try {
                    SessionManager.serverMail = new ServerSocket(SessionManager.PORT);
                    LogManager.log("Server in ascolto sulla porta: " + SessionManager.PORT, homeController);

                    while (ServerModel.isServerRunning()) {
                        try {
                            Socket clientSocket = SessionManager.serverMail.accept();

                            // associo al nuovo utente uno dei thread della thread pool, chiamando handleclient
                            SessionManager.threadPool.execute(() -> {
                                try {
                                    ClientHandler.handleClient(clientSocket, homeController);
                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }
                            });
                        } catch (IOException e) {
                            if (ServerModel.isServerRunning()) LogManager.log("Errore connessione con il client", homeController);
                        }
                    }
                } catch (IOException e) {
                    LogManager.log("Errore creazione socket del server", homeController);
                }
            }).start();
        }
    }

    public static void main(String[] args) {
        launch();  // Avvia l'interfaccia grafica di JavaFX
    }
}