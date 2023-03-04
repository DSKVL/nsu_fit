package ru.nsu.dskvl.pacman.entities;

import ru.nsu.dskvl.pacman.BadGameInitializationException;
import ru.nsu.dskvl.pacman.PacmanSpriteManager;
import ru.nsu.dskvl.pacman.visuals.Drawable;
import ru.nsu.dskvl.pacman.visuals.StaticEntityVisuals;

import java.awt.*;

public class Tile extends Entity {
    protected final Rectangle hitbox;
    protected Drawable display;
    protected Entity object = null;

    protected boolean notAWall = false;

    public Tile(int x, int y, char type) throws BadGameInitializationException {
        position.setLocation(x, y);

        hitbox = PacmanSpriteManager.loadHitbox(type);
        hitbox.x += x;
        hitbox.y += y;

        display = new StaticEntityVisuals(this, PacmanSpriteManager.loadWallSprite(type));
        if (type == 'O' || type == '.' || type == ' ')
            notAWall = true;
    }

    public Entity getObject() {
        return object;
    }

    public void setObject(Entity object) {
        this.object = object;
    }

    public boolean isNotAWall() {
        return notAWall;
    }

    public void setNotAWall(boolean notAWall) {
        this.notAWall = notAWall;
    }

    public Drawable getDisplay() {
        return display;
    }

    @Override
    public void update() {
    }

    @Override
    public Rectangle getHitbox() {
        return hitbox;
    }
}
