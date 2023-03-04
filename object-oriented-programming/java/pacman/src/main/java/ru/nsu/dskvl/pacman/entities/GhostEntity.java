package ru.nsu.dskvl.pacman.entities;

import ru.nsu.dskvl.pacman.engine.LevelInfo;
import ru.nsu.dskvl.pacman.engine.ModesManager;
import ru.nsu.dskvl.pacman.engine.ScoreManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

interface DesiredCellBehaviour {
    Point2D getDesiredCellCoords(PacmanEntity pacman);
}

public class GhostEntity extends MovableEntity implements ActionListener {
    private GhostAI ghostAI;
    private GhostMode mode = GhostMode.SCATTER;
    private int[] delays = new int[]{7, 20, 7, 20, 5, 20, 5, 9999999};
    private int currentDelay = 0;
    private int secondsInState = 0;
    private int initialDelay;
    private boolean clockPaused = false;
    private Point2D initialPosition = new Point2D.Double();
    private Timer clock = new Timer(1000, this);

    public GhostEntity(String name, PacmanLevel level, PacmanEntity pacman, GhostEntity additionalEntity) {

        initialDelay = 0;

        switch (name) {
            case "Blinky" -> {
                position.setLocation(initialBlinkyPosition);
                initialPosition.setLocation(initialBlinkyPosition);
                ghostAI = new GhostAI(this, level, pacman, new BlinkyDesiredCell(), new ScatterDesiredCell(upperRightCorner));
            }
            case "Inky" -> {
                position.setLocation(initialInkyPosition);
                initialPosition.setLocation(initialInkyPosition);
                ghostAI = new GhostAI(this, level, pacman, new InkyDesiredCell(additionalEntity), new ScatterDesiredCell(lowerRightCorner));
                movingOnNextUpdate = false;
                initialDelay = 3;
            }
            case "Pinky" -> {
                position.setLocation(initialPinkyPosition);
                initialPosition.setLocation(initialPinkyPosition);
                ghostAI = new GhostAI(this, level, pacman, new PinkyDesiredCell(), new ScatterDesiredCell(upperLeftCorner));
                movingOnNextUpdate = false;
                initialDelay = 6;
            }
            case "Clyde" -> {
                position.setLocation(initialClydePosition);
                initialPosition.setLocation(initialClydePosition);
                ScatterDesiredCell clydeScatter = new ScatterDesiredCell(lowerLeftCorner);
                ghostAI = new GhostAI(this, level, pacman, new ClydeDesiredCell(clydeScatter, this), clydeScatter);
                movingOnNextUpdate = false;
                initialDelay = 9;
            }
        }
        hitbox.setRect(position.getX() + 2, position.getY() + 1,  13, 13);
    }

    public void start() {
        clock.start();
    }

    public void reset() {
        position.setLocation(initialPosition);
        hitbox.setRect(position.getX() + 2, position.getY() + 1,  13, 13);
        setSpeed(LevelInfo.getGhostNormalSpeed());
        currentDelay = 0;
        secondsInState = 0;
        movingOnNextUpdate = false;
        clockPaused = false;
        mode = GhostMode.SCATTER;
        ghostAI.reset();
    }

    public void setClockPaused(boolean state) { clockPaused = state; }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        if (!movingOnNextUpdate) {
            if (secondsInState == initialDelay) {
                movingOnNextUpdate = true;
            }
        }
        secondsInState++;

        if (!clockPaused) {
            if (delays[currentDelay] == secondsInState) {
                //TODO reverse direction
                mode = (currentDelay % 2 == 0) ? GhostMode.CHASE : GhostMode.SCATTER;
                currentDelay++;
                secondsInState = 0;
            }
        } else {
            if (secondsInState == LevelInfo.getFrightTime()) {
                clockPaused = false;
                secondsInState = 0;
                currentDelay = 0;
                mode = GhostMode.SCATTER;
            }
        }
    }

    public GhostMode getMode() {
        return mode;
    }

    public void setMode(GhostMode mode) {
        this.mode = mode;
    }

    public void eaten() {
        mode = GhostMode.EATEN;
        clockPaused = true;
        int reward = ModesManager.getInstance().getEatenReward();
        ScoreManager.getInstance().addScore(reward);
        ModesManager.getInstance().setEatenReward(2 * reward);

        ghostAI.calculateNextPosition();
    }

    @Override
    public void update() {
        if (!movingOnNextUpdate) {
            return;
        }

        if (tunnelLeft.contains(getCenterCoordinates()) || tunnelRight.contains(getCenterCoordinates())) {
            setSpeed(LevelInfo.getGhostTunnelSpeed());
        } else if (getMode() == GhostMode.FRIGHTEND) {
            setSpeed(LevelInfo.getGhostFrightSpeed());
        } else {
            setSpeed(LevelInfo.getGhostNormalSpeed());
        }

        if (mode == GhostMode.EATEN && getCenterPositionOnGrid().equals(revivalCellCoordinates)) {
            mode = GhostMode.CHASE;
            clock.start();
            secondsInState = 0;
            currentDelay = 0;
        }

        switch (ghostAI.getDirection()) {

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

    private static final Point initialBlinkyPosition = new Point(104, 108);
    private static final Point initialInkyPosition = new Point(88, 132);
    private static final Point initialPinkyPosition = new Point(104, 132);
    private static final Point initialClydePosition = new Point(120, 132);

    private static final Point upperLeftCorner = new Point(12, 36);
    private static final Point upperRightCorner = new Point(212, 36);
    private static final Point lowerLeftCorner = new Point(12, 260);
    private static final Point lowerRightCorner = new Point(212, 260);
    private static final Point revivalCellCoordinates = new Point(13, 11);
}

class GhostAI {
    private final GhostEntity state;
    private final PacmanLevel level;
    private final PacmanEntity pacman;

    private final DesiredCellBehaviour desiredCellBehaviour;
    private final DesiredCellBehaviour scatterCellBehaviour;
    private final DesiredCellBehaviour eatenCellBehaviour = new ScatterDesiredCell(revivalPoint);

    private Point nextGridPosition;
    private Direction nextDirection;
    private boolean initialState = true;

    public GhostAI(GhostEntity state, PacmanLevel level, PacmanEntity pacman, DesiredCellBehaviour desiredCellBehaviour,
                   DesiredCellBehaviour scatterCellBehaviour) {
        this.state = state;
        this.level = level;
        this.pacman = pacman;
        this.desiredCellBehaviour = desiredCellBehaviour;
        this.scatterCellBehaviour = scatterCellBehaviour;
        this.nextGridPosition = state.getCenterPositionOnGrid();
        this.nextGridPosition.x++;
        this.nextDirection = Direction.RIGHT;
    }

    void calculateNextPosition() {
        Point gridPosition = state.getCenterPositionOnGrid();

        //List of all possible directions
        List<Direction> possibleNextDirections = new ArrayList<>(Arrays.asList(Direction.UP, Direction.LEFT,
                Direction.DOWN, Direction.RIGHT));

        //Filter opposite
        possibleNextDirections.remove(state.getDirection().opposite());

        //Filter illegal zones
        if ((gridPosition.y == 11 && gridPosition.x >= 11 && gridPosition.x <= 16) ||
                (gridPosition.y == 23 && gridPosition.x >= 11 && gridPosition.x <= 16))
            possibleNextDirections.remove(Direction.UP);

        //Filter directions that lead to walls
        if (!((Tile) level.getEntityAt((gridPosition.x+1)%28, gridPosition.y)).isNotAWall())
            possibleNextDirections.remove(Direction.RIGHT);

        int nextX = (gridPosition.x-1)%28 >= 0 ? (gridPosition.x-1)%28 : 27;
        if (!((Tile) level.getEntityAt(nextX, gridPosition.y)).isNotAWall())
            possibleNextDirections.remove(Direction.LEFT);
        if (!((Tile) level.getEntityAt(gridPosition.x, (gridPosition.y+1)%31)).isNotAWall())
            possibleNextDirections.remove(Direction.DOWN);

        int nextY = (gridPosition.y-1)%31 >= 0 ? (gridPosition.y-1)%31 : 30;
        if (!((Tile) level.getEntityAt(gridPosition.x%28, nextY)).isNotAWall())
            possibleNextDirections.remove(Direction.UP);

        //Choose minimal possible distance for test cell
        double minDistance = 9999999;
        Point2D minDistanceCellCoordinates = level.getCellCenterCoordinatesAt(gridPosition);
        Point2D desiredCellCoords = getDesiredCellCoords();

        for (var dir : possibleNextDirections) {
            Point2D testCellCoord = switch (dir) {

                case RIGHT -> level.getCellCenterCoordinatesAt((gridPosition.x + 1)%28, gridPosition.y);
                case UP -> level.getCellCenterCoordinatesAt(gridPosition.x, nextY);
                case LEFT -> level.getCellCenterCoordinatesAt(nextX, gridPosition.y);
                case DOWN -> level.getCellCenterCoordinatesAt(gridPosition.x, (gridPosition.y + 1)%31);
            };

            double distance = testCellCoord.distance(desiredCellCoords);
            if (distance < minDistance) {
                nextDirection = dir;
                minDistance = distance;
                minDistanceCellCoordinates = testCellCoord;
            }
        }

        nextGridPosition.move(((int)minDistanceCellCoordinates.getX())/8%28,
                (((int)minDistanceCellCoordinates.getY())/8 - 3)%31);
    }

    Direction getDirection() {
        Point gridPosition = state.getCenterPositionOnGrid();

        //Check if ghost changed current cell
        if (!gridPosition.equals(nextGridPosition)) {
            Point2D cellCenterCoordinates = level.getCellCenterCoordinatesAt(gridPosition);
            Point2D pacmanCenterCoordinates = state.getCenterCoordinates();

            if (switch (state.getDirection()) {
                    case RIGHT -> pacmanCenterCoordinates.getX() > cellCenterCoordinates.getX();
                    case UP -> pacmanCenterCoordinates.getY() < cellCenterCoordinates.getY();
                    case LEFT -> pacmanCenterCoordinates.getX() < cellCenterCoordinates.getX();
                    case DOWN -> pacmanCenterCoordinates.getY() > cellCenterCoordinates.getY();
                }) {
                    state.setDirection(nextDirection);
                    return nextDirection;
            }
            return state.getDirection();
        }

        calculateNextPosition();

        return state.getDirection();
    }

    public void reset() {
        this.nextGridPosition = state.getCenterPositionOnGrid();
        this.nextGridPosition.x++;
        this.nextDirection = Direction.RIGHT;
    }

    Point2D getDesiredCellCoords() {
//        if (initialState) {
//            if (state.getCenterCoordinates().equals(revivalPoint)) {
//                initialState =false;
//            } else {
//                return eatenCellBehaviour.getDesiredCellCoords(pacman);
//            }
//
//        }
        return switch (state.getMode()) {
            case CHASE -> desiredCellBehaviour.getDesiredCellCoords(pacman);
            case SCATTER, FRIGHTEND -> scatterCellBehaviour.getDesiredCellCoords(pacman);
            case EATEN -> eatenCellBehaviour.getDesiredCellCoords(pacman);
        };
    }

    private final static Point revivalPoint = new Point(104, 108);
}

class BlinkyDesiredCell implements DesiredCellBehaviour {
    @Override
    public Point2D getDesiredCellCoords(PacmanEntity pacman) {
        return pacman.getCurrentCellCenterCoordinates();
    }
}

class InkyDesiredCell implements DesiredCellBehaviour {
    GhostEntity blinky;

    InkyDesiredCell(GhostEntity blinky) {
        this.blinky = blinky;
    }

    @Override
    public Point2D getDesiredCellCoords(PacmanEntity pacman) {
        Point2D destination = pacman.getCurrentCellCenterCoordinates();
        switch (pacman.getDirection()) {
            case UP -> destination.setLocation(destination.getX(), destination.getY() - 2 * 16);
            case DOWN -> destination.setLocation(destination.getX(), destination.getY() + 2 * 16);
            case LEFT -> destination.setLocation(destination.getX() - 2 * 16, destination.getY());
            case RIGHT -> destination.setLocation(destination.getX() + 2 * 16, destination.getY());
        }

        Point2D blinkyPos = blinky.getCurrentCellCenterCoordinates();
        destination.setLocation(destination.getX() + 2 * (destination.getX() - blinkyPos.getX()),
                destination.getY() + 2 * (destination.getY() - blinkyPos.getY()));
        return destination;
    }
}

class PinkyDesiredCell implements DesiredCellBehaviour {
    private static final int cellSize = 8;

    @Override
    public Point2D getDesiredCellCoords(PacmanEntity pacman) {
        Point2D destination = pacman.getCurrentCellCenterCoordinates();
        switch (pacman.getDirection()) {

            case UP -> destination.setLocation(destination.getX() - 4*cellSize, destination.getY() - 4*cellSize);
            case DOWN -> destination.setLocation(destination.getX(), destination.getY() + 4*cellSize);
            case LEFT -> destination.setLocation(destination.getX() - 4*cellSize, destination.getY());
            case RIGHT -> destination.setLocation(destination.getX() + 4*cellSize, destination.getY());

        }
        return destination;
    }
}

class ClydeDesiredCell implements DesiredCellBehaviour {
    ScatterDesiredCell scatterDesiredCell;
    GhostEntity clyde;

    public ClydeDesiredCell(ScatterDesiredCell scatterDesiredCell, GhostEntity clyde) {
        this.scatterDesiredCell = scatterDesiredCell;
        this.clyde = clyde;
    }

    @Override
    public Point2D getDesiredCellCoords(PacmanEntity pacman) {
        if (pacman.getPosition().distance(clyde.getPosition()) > 8 * 16) {
            return pacman.getCurrentCellCenterCoordinates();
        } else {
            return scatterDesiredCell.getDesiredCellCoords(pacman);
        }
    }
}

class ScatterDesiredCell implements DesiredCellBehaviour {
    Point cornerCoords;

    ScatterDesiredCell(Point cornerCoords) {
        this.cornerCoords = cornerCoords;
    }

    @Override
    public Point getDesiredCellCoords(PacmanEntity pacman) {
        return cornerCoords;
    }
}