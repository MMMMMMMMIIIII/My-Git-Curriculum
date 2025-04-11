package org.example.progettoprog3client.models;

import java.util.Collection;
import java.util.Objects;

public class Mail {

    private final int id;
    private final String mittente;
    private final Collection<String> destinatario;
    private final String oggetto;
    private final String contenuto;
    private final String dateAndTime;
    private boolean isRead;

    public Mail(int id, Collection<String> destinatario, String oggetto, String contenuto, String dateAndTime) {
        this.id = id;
        this.mittente = User.getUserMail();
        this.destinatario = destinatario;
        this.oggetto = oggetto;
        this.contenuto = contenuto;
        this.dateAndTime = dateAndTime;
    }

    public Mail(int id, String mittente, Collection<String> destinatario, String oggetto, String contenuto, String dateAndTime, boolean isRead) {
        this.id = id;
        this.mittente = mittente;
        this.destinatario = destinatario;
        this.oggetto = oggetto;
        this.contenuto = contenuto;
        this.dateAndTime = dateAndTime;
        this.isRead = isRead;
    }

    public int getId() {
        return id;
    }

    public String getMittente() {
        return mittente;
    }

    public Collection<String> getDestinatario() {
        return destinatario;
    }

    public String getOggetto() {
        return oggetto;
    }

    public String getContenuto() {
        return contenuto;
    }

    public String getDateAndTime() {
        return dateAndTime;
    }

    public boolean getIsRead(){
        return isRead;
    }
    public void setIsRead(boolean isRead){
        this.isRead = isRead;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Mail mail = (Mail) o;
        return id == mail.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Mittente: " + mittente + ", Destinatari: " + destinatario + ", Oggetto: " + oggetto + ", Contenuto: " + contenuto + ", Data: " + dateAndTime;
    }
}
