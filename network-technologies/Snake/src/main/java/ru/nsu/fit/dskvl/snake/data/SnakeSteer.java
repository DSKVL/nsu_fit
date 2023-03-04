package ru.nsu.fit.dskvl.snake.data;

import java.net.InetAddress;

public record SnakeSteer(
        int id,
        InetAddress address,
        int port,
        SnakeDirection direction
) {
    public SnakeSteer newSenderSocketAddress(InetAddress newSenderAddress,
                                                   int newSenderPort) {
        return new SnakeSteer(id, newSenderAddress, newSenderPort, direction);
    }
}
