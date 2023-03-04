package ru.nsu.fit.dskvl.filesender;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.StandardOpenOption;

public class Server {
    private static final File uploadDirectory = new File("uploads");

    private static void receiveFile(SocketChannel client, File uploadDirectory) throws IOException {
        var buffer = ByteBuffer.allocate(client.socket().getReceiveBufferSize());
        client.read(buffer);
        buffer.flip();
        var nameLength = buffer.getInt();
        byte[] fileNameBytes = new byte[nameLength];
        buffer.get(fileNameBytes);
        var fileLength = buffer.getLong();

        var file = new File(uploadDirectory, new String(fileNameBytes, StandardCharsets.UTF_8));
        if (!file.exists() && !file.createNewFile()) {
            System.out.println("Sorry file already exist");
            return;
        }
        try (client; var fileChannel = FileChannel.open(file.toPath(), StandardOpenOption.WRITE)) {
            long writtenCount = fileChannel.write(buffer);
            buffer.flip();

            while (writtenCount != fileLength) {
                client.read(buffer);
                buffer.flip();
                writtenCount += fileChannel.write(buffer);
                buffer.flip();
            }

            sendReceivedSize(writtenCount, client);
        }
    }
    private static void startServer(File uploadDirectory, int port) {
        try (var server = ServerSocketChannel.open().bind(
                new InetSocketAddress(InetAddress.getByName("localhost"),port))) {
            while (true) {
                var client = server.accept();
                new Thread(() -> {
                    try {
                        receiveFile(client, uploadDirectory);
                    } catch (IOException e) {
                        System.out.println("Failed to download");
                    }
                }).start();
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void sendReceivedSize(long receivedSize, SocketChannel client) throws IOException {
        var buf = ByteBuffer.allocate(Long.BYTES);
        buf.putLong(receivedSize);
        buf.flip();
        client.write(buf);
    }

    public static void main(String[] args) {
        if (uploadDirectory.exists() && !uploadDirectory.isDirectory()) {
            System.out.println("Unable to create uploads directory.");
            return;
        }
        if (!uploadDirectory.exists() && !uploadDirectory.mkdir()) {
            System.out.println("Unable to create uploads directory.");
            return;
        }

        var port = Integer.parseInt(args[0]);

        startServer(uploadDirectory, port);
    }

}
