package ru.nsu.dskvl.pacman.visuals;

import java.awt.*;

public abstract class Drawable {
    protected boolean active = true;

    public abstract void draw(Graphics2D graphics2D);

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
