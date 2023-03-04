package ru.nsu.fit.dskvl.snake.visuals.game;

import ru.nsu.fit.dskvl.snake.data.Point;
import ru.nsu.fit.dskvl.snake.data.SnakeGameState;

import javax.swing.*;

import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SnakeGameField extends JPanel {
    int tileRows;
    int tileCols;
    Map<Point, JPanel> tiles = new HashMap<>();
    Map<Integer, Color> snakeColors = new HashMap<>();

    public void setDimensions(int height, int width) {
        this.removeAll();
        tiles.clear();

        this.setLayout(new GridLayout(height, width, 3, 3));

        for (int x = 0; x < height; x++) {
            for (int y = 0; y < width; y++) {
                JPanel tile = new JPanel();

                tile.setBackground(Color.WHITE);

                tiles.put(new Point(x, y), tile);
                this.add(tile);
            }
        }

        this.tileRows = height;
        this.tileCols = width;
    }

    public void updateGameField(SnakeGameState gameState) {
        for (int x = 0; x < tileRows; x++) {
            for (int y = 0; y < tileCols; y++) {
                JPanel tile = tiles.get(new Point(x, y));

                tile.setBackground(Color.WHITE);
            }
        }

        for (var food: gameState.getFoods()) {
            JPanel tile = tiles.get(food);

            tile.setBackground(Color.RED);
        }

        for (var snake: gameState.getSnakes().values()) {
            if (!snakeColors.containsKey(snake.id)) {
                float hue = (float)Math.random();
                int rgb = Color.HSBtoRGB(hue,1f,0.8f);
                Color color = new Color(rgb);
                snakeColors.put(snake.id, color);
            }

            List<Point> body = snake.getBody();
            Point currentPoint = snake.getHead();
            for (var body_part: body) {
                if (body_part != body.get(0))
                    currentPoint = currentPoint.add(body_part).wrap(tileRows, tileCols);
                JPanel tile = tiles.get(currentPoint);
                tile.setBackground(snakeColors.get(snake.id));
            }
        }
    }

    public void gameExit() {
        snakeColors.clear();
        tiles.clear();
    }
}
