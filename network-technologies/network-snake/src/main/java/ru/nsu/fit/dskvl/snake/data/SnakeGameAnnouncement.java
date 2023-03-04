package ru.nsu.fit.dskvl.snake.data;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;

public class SnakeGameAnnouncement {
    public String name;
    public InetAddress address;
    public int port;

    public boolean canJoin;

    public SnakeGameParameters gameParameters;
    public Map<Integer, SnakePlayer> players;

    public void setAddress(InetAddress address, int port) {
        this.address = address;
        this.port = port;
    }
}
