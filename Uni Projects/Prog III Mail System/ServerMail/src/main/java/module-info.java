module org.example.progettoprog3server {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires org.json;
    requires java.desktop;


    opens org.example.progettoprog3server to javafx.fxml;
    exports org.example.progettoprog3server;
    exports org.example.progettoprog3server.controllers;
    opens org.example.progettoprog3server.controllers to javafx.fxml;
    exports org.example.progettoprog3server.model;
    opens org.example.progettoprog3server.model to javafx.fxml;
}