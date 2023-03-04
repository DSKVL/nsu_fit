package ru.nsu.dskvl.pacman.engine;

import ru.nsu.dskvl.pacman.CurrentKeyInput;
import ru.nsu.dskvl.pacman.entities.*;

import java.awt.geom.Rectangle2D;

public class PacmanEngine {
    private PacmanEntity player;
    private GhostEntity[] ghosts;
    private PacmanLevel level;
    private final static CollisionDetector collisions = new CollisionDetector();

    public PacmanEngine() {}

    public void setPacmanEntity(PacmanEntity player) {
        this.player = player;
    }

    public void setLevel(PacmanLevel level) {
        this.level = level;
        collisions.setEnvironment(level);
    }

    public void setGhosts(GhostEntity[] ghosts) {
        this.ghosts = ghosts;
    }

    public void update(CurrentKeyInput input) {
        player.HandleInput(input);

        Dot dot = (Dot) ((Tile) level.getEntityAt(player.getCenterPositionOnGrid())).getObject();

        if (dot != null && !dot.isCollected() && collisions.collide(player, dot)) {
            dot.update();
            level.decreaseDotsleft();
            if (level.getDotsLeft() == 0) {
                nextLevel();
            }
        }

        for (var ghost : ghosts) {
            if (collisions.collide(player, ghost)) {
                if (player.getPlayerMode() == PacmanMode.NORMAL) {
                    if (ghost.getMode() == GhostMode.CHASE ||
                            ghost.getMode() == GhostMode.SCATTER) {

                        LivesManager.getInstance().decrementLives();
                        resetEntities();
                    }
                } else {
                    if (ghost.getMode() == GhostMode.FRIGHTEND) {
                        ghost.eaten();
                    }
                }
            }
        }

        switch (collisions.collidesOnUpdate(player)) {
            case NONE -> {}
            case TOTAL -> player.setMovingOnNextUpdate(false);
            case RIGHTFREE -> {
                player.setMovingOnNextUpdate(false);
                player.move(Direction.RIGHT);
            }
            case TOPFREE -> {
                player.setMovingOnNextUpdate(false);
                player.move(Direction.UP);
            }
            case LEFTFREE -> {
                player.setMovingOnNextUpdate(false);
                player.move(Direction.LEFT);
            }
            case BOTTOMFREE -> {
                player.setMovingOnNextUpdate(false);
                player.move(Direction.DOWN);
            }
        }

        for (var gh : ghosts) {
            gh.update();
        }
        player.update();
    }

    public void start() {
        resetEntities();

        for (var gh : ghosts) {
            gh.start();
        }
    }

    public void resetLevel() {
        level.reset();
    }

    public void resetEntities() {
        for (var gh : ghosts) {

            gh.reset();
        }
        player.reset();
    }

    public void nextLevel() {
        LevelInfo.increaseLevel();
        resetEntities();
        resetLevel();
    }

    public void gameover() {
        resetEntities();
        resetLevel();
        LevelInfo.setCurrentLevel(0);
        LivesManager.getInstance().setLives(5);
        ScoreManager.getInstance().setCurrentScore(0);
    }
}

class CollisionDetector {
    private PacmanLevel env;

    public void setEnvironment(PacmanLevel environment) {
        this.env = environment;
    }


    enum WallCollisionType {NONE, TOTAL, RIGHTFREE, TOPFREE, LEFTFREE, BOTTOMFREE}
    WallCollisionType collidesOnUpdate(MovableEntity entity) {
        Rectangle2D nextEntityHitbox = new Rectangle2D.Double();
        nextEntityHitbox.setRect(entity.getHitbox());

        Tile nextTile1;
        Tile nextTile2;
        Tile nextTile3;

        switch (entity.getDirection()) {

            case RIGHT -> {
                double rightBoundaryAbsolute = nextEntityHitbox.getX() + entity.getSpeed() + nextEntityHitbox.getWidth();
                int nextTileXCoordiante = (int) (rightBoundaryAbsolute / 8);
                int nextTileYCoordinate = (int) entity.getCenterPositionOnGrid().getY();

                nextEntityHitbox.setRect(nextEntityHitbox.getX() + entity.getSpeed(), nextEntityHitbox.getY(),
                        nextEntityHitbox.getWidth(), nextEntityHitbox.getHeight());

                nextTile1 = (Tile) env.getEntityAt(nextTileXCoordiante, nextTileYCoordinate - 1);
                nextTile2 = (Tile) env.getEntityAt(nextTileXCoordiante, nextTileYCoordinate);
                nextTile3 = (Tile) env.getEntityAt(nextTileXCoordiante, nextTileYCoordinate + 1);

                if (nextEntityHitbox.intersects(nextTile2.getHitbox())) {
                    return WallCollisionType.TOTAL;
                }

                if (nextEntityHitbox.intersects(nextTile1.getHitbox())) {
                    return WallCollisionType.BOTTOMFREE;
                }

                if (nextEntityHitbox.intersects(nextTile3.getHitbox())) {
                    return WallCollisionType.TOPFREE;
                }

            }
            case UP -> {
                double upperBoundaryAbsolute = nextEntityHitbox.getY() - entity.getSpeed();
                int nextTileXCoordiante = (int) entity.getCenterPositionOnGrid().getX();
                int nextTileYCoordinate = (int) (upperBoundaryAbsolute / 8) - 3;

                nextEntityHitbox.setRect(nextEntityHitbox.getX(), nextEntityHitbox.getY() - entity.getSpeed(),
                        nextEntityHitbox.getWidth(), nextEntityHitbox.getHeight());

                nextTile1 = (Tile) env.getEntityAt(nextTileXCoordiante - 1, nextTileYCoordinate);
                nextTile2 = (Tile) env.getEntityAt(nextTileXCoordiante, nextTileYCoordinate);
                nextTile3 = (Tile) env.getEntityAt(nextTileXCoordiante + 1, nextTileYCoordinate);

                if (nextEntityHitbox.intersects(nextTile2.getHitbox())) {
                    return WallCollisionType.TOTAL;
                }

                if (nextEntityHitbox.intersects(nextTile1.getHitbox())) {
                    return WallCollisionType.RIGHTFREE;
                }

                if (nextEntityHitbox.intersects(nextTile3.getHitbox())) {
                    return WallCollisionType.LEFTFREE;
                }
            }
            case LEFT -> {
                double lefBoundaryAbsolute = nextEntityHitbox.getX() - entity.getSpeed();
                int nextTileXCoordiante = (int) (lefBoundaryAbsolute / 8);
                int nextTileYCoordinate = (int) entity.getCenterPositionOnGrid().getY();

                nextEntityHitbox.setRect(nextEntityHitbox.getX() - entity.getSpeed(), nextEntityHitbox.getY(),
                        nextEntityHitbox.getWidth(), nextEntityHitbox.getHeight());

                nextTile1 = (Tile) env.getEntityAt(nextTileXCoordiante, nextTileYCoordinate - 1);
                nextTile2 = (Tile) env.getEntityAt(nextTileXCoordiante, nextTileYCoordinate);
                nextTile3 = (Tile) env.getEntityAt(nextTileXCoordiante, nextTileYCoordinate + 1);

                if (nextEntityHitbox.intersects(nextTile2.getHitbox())) {
                    return WallCollisionType.TOTAL;
                }

                if (nextEntityHitbox.intersects(nextTile1.getHitbox())) {
                    return WallCollisionType.BOTTOMFREE;
                }

                if (nextEntityHitbox.intersects(nextTile3.getHitbox())) {
                    return WallCollisionType.TOPFREE;
                }
            }
            default -> {
                double lowerBoundaryAbsolute = nextEntityHitbox.getY() + entity.getSpeed() + nextEntityHitbox.getHeight();
                int nextTileXCoordiante = (int) entity.getCenterPositionOnGrid().getX();
                int nextTileYCoordinate = (int) (lowerBoundaryAbsolute / 8) - 3;

                nextEntityHitbox.setRect(nextEntityHitbox.getX(), nextEntityHitbox.getY() + entity.getSpeed(),
                        nextEntityHitbox.getWidth(), nextEntityHitbox.getHeight());

                nextTile1 = (Tile) env.getEntityAt(nextTileXCoordiante - 1 , nextTileYCoordinate);
                nextTile2 = (Tile) env.getEntityAt(nextTileXCoordiante, nextTileYCoordinate);
                nextTile3 = (Tile) env.getEntityAt(nextTileXCoordiante + 1, nextTileYCoordinate);

                if (nextEntityHitbox.intersects(nextTile2.getHitbox())) {
                    return WallCollisionType.TOTAL;
                }

                if (nextEntityHitbox.intersects(nextTile1.getHitbox())) {
                    return WallCollisionType.RIGHTFREE;
                }

                if (nextEntityHitbox.intersects(nextTile3.getHitbox())) {
                    return WallCollisionType.LEFTFREE;
                }
            }
        }
        return WallCollisionType.NONE;
    }

    boolean collide(Entity ent1, Entity ent2) {
        return ent1.getHitbox().intersects(ent2.getHitbox());
    }
}