package ru.nsu.dskvl.pacman.entities;

import ru.nsu.dskvl.pacman.CurrentKeyInput;
import ru.nsu.dskvl.pacman.engine.LevelInfo;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

public class PacmanEntity extends MovableEntity {
    PacmanMode pacmanMode = PacmanMode.NORMAL;

    public PacmanEntity() {
        position.setLocation(initialPosition);
        hitbox.setRect(initialHitbox);
    }

    public PacmanMode getPlayerMode() {
        return pacmanMode;
    }

    public void setPlayerMode(PacmanMode pacmanMode) {
        this.pacmanMode = pacmanMode;
    }

    public void HandleInput(CurrentKeyInput input) {
        setMovingOnNextUpdate(true);
        setDirection(switch (input) {
            case UP -> Direction.UP;
            case DOWN -> Direction.DOWN;
            case LEFT -> Direction.LEFT;
            case RIGHT -> Direction.RIGHT;
            default -> getDirection();
        });
    }

    public void reset() {
        position.setLocation(initialPosition);
        hitbox.setRect(initialHitbox);
        setSpeed(LevelInfo.getPacmanNormalSpeed());
    }

    @Override
    public void update() {
        if (movingOnNextUpdate) {
            move(direction);
        }
    }

    public void move(Direction direction) {
        switch (direction) {

            case RIGHT -> {
                position.setLocation(position.getX() + speed, position.getY());
                hitbox.setRect(hitbox.getX() + speed, hitbox.getY(), hitbox.getWidth(), hitbox.getHeight());
                if (position.getX() >= 224) {
                    position.setLocation(0, position.getY());
                    hitbox.setRect(2, hitbox.getY(), hitbox.getWidth(), hitbox.getHeight());
                }
            }
            case UP -> {
                position.setLocation(position.getX(), position.getY() - speed);
                hitbox.setRect(hitbox.getX(), hitbox.getY() - speed, hitbox.getWidth(), hitbox.getHeight());
            }
            case LEFT -> {
                position.setLocation(position.getX() - speed, position.getY());
                hitbox.setRect(hitbox.getX() - speed, hitbox.getY(), hitbox.getWidth(), hitbox.getHeight());
                if (position.getX() < 0) {
                    position.setLocation(223, position.getY());
                    hitbox.setRect(223 + 2, hitbox.getY(), hitbox.getWidth(), hitbox.getHeight());
                }
            }
            case DOWN -> {
                position.setLocation(position.getX(), position.getY() + speed);
                hitbox.setRect(hitbox.getX(), hitbox.getY() + speed, hitbox.getWidth(), hitbox.getHeight());
            }
        }
    }

    private static final Point2D.Double initialPosition = new Point2D.Double(104, 204);
    private static final Rectangle2D initialHitbox = new Rectangle2D.Double(104 + 2, 204 + 1, 13, 13);

}

