package ru.nsu.fit.dskvl.filesender;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.StandardOpenOption;
import java.util.*;

public class Client {
    private static class SpeedCounter {
        private static final int AVERAGE_LENGTH = 8;
        private static final List<Long> sendCountsData = new ArrayList<>(Collections.nCopies(8, -1L));
        private static int currentData = 0;
        long sendedBytesCount = 0L;

        synchronized public void incrementSpeed(long delta) {
            sendedBytesCount += delta;
        }
        synchronized public long calculateSpeed() {
            sendCountsData.set((currentData++) % AVERAGE_LENGTH, sendedBytesCount);
            sendedBytesCount = 0L;
            var initialized = sendCountsData.stream().filter(x -> x != -1).toList();
            return initialized.stream().reduce(0L, Long::sum)/(initialized.size()*3L);
        }
        synchronized public long calculateSpeedLast() {
            return sendedBytesCount;
        }
    }
    private final SpeedCounter counter = new SpeedCounter();
    private void startSpeedCounter() {
        new Timer(true).schedule(new TimerTask() {
            @Override
            public void run() {
                System.out.println(counter.calculateSpeed());
            }
        },0, 3000);
    }



    private boolean sendFile(InetAddress address, int port, File file) {
        startSpeedCounter();

        try (var server = SocketChannel.open(new InetSocketAddress(address, port));
             var fileChannel = FileChannel.open(file.toPath(), StandardOpenOption.READ)) {

            var buf = ByteBuffer.allocate(server.socket().getSendBufferSize());
            var fileName = file.toString().getBytes(StandardCharsets.UTF_8);
            buf.putInt(fileName.length);
            buf.put(fileName);
            buf.putLong(fileChannel.size());

            while(-1 != fileChannel.read(buf)) {
                buf.flip();
                counter.incrementSpeed(server.write(buf));
                buf.flip();
            }

            System.out.println(counter.calculateSpeedLast());

            server.read(buf);
            buf.flip();
            var size = buf.getLong();
            return size == fileChannel.size();
        } catch (IOException e) {
            System.out.println("Unable to connect");
            return false;
        }
    }

    public static void main(String[] args) {
        var file = new File(args[0]);
        if (!(file.exists() && file.isFile())) {
            System.out.println("Specify valid file.");
            return;
        }
        InetAddress address;
        try {
            address = InetAddress.getByName(args[1]);
        } catch (UnknownHostException e) {
            System.out.println("Specify valid adress ");
            return;
        }
        int port = Integer.parseInt(args[2]);

        if(new Client().sendFile(address, port, file)) {
            System.out.println("Success");
        } else {
            System.out.println("Failure");
        }
    }
}