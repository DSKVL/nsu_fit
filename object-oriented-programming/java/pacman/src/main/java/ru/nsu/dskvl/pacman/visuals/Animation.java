package ru.nsu.dskvl.pacman.visuals;

import ru.nsu.dskvl.pacman.entities.Entity;
import ru.nsu.dskvl.pacman.entities.MovableEntity;

import java.awt.*;

public class Animation {
    private final Entity entity;
    private final Image[] sprites;
    private final int totalSprite;
    private int currentFrame = -1;

    public Animation(Image[] sprites, Entity entity, int totalSprites) {
        this.sprites = sprites;
        this.entity = entity;
        this.totalSprite = totalSprites;
    }

    public Image getSprite() {
        if (entity instanceof MovableEntity) {
            if (((MovableEntity) entity).isMovingOnNextUpdate()) {
                currentFrame++;
            }
        } else {
            currentFrame++;
        }

        int currentSprite = (currentFrame / 6) % totalSprite;
        if (entity instanceof MovableEntity)
            return switch (((MovableEntity) entity).getDirection()) {
                case RIGHT -> sprites[currentSprite];
                case UP -> sprites[totalSprite + currentSprite];
                case LEFT -> sprites[totalSprite * 2 + currentSprite];
                case DOWN -> sprites[totalSprite * 3 + currentSprite];
            };
        else return sprites[currentSprite];
    }
}
