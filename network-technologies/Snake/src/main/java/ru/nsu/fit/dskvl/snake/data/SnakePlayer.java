package ru.nsu.fit.dskvl.snake.data;

import java.net.InetAddress;

public record SnakePlayer(
        int id,
        String name,
        InetAddress address,
        int port,
        SnakeRole role,
        int playerType,
        int score
) {
    public SnakePlayer newScore(int newScore) {
        return new SnakePlayer(id, name, address, port, role, playerType, newScore);
    }
}
