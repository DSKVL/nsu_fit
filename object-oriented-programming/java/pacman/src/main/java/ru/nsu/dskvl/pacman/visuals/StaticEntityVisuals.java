package ru.nsu.dskvl.pacman.visuals;

import ru.nsu.dskvl.pacman.entities.Entity;

import java.awt.*;

public class StaticEntityVisuals extends Drawable {
    Entity entity;
    Image sprite;

    public StaticEntityVisuals(Entity entity, Image sprite) {
        this.entity = entity;
        this.sprite = sprite;
    }

    @Override
    public void draw(Graphics2D graphics2D) {
        if (active) {
            graphics2D.drawImage(sprite, ((int) entity.getPosition().getX()) * GameVisuals.scale,
                    ((int) entity.getPosition().getY()) * GameVisuals.scale, null);
        }
    }
}
