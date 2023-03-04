package ru.nsu.dskvl.pacman.visuals;

import ru.nsu.dskvl.pacman.engine.LivesManager;

import java.awt.*;

public class LivesManagerVisuals extends Drawable {
    Image icon;
    LivesManager livesManager = LivesManager.getInstance();

    public LivesManagerVisuals(Image image) {
        icon = image;
    }

    @Override
    public void draw(Graphics2D graphics2D) {
        for (int i = 0; i < livesManager.getLives(); i++) {
            graphics2D.drawImage(icon, (1 + i) * 16 * GameVisuals.scale, 272 * GameVisuals.scale, null);
        }
    }
}
