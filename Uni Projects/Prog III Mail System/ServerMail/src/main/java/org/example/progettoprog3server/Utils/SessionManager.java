package org.example.progettoprog3server.Utils;

import org.example.progettoprog3server.model.User;

import java.net.ServerSocket;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.HashSet;
import java.util.concurrent.ExecutorService;

public class SessionManager {
    // creo i 3 utenti che possono accedere al sistema, e i loro file (se non già esistenti)
    public static final Collection<User> emailsAddr = new HashSet<>(){{
        add(new User("ciao@ciao.ciao", Paths.get(System.getProperty("user.home"), "ciao.txt")));
        add(new User ("hello@world.com", Paths.get(System.getProperty("user.home"), "hello.txt")));
        add(new User("mirco.illengo@gmail.com", Paths.get(System.getProperty("user.home"), "mirco.txt")));
    }};


    public static final int PORT = 9122; // porta in ascolto
    public static final int THREAD_POOL_SIZE = 3; // thread pool size
    public static ExecutorService threadPool;
    public static ServerSocket serverMail;
}
