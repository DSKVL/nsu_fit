package ru.nsu.fit.dskvl.snake;

import ru.nsu.fit.dskvl.snake.data.Point;
import ru.nsu.fit.dskvl.snake.data.SnakeDirection;

import java.util.*;

public class SnakeEntity {

    final Map<SnakeDirection, Point> dirToShift = new HashMap<>() {{
        put(SnakeDirection.UP, new Point(-1, 0));
        put(SnakeDirection.DOWN, new Point(1, 0));
        put(SnakeDirection.LEFT, new Point(0, -1));
        put(SnakeDirection.RIGHT, new Point(0, 1));
    }};

    public enum SnakeState {
        ALIVE,
        ZOMBIE
    }

    public int id;
    public int score;
    public int length;
    public SnakeDirection direction;
    public SnakeState state = SnakeState.ALIVE;
    public List<Point> body = new ArrayList<>();

    public SnakeEntity() {}

    public SnakeEntity(int id, Point head, int size) {
        this.id = id;
        length = size;
        direction = SnakeDirection.randomDirection();
        body.add(head);
        for (int i = 0; i < size - 1; i++)
            body.add(new Point(dirToShift.get(direction).opposite()));

    }

    public List<Point> getBody() {
        return new ArrayList<>(body);
    }

    public Point getHead() {
        return new Point(body.get(0));
    }

    public void addHead(int height, int width) {
        Point newHeadPos = body.get(0)
                .add(dirToShift.get(direction))
                .wrap(height, width);

        body.set(0, dirToShift.get(direction).opposite());
        body.add(0, newHeadPos);
    }

    public void removeTail() {
        body.remove(body.size() - 1);
    }

    public void steer(SnakeDirection dir) {
        if (!body.get(0).equals(dirToShift.get(dir)))
            direction = dir;
    }

    public void addScore(int score) { this.score += score; }
    public SnakeState getState() {
        return state;
    }
    public SnakeDirection getDirection() {
        return direction;
    }
}
