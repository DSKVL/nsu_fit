package ru.nsu.fit.dskvl.snake.data;

import java.net.InetAddress;

public record SnakeJoinRequest(
        String playerName,
        String gameName,
        SnakeRole role,
        int playerType,
        InetAddress senderAddress,
        int senderPort
) {
    public SnakeJoinRequest newSenderSocketAddress(InetAddress newSenderAddress,
                                            int newSenderPort) {
        return new SnakeJoinRequest(playerName, gameName, role, playerType, newSenderAddress, newSenderPort);
    }
}
