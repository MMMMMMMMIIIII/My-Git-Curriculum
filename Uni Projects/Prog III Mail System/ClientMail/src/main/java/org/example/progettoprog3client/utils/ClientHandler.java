package org.example.progettoprog3client.utils;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.example.progettoprog3client.models.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

import static org.example.progettoprog3client.utils.CommunicationStatus.FAILURE;
import static org.example.progettoprog3client.utils.CommunicationStatus.SUCCESS;

public class ClientHandler {


    ExecutorService threadPool = Executors.newSingleThreadExecutor();

    public CommunicationStatus loginCheck(String emailInsert) {
        try (Socket socket = new Socket(SessionManager.getADDRESS(), SessionManager.getPORT());
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

            Converter conv = new Converter(Operation.LOGIN.name(), emailInsert);
            out.println(conv);

            String response = in.readLine();
            if (response == null) {
                return CommunicationStatus.NULL;
            }
            conv = new Converter(response);

            switch (conv.getStatus()){
                case SUCCESS:
                    ObservableList<Mail> emailProv = FXCollections.observableArrayList();
                    emailProv.addAll(parseEmailList(conv));
                    SessionManager.setUser(new User(emailInsert, emailProv));
                    return SUCCESS;
                case FAILURE:
                    return FAILURE;
                case ALREADY_LOGGED:
                    return CommunicationStatus.ALREADY_LOGGED;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return CommunicationStatus.Error;
    }

    public CommunicationStatus sendEmail(Collection<String> to, String subject, String body){
        try(Socket socket = new Socket(SessionManager.getADDRESS(), SessionManager.getPORT());
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true)){

            Converter conv = new Converter(OverlayModel.getOp().name(), User.getUserMail(), to, subject, body);
            out.println(conv);

            String response = in.readLine();

            if (response == null) {
                return CommunicationStatus.NULL;
            }
            conv = new Converter(response);

            switch (conv.getStatus()){
                case SUCCESS:
                    return SUCCESS;
                case FAILURE:
                    return FAILURE;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return CommunicationStatus.Error;
    }


    List<Mail> newEmails;

    public List<Mail> refresh(HomeModel homeModel) {
        threadPool.execute(() -> {
            try (Socket socket = new Socket(SessionManager.getADDRESS(), SessionManager.getPORT());
                 BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                 PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

                out.println(new Converter(Operation.UPDATE.name(), User.getUserMail()));
                homeModel.setIsActive(true);
                String response = in.readLine();
                if (response != null) {
                    Converter conv = new Converter(response);

                    if (conv.getStatus() == SUCCESS) {
                        newEmails = parseEmailList(conv);
                    }else{
                        newEmails = null;
                    }
                }
            } catch (IOException e) {
                homeModel.setIsActive(false);
            }
        });
        return newEmails;
    }


    public CommunicationStatus logout(String email){
        try (Socket socket = new Socket(SessionManager.getADDRESS(), SessionManager.getPORT());
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

            out.println(new Converter(Operation.LOGOUT.name(), email));

            String response = in.readLine();
            if(response == null){
                return CommunicationStatus.NULL;
            }
            Converter conv = new Converter(response);
            switch (conv.getStatus()){
                case SUCCESS:
                    return SUCCESS;
                case FAILURE:
                    return FAILURE;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return CommunicationStatus.Error;
    }

    public CommunicationStatus deleteManager(Converter conv) {
        try(Socket socket = new Socket(SessionManager.getADDRESS(), SessionManager.getPORT());
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true)){

            out.println(conv.toString());

            String response = in.readLine();

            if (response == null) {
                return CommunicationStatus.NULL;
            }
            conv = new Converter(response);

            switch (conv.getStatus()){
                case SUCCESS:
                    return SUCCESS;
                case FAILURE:
                    return FAILURE;
            }
        } catch (IOException e) {
            System.out.println("Non è stato possibile eliminare la mail.");
        }
        return CommunicationStatus.Error;
    }

    public void updateRead (Converter conv){
        try(Socket socket = new Socket(SessionManager.getADDRESS(), SessionManager.getPORT());
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

            out.println(conv.toString());

            String response = in.readLine();
            if (response == null) {
                return;
            }
            conv = new Converter(response);
            conv.getStatus();
        }catch (IOException e){
            System.out.println("Non è stato possibile passare il visualizzato per la seguente mail lato server.");
        }
    }

        private List<Mail> parseEmailList(Converter conv){
        LinkedList<Mail> emailList = new LinkedList<>();

        try {
            JSONArray jsonArray = conv.getJSONArray();

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonMail = jsonArray.getJSONObject(i);

                Mail mail = new Mail(
                        jsonMail.getInt("id"),
                        jsonMail.getString("from"),
                        jsonMail.getJSONArray("to").toList().stream().map(Object::toString).toList(),
                        jsonMail.getString("oggetto"),
                        jsonMail.getString("contenuto"),
                        jsonMail.getString("dataOra"),
                        jsonMail.getBoolean("isRead")
                );

                emailList.addFirst(mail);            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return emailList;
    }
}
