package ru.nsu.dskvl.netapp;

import java.io.*;
import java.net.*;
import java.util.*;

public class Server implements Runnable {
    private final Map<Socket, PrintWriter> clients = new HashMap<>();

    private PrintWriter log;

    public Server(File logFile) {
        try {
            log = new PrintWriter(logFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(1844)) {
            while (true) {
                Socket client = serverSocket.accept();
                synchronized (clients) {
                    clients.put(client, new PrintWriter(client.getOutputStream()));
                }
                Thread thread = new Thread(new MessageProcessor(client, this));
                thread.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized void messageRecieved(String message) {
        log.println(message);
        log.flush();

        synchronized (clients) {
            for (var key : clients.keySet()) {
                clients.get(key).println(message);
                clients.get(key).flush();
            }
        }
    }

    public synchronized void clientQuit(Socket client) {
        try {
            client.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        synchronized (clients) {
            clients.get(client).close();
            clients.remove(client);
        }
    }

    public static void main(String[] args) {
        File log = new File("log");
        Server s = new Server(log);
        s.run();
    }
}

class MessageProcessor implements Runnable {
    private final Socket socket;
    private final Server server;
    public MessageProcessor(Socket socket, Server server) {
        this.socket = socket;
        this.server = server;
    }

    @Override
    public void run() {
        try (Scanner sc = new Scanner(socket.getInputStream())) {
            while (true) {
                String message = sc.nextLine();
                if (message.equals("q")) {
                    server.clientQuit(socket);
                    break;
                }
                server.messageRecieved(message);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoSuchElementException e) {
            server.clientQuit(socket);
        }
    }
}