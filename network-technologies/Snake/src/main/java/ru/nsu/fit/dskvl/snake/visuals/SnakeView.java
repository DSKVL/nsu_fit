package ru.nsu.fit.dskvl.snake.visuals;

import ru.nsu.fit.dskvl.snake.data.SnakeGameAnnouncement;
import ru.nsu.fit.dskvl.snake.data.SnakeGameParameters;
import ru.nsu.fit.dskvl.snake.data.SnakeGameState;
import ru.nsu.fit.dskvl.snake.visuals.game.SnakeGamePane;
import ru.nsu.fit.dskvl.snake.visuals.menu.SnakeCreateGamePane;
import ru.nsu.fit.dskvl.snake.visuals.menu.SnakeMenuPane;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.awt.event.KeyListener;
import java.util.function.UnaryOperator;

public class SnakeView extends JFrame {

    SnakeMenuPane menuPane;
    SnakeCreateGamePane createGamePane;
    SnakeGamePane gamePane;

    public SnakeView() {
        this.setName("Snake game");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setBounds(500, 300, 2700/3, 2700/5);
        this.setFocusable(true);

        menuPane = new SnakeMenuPane();
        createGamePane = new SnakeCreateGamePane();
        gamePane = new SnakeGamePane();

        menuPane.getCreateElement().addActionListener(e -> this.showCreateMenu());

        createGamePane.getCancelElement().addActionListener(e -> this.showMainMenu());
    }

    public void showMainMenu() {
        this.setContentPane(menuPane);
        this.setVisible(true);
    }

    private void showCreateMenu() {
        this.setContentPane(createGamePane);
        this.setVisible(true);
    }

    public void showGame(SnakeGameParameters gameParameters) {
        if (gameParameters != null) gamePane.setParameters(gameParameters);
        this.setContentPane(gamePane);
        this.setVisible(true);
    }

    public void onPressLaunch(ActionListener actionListener) {
        createGamePane.getLaunchElement().addActionListener(actionListener);
    }

    public SnakeGameParameters getGameParameters() {
        return createGamePane.getGameParameters();
    }

    public void onPressLeave(ActionListener actionListener) {
        gamePane.getLeaveElement().addActionListener(actionListener);
    }


    public void onPressArrowKey(KeyListener keyListener) {
        this.addKeyListener(keyListener);
    }

    public void updateGameField(SnakeGameState gameState) {
        gamePane.updateGameField(gameState);
    }

    @Override
    public void setFocusable(boolean b) {
        super.setFocusable(b);
    }

    public void gameExit() {
        // Remove key listeners
        for (var kl: this.getKeyListeners()) {
            this.removeKeyListener(kl);
        }

        // Clear game panel
        gamePane.gameExit();

        // Show main menu
        showMainMenu();
    }

    public void addGameAnnouncement(SnakeGameAnnouncement gameAnnouncement) {
        menuPane.addGameAnnouncement(gameAnnouncement);
    }

    public void removeGameAnnouncement(SnakeGameAnnouncement gameAnnouncement) {
        menuPane.removeGameAnnouncement(gameAnnouncement);
    }
    public void onPressConnect(UnaryOperator<SnakeGameAnnouncement> function) {
        menuPane.onPressConnect(function);
    }

    public void showCreateErrorMsg(String s) {
        menuPane.showCreateErrorMsg(s);
    }
}
