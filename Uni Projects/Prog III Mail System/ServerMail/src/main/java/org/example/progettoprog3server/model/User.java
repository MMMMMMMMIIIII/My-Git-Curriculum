package org.example.progettoprog3server.model;

import org.example.progettoprog3server.Utils.CommunicationStatus;
import org.example.progettoprog3server.Utils.LogManager;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class User {
    private final String userEmail;
    private Collection<Mail> emails;
    private int emailId;
    private boolean active;
    private final Path path;

    public User(String userEmail, Path path) {
        this.userEmail = userEmail;
        this.active = false;
        this.path = path;

        try{
            if (Files.notExists(path)) {
                Files.createFile(path);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        emails = LogManager.readToFile(path);

        if(emails != null && !emails.isEmpty()) {
            Mail prov = ((List<Mail>)emails).get(emails.size()-1);
            emailId = prov.getId() + 1;
        }
        else {
            emailId = 0;
            emails = new ArrayList<>();
        }
    }

    public Path getPath() {
        return path;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean getActive() {
        return active;
    }

    public String getUserEmail(){
        return userEmail;
    }

    public Collection<Mail> getEmails(){
        return emails;
    }

    // aggiunge una mail, sia in "locale" che sul suo file
    public void addMail(String from, Collection<String> to, String oggetto, String contenuto, String dataOra){
        Mail mail = new Mail(emailId, from, to, oggetto, contenuto, dataOra);
        System.out.println(userEmail + ":\n" + emailId + "\n" + from + "\n" + to + "\n" + oggetto + "\n" + contenuto + "\n" + dataOra);
        emailId ++;
        Converter conv = new Converter(mail);
        LogManager.writeToFile(conv.toString(), path);
    }

    // cancella una mail, sia in "locale" che sul suo file
    public CommunicationStatus deleteEmail(int id){
        emails.removeIf(mail -> mail.getId() == id);
        if(LogManager.deleteFromFile(path, id)) return CommunicationStatus.SUCCESS;
        return CommunicationStatus.FAILURE;
    }

    public Mail selectEmail(int id){
        for(Mail mail : emails){
            if(mail.getId() == id){
                return mail;
            }
        }
        return null;
    }
}
