package ru.nsu.fit.dskvl.snake.network;

import com.google.protobuf.*;
import ru.nsu.fit.dskvl.snake.data.*;

import java.io.IOException;
import java.net.*;
import java.util.HashMap;
import java.util.function.UnaryOperator;

public class SnakeNetwork {
    public SnakeNetwork(String multicastAddress, int multicastPort) throws IOException {
        publicSocket = new MulticastSocket(multicastPort);
        InetAddress address = InetAddress.getByName(multicastAddress);
        publicSocket.joinGroup(new InetSocketAddress(address, multicastPort), null);
        personalSocket = new DatagramSocket();

        this.mcAddress = address;
        this.mcPort = multicastPort;

        multicastListener = new Thread( () -> { while (true) listenOnMulticast(); });
        multicastListener.start();

        peerListener = new Thread(() -> { while (true) listenPeers(); });
        peerListener.start();
    }

    private void listenOnMulticast() {
        byte[] buff = new byte[4096];
        DatagramPacket recvPacket = new DatagramPacket(buff, 4096);
        try {
            publicSocket.receive(recvPacket);
            byte[] data = new byte[recvPacket.getLength()];
            System.arraycopy(recvPacket.getData(), 0, data, 0, data.length);

            SnakeProtoConverter.MessageType msgType = SnakeProtoConverter.readGameMessageType(data);

            switch (msgType) {
                case ANNOUNCEMENT:
                    SnakeGameAnnouncement gameAnnouncement = SnakeProtoConverter.readGameAnnouncement(data);
                    gameAnnouncement.setAddress(recvPacket.getAddress(), recvPacket.getPort());
                    announcementFunction.apply(gameAnnouncement);
                    break;
                case DISCOVER:
                    break;
                default:
                    System.out.println("Incorrect message type");
                    break;
            }
        } catch (InvalidProtocolBufferException e) {
            System.out.println("Failed to parse received message");
        } catch (IOException e) {
            System.out.println("Failed to receive message");
        }
    }

    private void listenPeers() {
        byte[] buff = new byte[4096];
        DatagramPacket recvPacket = new DatagramPacket(buff, 4096);
        try {
            personalSocket.receive(recvPacket);

            byte[] data = new byte[recvPacket.getLength()];
            System.arraycopy(recvPacket.getData(), 0, data, 0, data.length);

            SnakeProtoConverter.MessageType msgType = SnakeProtoConverter.readGameMessageType(data);

            switch (msgType) {
                case JOIN -> {
                    SnakeJoinRequest joinRequest =
                            SnakeProtoConverter.readJoinMsg(data).newSenderSocketAddress(
                                    recvPacket.getAddress(), recvPacket.getPort()
                            );
                    joinFunction.apply(joinRequest);
                }
                case STATE -> {
                    SnakeGameState gameState = SnakeProtoConverter.readStateMsg(data);
                    stateFunction.apply(gameState);
                }
                case STEER -> {
                    SnakeSteer steer = SnakeProtoConverter.readSteerMsg(data)
                            .newSenderSocketAddress(recvPacket.getAddress(), recvPacket.getPort());
                    steerFunction.apply(steer);
                }
                default -> System.out.println("Incorrect peer-to-peer message type");
            }
        } catch (InvalidProtocolBufferException e) {
            System.out.println("Failed to parse join message");
        } catch (IOException e) {
            System.out.println("Personal socket couldn't receive message");
        }
    }

    public void sendDiscoverMessage() {
        byte[] message = SnakeProtoConverter.writeDiscoverMessage(msg_seq++);
        DatagramPacket packet = new DatagramPacket(message, message.length, mcAddress, mcPort);

        try {
            personalSocket.send(packet);
        } catch (IOException e) {
            System.out.println("Couldn't send DiscoverMsg");
        }
    }

    public void sendAnnouncementMsg(SnakeGameAnnouncement gameAnnouncement) {
        byte[] message = SnakeProtoConverter.writeAnnouncementMessage(msg_seq++, gameAnnouncement);
        DatagramPacket packet = new DatagramPacket(message, message.length, mcAddress, mcPort);

        try {
            personalSocket.send(packet);
        } catch (IOException e) {
            System.out.println("Couldn't send AnnouncementMsg");
        }
    }

    public void onReceiveAnnouncement(UnaryOperator<SnakeGameAnnouncement> function) {
        announcementFunction = function;
    }

    public void joinGame(SnakeGameAnnouncement gameAnnouncement) {
        masterAddress = gameAnnouncement.address;
        masterPort = gameAnnouncement.port;
        sendJoinMsg(gameAnnouncement.name);
    }

    private void sendJoinMsg(String gameName) {
        byte[] message = SnakeProtoConverter.writeJoinMessage(msg_seq++, gameName);
        DatagramPacket packet = new DatagramPacket(message, message.length, masterAddress, masterPort);

        try {
            personalSocket.send(packet);
        } catch (IOException e) {
            System.out.println("Couldn't send JoinMsg");
        }
    }

    public void onJoinRequest(UnaryOperator<SnakeJoinRequest> function) {
        joinFunction = function;
    }

    public void destroyFunctions() {
        joinFunction = (e) -> e;
        stateFunction = (e) -> e;
    }

    public void sendGameState(SnakePlayer peer, SnakeGameState gameState, HashMap<Integer, SnakePlayer> peers) {
        byte[] message = SnakeProtoConverter.writeStateMessage(msg_seq++, gameState, peers);
        DatagramPacket packet = new DatagramPacket(message, message.length, peer.address(), peer.port());

        try {
            personalSocket.send(packet);
        } catch (IOException e) {
            System.out.println("Couldn't send StateMsg");
        }
    }

    public void onStateGet(UnaryOperator<SnakeGameState> function) {
        stateFunction = function;
    }

    public void sendSteerMsg(SnakeDirection dir) {
        byte[] message = SnakeProtoConverter.writeSteerMsg(msg_seq++, dir);
        DatagramPacket packet = new DatagramPacket(message, message.length, masterAddress, masterPort);

        try {
            personalSocket.send(packet);
        } catch (IOException e) {
            System.out.println("Couldn't send SteerMsg");
        }
    }

    public void onSteerGet(UnaryOperator<SnakeSteer> function) {
        steerFunction = function;
    }

    private final MulticastSocket publicSocket;
    private final InetAddress mcAddress;
    private final int mcPort;
    private final DatagramSocket personalSocket;
    private int msg_seq = 1;

    private final Thread multicastListener;

    private UnaryOperator<SnakeGameAnnouncement> announcementFunction = UnaryOperator.identity();
    private UnaryOperator<SnakeJoinRequest> joinFunction = UnaryOperator.identity();
    private UnaryOperator<SnakeGameState> stateFunction = UnaryOperator.identity();
    private UnaryOperator<SnakeSteer> steerFunction = UnaryOperator.identity();

    private InetAddress masterAddress;
    private int masterPort;
    private final Thread peerListener;
}
