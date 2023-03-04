package org.example;

import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class CopiesFinder {
    static class FellaPrinter {
        static void printFellas(Set<String> fellas) {
            System.out.flush();
            fellas.forEach(System.out::println);
        }
    }

    private final DatagramChannel recieveChannel;
    private final DatagramChannel sendChannel;
    private final InetSocketAddress socketAddress;
    int MULTICAST_PORT = 5239;
    private Timer timer;
    Map<String, Integer> fellasMap = new ConcurrentHashMap<>(4);

    public CopiesFinder(String ip) throws IOException, NoSuchElementException {
        NetworkInterface ni = NetworkInterface.getByName("wlo1");
        InetAddress address = InetAddress.getByName(ip);
        socketAddress = new InetSocketAddress(address, MULTICAST_PORT);

        recieveChannel = DatagramChannel.open()
                .setOption(StandardSocketOptions.SO_REUSEADDR, true)
                .bind(new InetSocketAddress(MULTICAST_PORT))
                .setOption(StandardSocketOptions.IP_MULTICAST_IF, ni);

        recieveChannel.join(address, ni);

        sendChannel = DatagramChannel.open().bind(null);

        var listener = this;
        new Thread(() -> {
            timer = new Timer(true);
            timer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    listener.multicast();
                    listener.tick();
                }
            }, 100, 1000);
        }).start();
    }

    private final static byte[] msg = "Looking for copies".getBytes(StandardCharsets.US_ASCII);
    private final static ByteBuffer buffer = ByteBuffer.wrap(msg);

    private void multicast() {
        try {
            sendChannel.send(buffer, socketAddress);
            buffer.rewind();
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    private void tick() {
        fellasMap.forEach((address, ticks) -> fellasMap.replace(address, ticks + 1));
        if (fellasMap.entrySet().removeIf(x -> x.getValue() > 5)) {
            FellaPrinter.printFellas(fellasMap.keySet());
        }
    }

    public static void main(String[] args) {
        if (args.length < 1) {
            return;
        }
        var ip = args[0];

        try {
            CopiesFinder copiesFinder = new CopiesFinder(ip);
            copiesFinder.start();
        } catch (IOException | NoSuchElementException e) {
            System.err.println(e.getMessage());
        }
    }

    public void start() throws IOException {
        var buffer = ByteBuffer.allocate(18);
        while (true) {
            var fella = recieveChannel.receive(buffer).toString();
            if (!Arrays.equals(buffer.array(), msg)) {
                continue;
            }

            if (!fellasMap.containsKey(fella)) {
                addFella(fella);
                continue;
            }

            fellasMap.replace(fella, 0);
        }
    }

    private void addFella(String address) {
        fellasMap.put(address, 0);
        FellaPrinter.printFellas(fellasMap.keySet());
    }
}