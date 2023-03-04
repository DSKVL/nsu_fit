package ru.nsu.dskvl.pacman.visuals;

import ru.nsu.dskvl.pacman.entities.Entity;

import java.awt.*;

public class DynamicEntityVisuals extends Drawable {
    protected final Animation sprites;
    protected Entity state;

    public DynamicEntityVisuals(Entity state, Animation sprites) {
        this.state = state;
        this.sprites = sprites;
    }

    @Override
    public void draw(Graphics2D graphics2D) {
        if (active) {
            graphics2D.drawImage(sprites.getSprite(), ((int) state.getPosition().getX()) * GameVisuals.scale,
                    ((int) state.getPosition().getY()) * GameVisuals.scale, null);
        }
    }
}
