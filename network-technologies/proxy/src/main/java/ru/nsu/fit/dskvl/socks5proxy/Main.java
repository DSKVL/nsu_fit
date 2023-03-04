package ru.nsu.fit.dskvl.socks5proxy;

import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Specify port.");
            return;
        }

        var port = Integer.parseInt(args[0]);

        try {
            var proxy = new Proxy(port);
            proxy.start();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}