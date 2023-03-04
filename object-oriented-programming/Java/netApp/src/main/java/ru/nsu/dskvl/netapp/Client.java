package ru.nsu.dskvl.netapp;

import java.io.*;
import java.net.*;
import java.util.Scanner;
public class Client implements Runnable {
    private String name;

    public void setName(String name) {
        this.name = name;
    }

    public static void main(String[] args) {
        Client cl = new Client();
        cl.run();
    }

    @Override
    public void run() {
        Socket socket = new Socket();
        SocketAddress address = new InetSocketAddress("127.0.0.1", 1844);
        try {
            socket.connect(address);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Thread recvThread = new Thread(new serverReciever(socket));
        recvThread.start();

        try(Scanner sc = new Scanner(System.in)) {
            setName(sc.nextLine());
            try (PrintWriter wr = new PrintWriter(socket.getOutputStream())) {
                while (true) {
                    String input = sc.nextLine();

                    StringBuilder message = new StringBuilder(name);
                    message.append(": ").append(input);
                    wr.println(message);
                    wr.flush();

                    if (input.equals("q")) {
                        recvThread.interrupt();
                        break;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

class serverReciever implements Runnable {
    Socket server;

    serverReciever(Socket socket) {
        this.server = socket;
    }

    @Override
    public void run() {
        try (Scanner sc = new Scanner(server.getInputStream())) {
            while (true) {
                if (Thread.currentThread().isInterrupted()) {
                    break;
                }
                String message = sc.nextLine();
                System.out.println(message);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}