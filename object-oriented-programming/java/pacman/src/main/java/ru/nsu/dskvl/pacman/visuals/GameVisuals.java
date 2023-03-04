package ru.nsu.dskvl.pacman.visuals;

import ru.nsu.dskvl.pacman.CurrentKeyInput;
import ru.nsu.dskvl.pacman.InputHandler;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;


public class GameVisuals {
    private final JFrame mainWindow;
    private final GamePanel gamePanel;
    private List<Drawable> displayLayers = new ArrayList<>();
    public static int scale = 3;

    public GameVisuals() {
        mainWindow = new JFrame();
        mainWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainWindow.setResizable(false);
        mainWindow.setTitle("Pacman");

        gamePanel = new GamePanel();
        gamePanel.setDisplayLayers(displayLayers);
        mainWindow.add(gamePanel);
        mainWindow.pack();

        mainWindow.setLocationRelativeTo(null);
        mainWindow.setVisible(true);
    }

    public CurrentKeyInput getCurrentKeyInput() {
        return gamePanel.getCurrentKeyInput();
    }

    public void draw() {
        gamePanel.repaint();
    }

    public void setDisplayLayers(List<Drawable> displayLayers) {
        this.displayLayers = displayLayers;
    }

    public List<Drawable> getDisplayLayers() {
        return displayLayers;
    }


    public int getScale() {
        return scale;
    }

    public static void setScale(int sc) {
        scale = sc;
    }
}


class GamePanel extends JPanel {
    private int widthPixels = 224;
    private int heightPixels = 288;
    private List<Drawable> displayLayers;
    private final InputHandler inputHandler = new InputHandler();

    public void setDisplayLayers(List<Drawable> displayLayers) {
        this.displayLayers = displayLayers;
    }

    public List<Drawable> getDisplayLayers() {
        return displayLayers;
    }

    GamePanel() {
        this.setPreferredSize(new Dimension(widthPixels*GameVisuals.scale, heightPixels*GameVisuals.scale));
        this.setBackground(Color.BLACK);
        this.setDoubleBuffered(true);
        this.addKeyListener(inputHandler);
        this.setFocusable(true);
    }

    public void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        Graphics2D graphics2D = (Graphics2D) graphics;

        for (var drawable : displayLayers) {
                drawable.draw(graphics2D);
        }
    }

    public CurrentKeyInput getCurrentKeyInput() {
        return inputHandler.getCurrentKeyInput();
    }
}
