module org.example.progettoprog3 {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires java.sql;
    requires org.json;
    requires jdk.dynalink;


    opens org.example.progettoprog3client to javafx.fxml;
    exports org.example.progettoprog3client;
    exports org.example.progettoprog3client.controllers;
    opens org.example.progettoprog3client.controllers to javafx.fxml;
    exports org.example.progettoprog3client.utils;
    opens org.example.progettoprog3client.utils to javafx.fxml;
    exports org.example.progettoprog3client.models;
    opens org.example.progettoprog3client.models to javafx.fxml;
}