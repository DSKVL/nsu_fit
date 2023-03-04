package ru.nsu.fit.dskvl.snake;

import ru.nsu.fit.dskvl.snake.data.*;
import ru.nsu.fit.dskvl.snake.network.SnakeNetwork;
import ru.nsu.fit.dskvl.snake.visuals.SnakeView;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

public class GameController {
    SnakeRole role;
    SnakeView view;
    SnakeNetwork communicator;
    SnakeGameParameters gameParameters;
    int selfID;

    GameLogic model;
    Timer updateTimer;
    Timer announceTimer;
    HashMap<Integer, SnakePlayer> peers;

    public GameController(SnakeView view, SnakeNetwork communicator, SnakeGameParameters gameParameters) {
        this.view = view;
        this.communicator = communicator;
        this.gameParameters = gameParameters;
        this.role = this.gameParameters.role();

        view.showGame(this.gameParameters);
    }

    public void run() {
        if (role == SnakeRole.MASTER) {
            model = new GameLogic(gameParameters.width(), gameParameters.height(),
                    gameParameters.foodStatic());
            selfID = model.addSnake();
            peers = new HashMap<>();
            updateTimer = new Timer("Game Update");
            int updatePeriod = gameParameters.delay();
            updateTimer.scheduleAtFixedRate(new TimerTask() {
                public void run() {
                    updateGameState();
                }
            }, updatePeriod, updatePeriod);

            announceTimer = new Timer();
            announceTimer.scheduleAtFixedRate(new TimerTask() {
                public void run() {
                    announcePresence();
                }
            }, 0, 1000);

            communicator.onJoinRequest(this::joinPeer);
            communicator.onSteerGet(this::steerPeer);

            SnakeGameState gameState = model.currentState();
            view.updateGameField(gameState);
        } else if (role == SnakeRole.NORMAL || role == SnakeRole.DEPUTY) {
            communicator.onStateGet(this::updateGameState);
        }

        view.onPressArrowKey(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {}

            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
                    steerSnake(selfID, SnakeDirection.RIGHT);
                }
                if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                    steerSnake(selfID, SnakeDirection.LEFT);
                }
                if (e.getKeyCode() == KeyEvent.VK_UP) {
                    steerSnake(selfID, SnakeDirection.UP);
                }
                if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                    steerSnake(selfID, SnakeDirection.DOWN);
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {}
        });
    }

    private SnakeSteer steerPeer(SnakeSteer steer) {
        int peer_id = -1;
        for (var peer: peers.entrySet()) {
            if (peer.getValue().address().equals(steer.address())
                    && peer.getValue().port() == steer.port()) {
                peer_id = peer.getKey();
            }
        }

        if (peer_id != -1)
            steerSnake(peer_id, steer.direction());

        return steer;
    }

    private SnakeJoinRequest joinPeer(SnakeJoinRequest joinRequest) {
        int peerId = model.addSnake();

        SnakePlayer player = new SnakePlayer(
                peerId, joinRequest.playerName(),
                joinRequest.senderAddress(), joinRequest.senderPort(),
                joinRequest.role(), joinRequest.playerType(), 0);
        peers.put(peerId, player);

        return joinRequest;
    }

    public void stop() {
        if (role == SnakeRole.MASTER) {
            updateTimer.cancel();
            announceTimer.cancel();
        }
        communicator.destroyFunctions();
        view.gameExit();
    }

    private void updateGameState() {
        SnakeGameState gameState = model.nextState();
        view.updateGameField(gameState);

        peers.replaceAll((i, v) -> peers.get(i).newScore(model.getScore(i)));

        for (var peer: peers.values()) {
            communicator.sendGameState(peer, gameState, peers);
        }
    }

    private SnakeGameState updateGameState(SnakeGameState gameState) {
        view.updateGameField(gameState);
        return gameState;
    }

    private void steerSnake(int id, SnakeDirection dir) {
        if (role == SnakeRole.MASTER)
            model.steerSnake(id, dir);
        else if (role == SnakeRole.NORMAL || role == SnakeRole.DEPUTY)
            communicator.sendSteerMsg(dir);
    }

    private void announcePresence() {
        SnakeGameAnnouncement gameAnnouncement = new SnakeGameAnnouncement();
        gameAnnouncement.name = gameParameters.name();
        gameAnnouncement.canJoin = model.canSpawn();
        gameAnnouncement.gameParameters = this.gameParameters;
        gameAnnouncement.players = new HashMap<>(peers);

        communicator.sendAnnouncementMsg(gameAnnouncement);
    }
}
