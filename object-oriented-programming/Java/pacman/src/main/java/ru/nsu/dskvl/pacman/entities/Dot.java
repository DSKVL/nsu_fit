package ru.nsu.dskvl.pacman.entities;

import ru.nsu.dskvl.pacman.PacmanSpriteManager;
import ru.nsu.dskvl.pacman.engine.ScoreManager;
import ru.nsu.dskvl.pacman.visuals.Drawable;
import ru.nsu.dskvl.pacman.visuals.GameVisuals;
import ru.nsu.dskvl.pacman.visuals.StaticEntityVisuals;

import java.awt.*;
import java.awt.geom.Rectangle2D;

public class Dot extends Entity {
    protected Drawable display;
    protected Rectangle hitbox;
    protected GameVisuals visuals;
    protected Tile tile;
    protected boolean collected = false;

    public Dot() {}

    public Dot(int x, int y, GameVisuals vis, Tile tile) {
        position.setLocation(x, y);
        display = new StaticEntityVisuals(this, PacmanSpriteManager.loadSprite("levels/sprites/Dot.png"));
        this.hitbox = new Rectangle(x + 3, y + 3, 2, 2);
        this.visuals = vis;
        this.tile = tile;
    }

    public Drawable getDisplay() {
        return display;
    }

    public boolean isCollected() {
        return collected;
    }

    public void setCollected(boolean collected) {
        this.collected = collected;
        this.display.setActive(!collected);
    }

    @Override
    public void update() {
        display.setActive(false);
        collected = true;
        ScoreManager.getInstance().addScore(10);
    }

    @Override
    public Rectangle2D getHitbox() {
        return hitbox;
    }
}
