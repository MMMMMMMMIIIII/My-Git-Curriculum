package org.example.progettoprog3server.Utils;

import org.example.progettoprog3server.controllers.ServerController;
import org.example.progettoprog3server.model.Converter;
import org.example.progettoprog3server.model.Mail;
import org.example.progettoprog3server.model.User;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;

public class ClientHandler {

    private static ServerController homeController;


    public static synchronized void handleClient(Socket clientSocket, ServerController homeController) throws IOException {
        ClientHandler.homeController = homeController;
        BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);

        String clientMessage = in.readLine();
        LogManager.log(clientMessage, homeController);
        Converter conv = new Converter(clientMessage);

        switch (conv.getOperation()){
            case LOGIN:
                conv = loginManager(conv.getEmail());
                LogManager.log(String.valueOf(conv), homeController);
                out.println(conv);
                break;
            case MAIL, REPLY, REPLY_ALL, FORWARD:
                conv = new Converter(mailManager(conv));
                LogManager.log(String.valueOf(conv), homeController);
                out.println(conv);
                break;
            case UPDATE:
                conv = updateManager(conv);
                LogManager.log(String.valueOf(conv), homeController);
                out.println(conv);
                break;
            case DELETE:
                conv = new Converter(deleteManager(conv));
                LogManager.log(String.valueOf(conv), homeController);
                out.println(conv);
                break;
            case LOGOUT:
                conv = new Converter(logoutManager(conv));
                LogManager.log(String.valueOf(conv), homeController);
                out.println(conv);
                break;
            case READ:
                conv = new Converter(readManager(conv));
                LogManager.log(String.valueOf(conv), homeController);
                out.println(conv);
                break;
        }
    }

    private static Converter loginManager(String email) {
        // Filtra l'utente che si vuole loggare dalla lista di utenti presenti
        User curUser = SessionManager.emailsAddr.stream().filter(
                user -> user.getUserEmail().equals(email)
        ).findAny().orElse(null);

        if(curUser != null) {
            if (!curUser.getActive()) { // l'utente non deve essere già loggato
                curUser.setActive(true);
                return new Converter(curUser.getEmails(), CommunicationStatus.SUCCESS);
            }
            return new Converter(CommunicationStatus.ALREADY_LOGGED);
        }
        return new Converter(CommunicationStatus.FAILURE);
    }

    private static CommunicationStatus logoutManager(Converter conv) {
        User curUser = SessionManager.emailsAddr.stream().filter(
                user -> user.getUserEmail().equals(conv.getEmail())
        ).findAny().orElse(null);

        if(curUser != null) {
            curUser.setActive(false); // setta l'utente come offline
            return CommunicationStatus.SUCCESS;
        }

        return CommunicationStatus.FAILURE;
    }

    // metodo per formattare il dateTime a nostro piacimento ed averlo sotto forma di stringa
    private static String dateToString (){
        LocalDateTime date = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return date.format(formatter);
    }

    private static CommunicationStatus mailManager(Converter conv) {
        Collection<User> tmp = new ArrayList<>();
        for (String to : conv.getTo()) { // dai destinatari indicati ne ottengo tutti gli user. Se anche solo uno di questi non esiste, non invio la mail
            User prov = SessionManager.emailsAddr.stream().filter(
                    user -> user.getUserEmail().equals(to.trim())
            ).findAny().orElse(null);
            if (prov == null) {
                LogManager.log("Email non esistente: " + to, homeController);
                return CommunicationStatus.FAILURE;
            }
            tmp.add(prov);
        }
        for (User prov : tmp){
            prov.addMail(conv.getFrom(), conv.getTo(), conv.getOggetto(), conv.getContenuto(), dateToString());
        }
        return CommunicationStatus.SUCCESS;
    }

    private static CommunicationStatus deleteManager(Converter conv){
        String email = conv.getEmail();
        int id = conv.getId();
        User prov = SessionManager.emailsAddr.stream().filter(
                user -> user.getUserEmail().equals(email)
        ).findAny().orElse(null);
        return prov.deleteEmail(id);
    }

    private static CommunicationStatus readManager(Converter conv){
        String email = conv.getEmail();
        int id = conv.getId();
        User curUser = SessionManager.emailsAddr.stream().filter(
                user -> user.getUserEmail().equals(email)
        ).findAny().orElse(null);

        if (curUser != null){
            Mail tmp = curUser.selectEmail(id);
            tmp.setIsRead();
            if(LogManager.setMailRead(curUser.getPath(), curUser.getEmails())) // metto il visualizzato nella mail salvata sul file
                return CommunicationStatus.SUCCESS;
        }
        return CommunicationStatus.FAILURE;
    }

    private static Converter updateManager(Converter conv){
        String email = conv.getEmail();

        User curUser = SessionManager.emailsAddr.stream().filter(
                user -> user.getUserEmail().equals(email)
        ).findAny().orElse(null);
        if(curUser != null) {
            Collection<Mail> userMails = curUser.getEmails();

            Collection<Mail> updateMails = LogManager.readToFile(curUser.getPath());

            if(updateMails != null){
                updateMails.removeAll(userMails); // faccio "la differenza" tra la lista del server e la lista salvata sul file
                userMails.addAll(updateMails); // aggiungo le mail nuove alle mail vecchie

                Converter responseConverter;
                if (!updateMails.isEmpty()) responseConverter = new Converter(updateMails, CommunicationStatus.SUCCESS);
                else responseConverter = new Converter(CommunicationStatus.FAILURE);

                return responseConverter;
            }
        }
        return null;
    }
}
