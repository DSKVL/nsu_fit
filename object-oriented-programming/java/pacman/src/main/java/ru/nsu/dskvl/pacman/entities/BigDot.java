package ru.nsu.dskvl.pacman.entities;

import ru.nsu.dskvl.pacman.PacmanSpriteManager;
import ru.nsu.dskvl.pacman.engine.ModesManager;
import ru.nsu.dskvl.pacman.engine.ScoreManager;
import ru.nsu.dskvl.pacman.visuals.DynamicEntityVisuals;
import ru.nsu.dskvl.pacman.visuals.GameVisuals;

import java.awt.*;

public class BigDot extends Dot {

    public BigDot(int x, int y, GameVisuals vis, Tile tile) {
        position.setLocation(x, y);
        display = new DynamicEntityVisuals(this, PacmanSpriteManager.loadAnimation("BigDot", this, 4));
        this.hitbox = new Rectangle(x + 1, y + 1, 6, 6);
        this.visuals = vis;
        this.tile = tile;
    }

    @Override
    public void update() {
        super.update();
        ScoreManager.getInstance().addScore(40);
        ModesManager.getInstance().bigDotEaten();
    }
}
