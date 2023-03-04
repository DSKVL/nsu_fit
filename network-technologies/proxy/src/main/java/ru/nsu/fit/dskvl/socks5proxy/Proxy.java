package ru.nsu.fit.dskvl.socks5proxy;

import org.xbill.DNS.*;
import org.xbill.DNS.Record;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Proxy {
    record SockAndPort(SocketChannel channel, short port) {
    }

    public Map<Integer, SockAndPort> getServerMap() {
        return serverMap;
    }

    Proxy(int port) throws IOException {
        serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.socket().bind(new InetSocketAddress(port));
        serverSocketChannel.configureBlocking(false);
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        resolverChannel = DatagramChannel.open();
        for (var dnsAddress : dnsServers) {
            try {
                resolverChannel.connect(dnsAddress);
                break;
            } catch (IOException ignored) {
            }
        }
        resolverChannel.configureBlocking(false).register(selector, SelectionKey.OP_READ);
    }

    public void start() {
        while (true) {
            try {
                selector.select();
            } catch (IOException e) {
                System.err.println(e.getMessage());
            }

            var iterator = selector.selectedKeys().iterator();

            while (iterator.hasNext()) {
                SelectionKey key = iterator.next();

                if (key.channel() == serverSocketChannel) {
                    acceptConnection();
                    iterator.remove();
                    continue;
                }

                if (key.channel() == resolverChannel) {
                    try {
                        resolverChannel.read(resolverBuffer);
                    } catch (IOException e) {
                        System.err.println("Unable to read from DNS server. I'll just die, i wont be able to function properly anymore.");
                    }

                    resolverBuffer.flip();
                    try {
                        var dnsResponse = new Message(resolverBuffer);
                        var sockAndPort = serverMap.get(dnsResponse.getHeader().getID());
                        var sock = sockAndPort.channel;
                        var port = sockAndPort.port;
                        for (Record record : dnsResponse.getSection(Section.ANSWER)) {
                            if (record instanceof ARecord it) {
                                try {
                                    connectToServer(sock, it.getAddress(), port);
                                    break;
                                } catch (IOException ignore) {
                                }
                            }
                        }
                        resolverBuffer.compact();
                    } catch (IOException e) {
                        resolverBuffer.flip();
                    }
                    iterator.remove();
                    continue;
                }
                try {
                    if (key.isValid() && key.isReadable()) ((ProxyChannel) key.attachment()).read(key, this);
                } catch (IOException e) {
                    System.err.println(e.getMessage());
                    key.cancel();
                }

                if (key.isValid() && key.isWritable()) ((ProxyChannel) key.attachment()).write(key);

                iterator.remove();
            }
        }
    }

    private void acceptConnection() {
        try {
            var clientSocket = serverSocketChannel.accept();
            clientSocket.configureBlocking(false);
            clientSocket.register(selector, SelectionKey.OP_READ)
                    .attach(new NewChannel(clientSocket));
        } catch (IOException e) {
            System.out.println("Someone tried to connect but failed");
        }
    }

    public void connectToServer(SocketChannel client, InetAddress address, short port) throws IOException {
        var serverAddress = new InetSocketAddress(address, port);
        SocketChannel serverSocketChannel;
        serverSocketChannel = SocketChannel.open(serverAddress);
        serverSocketChannel.configureBlocking(false);
        var myPort = serverSocketChannel.socket().getLocalPort();

        var clientByteBuffer = ByteBuffer.allocate(4096);
        var serverByteBuffer = ByteBuffer.allocate(4096);
        byte[] portbytes = ByteBuffer.allocate(2).putShort((short) myPort).array();
        var bytes = address.getAddress();
        serverByteBuffer.put(new byte[]{5, 0, 0, 1,
                bytes[0], bytes[1], bytes[2], bytes[3], portbytes[0], portbytes[1]});
        var serverKey = serverSocketChannel.register(selector, SelectionKey.OP_READ);
        var clientKey = client.register(selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE);
        var clientChannel = new Channel(client, clientByteBuffer, clientKey,
                serverSocketChannel, serverByteBuffer, serverKey);
        clientKey.attach(clientChannel);
        serverKey.attach(clientChannel);
    }

    private final Selector selector = Selector.open();
    private final ServerSocketChannel serverSocketChannel;
    private final Map<Integer, SockAndPort> serverMap = new HashMap<>();

    public DatagramChannel getResolverChannel() {
        return resolverChannel;
    }

    private final DatagramChannel resolverChannel;
    private final ByteBuffer resolverBuffer = ByteBuffer.allocate(512);
    public static final List<InetSocketAddress> dnsServers = ResolverConfig.getCurrentConfig().servers();

    public static final byte[] HandshakeStartResponse = new byte[]{5, 0};
    public static final byte[] HandshakeStartError = new byte[]{5, -128};
    public static final byte[] HandshakeProtocolError = new byte[]{5, 7, 0, 0, 0, 0, 0, 0, 0, 0};
}

enum HandshakeStage {
    STARTED, REQUESTING, ESTABLISHED;

    HandshakeStage next() {
        return switch (this) {
            case STARTED -> REQUESTING;
            case REQUESTING, ESTABLISHED -> ESTABLISHED;
        };
    }
}

interface ProxyChannel {
    void read(SelectionKey key, Proxy proxy) throws IOException;

    void write(SelectionKey key);
}

class NewChannel implements ProxyChannel {
    NewChannel(SocketChannel channel) {
        this.channel = channel;
    }

    @Override
    public void read(SelectionKey key, Proxy proxy) throws IOException {
        switch (stage) {
            case STARTED -> connectionStartedRead(key);
            case REQUESTING -> connectionRequestingRead(key, proxy);
        }
    }

    void connectionStartedRead(SelectionKey key) throws IOException {
        channel.read(readBuffer);
        byte[] auth;
        try {
            readBuffer.flip();
            readBuffer.get();
            byte nauth = readBuffer.get();
            auth = new byte[nauth];
            readBuffer.get(auth);
        } catch (BufferUnderflowException e) {
            return;
        }

        readBuffer.compact();

        boolean containsNoAuth = false;
        for (var b : auth) {
            containsNoAuth = b == 0;
        }

        if (!containsNoAuth) {
            writeBuffer.put(Proxy.HandshakeStartError);
            key.interestOps(0);
            lastMessage = true;
        } else {
            writeBuffer.put(Proxy.HandshakeStartResponse);
        }
        key.interestOps(key.interestOps() | SelectionKey.OP_WRITE);
    }

    void connectionRequestingRead(SelectionKey key, Proxy proxy) throws IOException {
        var readCount = channel.read(readBuffer);
        if (readBuffer.position() < 10) {
            if (readCount == 0) key.cancel();
            return;
        }

        readBuffer.flip();
        if (readBuffer.get() != (byte) 5) {
            key.cancel();
        }

        if (readBuffer.get() != (byte) 1 | readBuffer.get() != (byte) 0) {
            writeBuffer.put(Proxy.HandshakeProtocolError);
            key.interestOps(SelectionKey.OP_WRITE);
            lastMessage = true;
            return;
        }

        switch (readBuffer.get()) {
            case (byte) 1 -> {
                byte[] bytes = new byte[4];
                readBuffer.get(bytes);
                StringBuilder address = new StringBuilder();
                for (int i = 0; i < 3; i++)
                    address.append(bytes[i] < 0 ? 256 + bytes[i] : bytes[i]).append(".");
                address.append(bytes[3] < 0 ? 256 + bytes[3] : bytes[3]);
                int port = readBuffer.getShort();

                var serverAddress = new InetSocketAddress(address.toString(), port);
                proxy.connectToServer(channel, serverAddress.getAddress(), (short) port);
            }
            case (byte) 3 -> {
                byte len = readBuffer.get();
                byte[] address = new byte[len];
                readBuffer.get(address);
                short port = readBuffer.getShort();
                var strAddress = new String(address, StandardCharsets.US_ASCII);

                var queryRecord = Record.newRecord(Name.fromString(strAddress + "."), Type.A, DClass.IN);
                Message queryMessage = Message.newQuery(queryRecord);
                var resolver = proxy.getResolverChannel();
                resolver.write(ByteBuffer.wrap(queryMessage.toWire(512)));
                proxy.getServerMap().put(queryMessage.getHeader().getID(), new Proxy.SockAndPort(channel, port));
            }
            default -> {
                writeBuffer.put(Proxy.HandshakeProtocolError);
                key.interestOps(SelectionKey.OP_WRITE);
                lastMessage = true;
                System.err.println("Unable to process.");
            }
        }
        readBuffer.compact();
    }

    @Override
    public void write(SelectionKey key) {
        writeBuffer.flip();
        try {
            var writeCount = channel.write(writeBuffer);
            System.err.println("New connection wrote " + writeCount);
            writeBuffer.compact();
            if (writeBuffer.position() == 0) key.interestOps(key.interestOps() ^ SelectionKey.OP_WRITE);
            if (lastMessage) {
                key.cancel();
                channel.close();
            }
            stage = stage.next();
        } catch (IOException e) {
            System.err.println("Unable to write to new connection");
            key.cancel();
        }
    }

    private final SocketChannel channel;
    private final ByteBuffer readBuffer = ByteBuffer.allocate(64);
    private final ByteBuffer writeBuffer = ByteBuffer.allocate(64);
    HandshakeStage stage = HandshakeStage.STARTED;
    boolean lastMessage = false;
}

class Channel implements ProxyChannel {
    Channel(SocketChannel clientChannel, ByteBuffer clientBuffer, SelectionKey clientKey,
            SocketChannel serverChannel, ByteBuffer serverBuffer, SelectionKey serverKey) {

        this.clientChannel = clientChannel;
        this.clientBuffer = clientBuffer;
        this.serverChannel = serverChannel;
        this.serverBuffer = serverBuffer;
        this.serverKey = serverKey;
        this.clientKey = clientKey;
    }

    @Override
    public void read(SelectionKey key, Proxy proxy) {
        try {
            if (key.channel() == clientChannel) {
                var readCount = clientChannel.read(clientBuffer);
                System.err.println("Client read " + readCount);
                if (readCount == -1)
                    throw new IOException();
                serverKey.interestOps(serverKey.interestOps() | SelectionKey.OP_WRITE);
            } else {
                var readCount = serverChannel.read(serverBuffer);
                System.err.println("Server read " + readCount);
                if (readCount == -1)
                    throw new IOException();
                clientKey.interestOps(serverKey.interestOps() | SelectionKey.OP_WRITE);
            }
        } catch (IOException e) {
            shut();
        }
    }

    private void shut() {
        try {
            serverChannel.close();
            clientChannel.close();
        } catch (IOException e) {
            System.err.println("Some errors during shutting.");
        }

        serverKey.cancel();
        clientKey.cancel();
    }

    @Override
    public void write(SelectionKey key) {
        try {
            if (key.channel() == clientChannel) {
                serverBuffer.flip();
                var writeCount = clientChannel.write(serverBuffer);
                System.err.println("Client write " + writeCount);
                serverBuffer.compact();
                if (serverBuffer.position() == 0) key.interestOps(key.interestOps() ^ SelectionKey.OP_WRITE);
            } else {
                clientBuffer.flip();
                System.err.println("Server write " + serverChannel.write(clientBuffer));
                clientBuffer.compact();
                if (clientBuffer.position() == 0) key.interestOps(key.interestOps() ^ SelectionKey.OP_WRITE);
            }
        } catch (IOException e) {
            System.err.println("Unable to write somewhere");
        }
    }

    private final SocketChannel clientChannel;
    private final ByteBuffer clientBuffer;
    private final SelectionKey clientKey;
    private final SocketChannel serverChannel;
    private final ByteBuffer serverBuffer;
    private final SelectionKey serverKey;
}
