package org.example.progettoprog3client.utils;

import org.example.progettoprog3client.models.User;

public class SessionManager {

    private static final int PORT = 9122; // porta del server
    private  static final String ADDRESS = "localhost"; // indirizzo del server

    private static User currentUser;

    public static void setUser(User user) {
        currentUser = user;
    }


    public static int getPORT(){
        return PORT;
    }

    public static String getADDRESS(){
        return ADDRESS;
    }

}
