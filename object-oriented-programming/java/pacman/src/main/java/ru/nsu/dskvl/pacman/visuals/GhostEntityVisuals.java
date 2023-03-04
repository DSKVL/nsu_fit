package ru.nsu.dskvl.pacman.visuals;

import ru.nsu.dskvl.pacman.entities.GhostEntity;

import java.awt.*;

public class GhostEntityVisuals extends DynamicEntityVisuals {
    private final Animation frightenedAnimation;
    private final Animation eatenAnimation;

    public GhostEntityVisuals(GhostEntity state, Animation sprites,
                              Animation frightenedAnimation, Animation eatenAnimation) {
        super(state, sprites);
        this.frightenedAnimation = frightenedAnimation;
        this.eatenAnimation = eatenAnimation;
    }

    @Override
    public void draw(Graphics2D graphics2D) {
        var spr = switch (((GhostEntity) state).getMode()) {
            case CHASE, SCATTER -> sprites;
            case EATEN -> eatenAnimation;
            case FRIGHTEND -> frightenedAnimation;
        };

        graphics2D.drawImage(spr.getSprite(), ((int) state.getPosition().getX())*GameVisuals.scale,
                ((int) state.getPosition().getY())*GameVisuals.scale, null);
    }
}
