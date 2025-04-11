package org.example.progettoprog3server.Utils;

import javafx.application.Platform;
import org.example.progettoprog3server.controllers.ServerController;
import org.example.progettoprog3server.model.Converter;
import org.example.progettoprog3server.model.Mail;
import org.example.progettoprog3server.model.ServerModel;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.nio.file.Path;
import java.util.Collection;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class LogManager {
    private static final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    private static final ReentrantReadWriteLock.WriteLock writeLock = lock.writeLock();
    private static final ReentrantReadWriteLock.ReadLock readLock = lock.readLock();

    // stampo il log sia sull'interfaccia server, sia sul file di log
    public static void log(String message, ServerController serverController) {
        Platform.runLater(() -> serverController.addLog(message));
        writeToFile(message, ServerModel.getLOG_FILE());
    }

    // metodo generico per appendere su un file, gestito dal write lock
    public static void writeToFile(String message, Path path) {
        writeLock.lock();
        try {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(path.toFile(), true))) {
                writer.write(message);
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            writeLock.unlock();
        }
    }

    // metodo per leggere una lista di JSONObject da un file e creare di conseguenza un JSONArray
    public static Collection<Mail> readToFile(Path path) {
        readLock.lock();
        try {
            JSONArray array = new JSONArray();

            try (BufferedReader reader = new BufferedReader(new FileReader(path.toFile()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    JSONObject jsonObject = new JSONObject(line);
                    array.put(jsonObject);
                }
            }
            Converter conv = new Converter();
            return conv.getMails(array);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            readLock.unlock();
        }
        return null;
    }

    // per eliminare una mail, leggo tutto il file tramite JSONArray, elimino il JSONObject e riscrivo tutto sul file
    public static boolean deleteFromFile(Path path, int id){
        writeLock.lock();
        try {
            Collection<Mail> emails = readToFile(path);

            emails.removeIf(mail -> mail.getId() == id);

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(path.toFile()))) {
                for (Mail mail : emails) {
                    writer.write(new Converter(mail).toString());
                    writer.newLine();
                }
            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            writeLock.unlock();
        }
        return false;
    }

    // per settare il visualizzato, sovrascrivo tutto il file modificando la mail desiderata
    public static boolean setMailRead(Path path, Collection<Mail> emails){
        writeLock.lock();
        try {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(path.toFile()))) {
                for (Mail mail : emails) {
                    writer.write(new Converter(mail).toString());
                    writer.newLine();
                }
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            writeLock.unlock();
        }
        return false;
    }
}
