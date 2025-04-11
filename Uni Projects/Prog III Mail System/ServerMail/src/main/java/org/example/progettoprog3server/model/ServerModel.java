package org.example.progettoprog3server.model;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

import java.nio.file.Path;
import java.nio.file.Paths;

public class ServerModel {
    private static final BooleanProperty serverRunning = new SimpleBooleanProperty(false); // property "serverRunning", utilizzata per startare/stoppare il server
    private static final Path LOG_FILE = Paths.get(System.getProperty("user.home"), "log_file.txt");

    public static Path getLOG_FILE(){
        return LOG_FILE;
    }

    public static BooleanProperty getServerRunningProperty(){
        return serverRunning;
    }

    public static boolean isServerRunning() {
        return serverRunning.get();
    }

    public static void setServerRunning(boolean sr){
        serverRunning.set(sr);
    }
}
