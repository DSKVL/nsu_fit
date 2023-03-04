package ru.nsu.dskvl.pacman.entities;

public enum Direction { RIGHT,UP, LEFT, DOWN;

    public Direction opposite() {
        return switch (this) {
            case RIGHT -> LEFT;
            case UP -> DOWN;
            case LEFT -> RIGHT;
            case DOWN -> UP;
        };
    }
}
