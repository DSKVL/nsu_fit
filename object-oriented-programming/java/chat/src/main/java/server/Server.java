package Server;

import java.io.IOException;
import java.net.InetSocketAddress;

import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;


public class Server {
    private final Selector selector = Selector.open();
    private final Map<SocketChannel, SelectionKey> keyMap = new HashMap<>();
    private final Map<SocketChannel, ArrayDeque<ByteBuffer>> queueMap = new HashMap<>();
    private final Thread thread = new Thread(this::work);

    public Server() throws IOException {
        ServerSocketChannel server = ServerSocketChannel.open();
        server.socket().bind(new InetSocketAddress("localhost", 6765));
        server.configureBlocking(false);
        server.register(selector, SelectionKey.OP_ACCEPT);
        System.out.println(server.socket().getLocalSocketAddress().toString() + " is up.");
    }

    private void work() {
        while (true) {
            try {
                selector.select();
            } catch (IOException e) {
                System.err.println(e.getMessage());
            }

            var iterator = selector.selectedKeys().iterator();

            while (iterator.hasNext()) {
                SelectionKey key = iterator.next();

                if (key.isAcceptable()) {
                    accepter.accept(this, (ServerSocketChannel) key.channel());
                }

                try {
                    if (key.isWritable()) {
                        send(key);
                    }

                    if (key.isReadable()) {
                        DataReader reader = (DataReader) key.attachment();
                        reader.read();
                    }

                } catch (IOException e) {
                    System.err.println(e.getMessage() + " " + key.channel());
                    key.cancel();
                    accepter.anErrorOccurred((SocketChannel) key.channel());
                }
                iterator.remove();
            }
        }
    }

    private void send(SelectionKey key) throws IOException {
        var socketChannel = (SocketChannel) key.channel();
        var queue = queueMap.get(socketChannel);
        try {
            synchronized (queueMap.get(socketChannel)) {
                var buffer = queue.pop();
                socketChannel.write(buffer);
                if (queue.isEmpty()) {
                    key.interestOpsAnd(SelectionKey.OP_READ);
                }
            }
        } catch (NoSuchElementException e) {
            key.interestOpsAnd(SelectionKey.OP_READ);
        }
    }

    public void sendData(SocketChannel socketChannel, ByteBuffer buffer) {
        var queue = queueMap.get(socketChannel);
        synchronized (queueMap.get(socketChannel)) {
            queue.push(buffer);
            keyMap.get(socketChannel).interestOpsOr(SelectionKey.OP_WRITE);
        }

        selector.wakeup();
    }
}
