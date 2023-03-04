package ru.nsu.fit.dskvl.snake.visuals.game;

import ru.nsu.fit.dskvl.snake.data.SnakeGameParameters;
import ru.nsu.fit.dskvl.snake.data.SnakeGameState;

import javax.swing.*;
import java.awt.*;

public class SnakeGamePane extends JPanel {
    SnakeGameField gameField;
    SnakeGameStats gameStats;
    JButton leaveButton;

    public SnakeGamePane() {
        gameField = new SnakeGameField();
        gameStats = new SnakeGameStats();
        leaveButton = new JButton("Leave game");

        this.setLayout(new GridLayout(1, 2, 10, 10));

        JPanel paneUI = new JPanel();
        paneUI.setLayout(new BoxLayout(paneUI, BoxLayout.Y_AXIS));
        paneUI.add(gameStats, BorderLayout.CENTER);
        leaveButton.setAlignmentY(Component.TOP_ALIGNMENT);
        paneUI.add(leaveButton, BorderLayout.PAGE_END);

        this.add(gameField);
        this.add(paneUI);
    }

    public JButton getLeaveElement() {
        return leaveButton;
    }

    public void setParameters(SnakeGameParameters gameParameters) {
        gameField.setDimensions(gameParameters.height(), gameParameters.width());
    }

    public void updateGameField(SnakeGameState gameState) {
        gameField.updateGameField(gameState);
    }

    public void gameExit() {
        gameField.gameExit();
    }
}
