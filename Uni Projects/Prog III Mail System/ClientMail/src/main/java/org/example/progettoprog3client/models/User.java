package org.example.progettoprog3client.models;

import javafx.collections.ObservableList;


public class User{
    private static String userMail;
    private static ObservableList<Mail> emails;


    public User(String userMail, ObservableList<Mail> emails) {
        User.userMail = userMail;
        User.emails = emails;
    }

    public static String getUserMail(){
        return userMail;
    }

    public static ObservableList<Mail> getEmails(){
        return emails;
    }
}
