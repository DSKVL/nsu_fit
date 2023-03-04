package ru.nsu.dskvl.pacman.entities;

import java.awt.geom.Rectangle2D;

public abstract class MovableEntity extends Entity {
    protected double speed = basicSpeed;
    protected Direction direction = Direction.RIGHT;
    protected boolean movingOnNextUpdate = true;

    public boolean isMovingOnNextUpdate() {
        return movingOnNextUpdate;
    }

    public void setMovingOnNextUpdate(boolean movingOnNextUpdate) {
        this.movingOnNextUpdate = movingOnNextUpdate;
    }

    public double getSpeed() {
        if (movingOnNextUpdate) {
            return speed;
        }
        return 0;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public Direction getDirection() {
        return direction;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public static final double basicSpeed = 0.95;
    protected static final Rectangle2D tunnelLeft = new Rectangle2D.Double(0, 136, 8*5, 8);
    protected static final Rectangle2D tunnelRight = new Rectangle2D.Double(184, 136, 8*5, 8);
}
