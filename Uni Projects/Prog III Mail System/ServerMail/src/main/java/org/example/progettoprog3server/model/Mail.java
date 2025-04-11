package org.example.progettoprog3server.model;

import java.util.Collection;
import java.util.Objects;

public class Mail {

    private int id;
    private String mittente;
    private Collection<String> destinatario;
    private String oggetto;
    private String contenuto;
    private String dateAndTime;
    private boolean isRead;

    // Costruttore
    public Mail(int id, String mittente, Collection<String> destinatario, String oggetto, String contenuto, String dateAndTime) {
        this.id = id;
        this.mittente = mittente;
        this.destinatario = destinatario;
        this.oggetto = oggetto;
        this.contenuto = contenuto;
        this.dateAndTime = dateAndTime;
        isRead = false;
    }

    public Mail() {
        this.mittente = null;
        this.destinatario = null;
        this.oggetto = null;
        this.contenuto = null;
        this.dateAndTime = null;
        isRead = false;
    }

    public void setId(int id){
        this.id = id;
    }

    public int getId(){
        return id;
    }

    public String getMittente() {
        return mittente;
    }

   public void setMittente(String mittente) {
        this.mittente = mittente;
    }

    public Collection<String> getDestinatario() {
        return destinatario;
    }

    public void setDestinatario(Collection<String> destinatario) {
        this.destinatario = destinatario;
    }

    public String getOggetto() {
        return oggetto;
    }

    public void setIsRead(){ isRead = true; }

    public void setIsRead(boolean value){ isRead = value; }

    public boolean getIsRead(){ return isRead; }

    public void setOggetto(String oggetto) {
        if (oggetto != null && !oggetto.isBlank()) {
            this.oggetto = oggetto;
        }
    }

    public String getContenuto() {
        return contenuto;
    }

    public void setContenuto(String contenuto) {
        if (contenuto != null && !contenuto.isBlank()) {
            this.contenuto = contenuto;
        }
    }

    public void setDateAndTime(String dateAndTime){
        this.dateAndTime = dateAndTime;
    }

    public String getDateAndTime(){
        return dateAndTime;
    }

    @Override
    public String toString() {
        return "ID: "+ id + ", Mittente: " + mittente + ", Destinatari: " + destinatario + ", Oggetto: " + oggetto + ", Contenuto: " + contenuto + ", Data: " + dateAndTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Mail mail = (Mail) o;
        return id == mail.id &&
                Objects.equals(oggetto, mail.oggetto) &&
                Objects.equals(contenuto, mail.contenuto) &&
                Objects.equals(mittente, mail.mittente) &&
                Objects.equals(destinatario, mail.destinatario) &&
                Objects.equals(dateAndTime, mail.dateAndTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, oggetto, contenuto, mittente, destinatario, dateAndTime);
    }

}
