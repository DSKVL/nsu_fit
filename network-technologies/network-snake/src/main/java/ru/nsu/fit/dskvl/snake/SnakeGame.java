package ru.nsu.fit.dskvl.snake;

import ru.nsu.fit.dskvl.snake.network.SnakeNetwork;
import ru.nsu.fit.dskvl.snake.data.SnakeGameAnnouncement;
import ru.nsu.fit.dskvl.snake.data.SnakeGameParameters;
import ru.nsu.fit.dskvl.snake.data.SnakeRole;
import ru.nsu.fit.dskvl.snake.visuals.SnakeView;

import java.io.IOException;
import java.util.*;

public class SnakeGame {

    final String multicastAddress = "239.192.0.4";
    final int multicastPort = 9192;

    private SnakeView visuals;
    private GameController gameController;
    SnakeNetwork communicator;

    Map<String, SnakeGameAnnouncement> gameList;
    Map<String, SnakeGameAnnouncement> checkList;
    Timer checkListTimer;

    public void start() {
        visuals = new SnakeView();
        visuals.showMainMenu();

        try {
            communicator = new SnakeNetwork(multicastAddress, multicastPort);
        } catch (IOException e) {
            System.out.println("Couldn't connect to socket, shutting down");
            System.exit(e.hashCode());
        }

        gameList = new HashMap<>();
        checkList = new HashMap<>();
        communicator.onReceiveAnnouncement(this::addGame);
        checkListTimer = new Timer();
        checkListTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                for (var game: gameList.values()) {
                    if (checkList.containsKey(game.name)) {
                        visuals.addGameAnnouncement(game);
                    }
                    else {
                        visuals.removeGameAnnouncement(game);
                    }
                }
                gameList.clear();
                for (var game: checkList.values()) {
                    gameList.put(game.name, game);
                    visuals.addGameAnnouncement(game);
                }
                checkList.clear();
            }
        }, 0, 1100);

        communicator.sendDiscoverMessage();
        visuals.onPressConnect(this::joinGame);
        visuals.onPressLaunch(e -> this.launchGame());
        visuals.onPressLeave(e -> this.leaveGame());
    }

    private SnakeGameAnnouncement joinGame(SnakeGameAnnouncement gameAnnouncement) {
        communicator.joinGame(gameAnnouncement);

        SnakeGameParameters gameParameters = gameAnnouncement.gameParameters;
        gameParameters = gameParameters.newRole(SnakeRole.NORMAL);

        gameController = new GameController(visuals, communicator, gameParameters);
        gameController.run();

        return gameAnnouncement;
    }

    private SnakeGameAnnouncement addGame(SnakeGameAnnouncement gameAnnouncement) {
        if (!checkList.containsKey(gameAnnouncement.name))
            checkList.put(gameAnnouncement.name, gameAnnouncement);
        return gameAnnouncement;
    }

    private void leaveGame() {
        gameController.stop();
    }

    private void launchGame() {
        SnakeGameParameters gp = visuals.getGameParameters();
        if (!gameList.containsKey(gp.name())) {
            gp = gp.newRole(SnakeRole.MASTER);
            gameController = new GameController(visuals, communicator, gp);
            gameController.run();
        } else {
            visuals.showCreateErrorMsg("Game with this name already exists");
        }
    }
}
