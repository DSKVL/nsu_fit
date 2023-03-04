package ru.nsu.dskvl.pacman.entities;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

public abstract class Entity {
    protected Point2D position = new Point2D.Double(0, 0);
    protected Rectangle2D hitbox = new Rectangle2D.Double();

    public Point2D getPosition() {
        return position;
    }

    public Point getCenterPositionOnGrid() {
        return new Point((((int) position.getX() +  8) / 8) % 28,
                (((int) position.getY() + 8) / 8 - 3) % 31);
    }

    public Point2D getCenterCoordinates() {
        return new Point2D.Double(position.getX() + 8.0 , position.getY() + 8.0);
    }

    public Point2D getCurrentCellCenterCoordinates() {
        return new Point2D.Double((int) position.getX() + 8 - ((int) position.getX() + 8) % 8 + 4,
                                  (int) position.getY() + 8 - ((int) position.getY() + 8) % 8 + 4);
    }

    public abstract void update();

    public Rectangle2D getHitbox() {
        return hitbox;
    }
}