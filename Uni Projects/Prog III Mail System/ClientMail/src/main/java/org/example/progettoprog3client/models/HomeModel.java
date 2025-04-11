package org.example.progettoprog3client.models;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class HomeModel {
    private final ObservableList<Mail> emails;
    private final BooleanProperty isActive = new SimpleBooleanProperty(false);  // Valore iniziale

    public HomeModel() {
        this.emails = FXCollections.observableArrayList(User.getEmails());
    }

    public ObservableList<Mail> getEmails() {
        return emails;
    }

    public void removeEmail(Mail email) {
        emails.remove(email);
    }


    public boolean getIsActive() {
        return isActive.get();
    }

    public void setIsActive(boolean value) {
        isActive.set(value);
    }

    public BooleanProperty isActiveProperty() {
        return isActive;
    }
}
