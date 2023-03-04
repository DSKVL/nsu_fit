package ru.nsu.fit.dskvl.snake.data;

import java.util.Objects;
import java.util.Vector;

public class Point {
    public int x;
    public int y;

    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Point(Point other) {
        this.x = other.x;
        this.y = other.y;
    }

    public Point add(Point p) {
        return new Point(this.x + p.x, this.y + p.y);
    }

    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Point that = (Point) o;
        return (x == that.x && y == that.y);
    }

    public int hashCode() {
        return Objects.hash(this.x, this.y);
    }

    @Override
    public String toString() {
        return "(" + this.x + ", " + this.y + ")";
    }

    public Point opposite() {
        return new Point(-this.x, -this.y);
    }

    public Point wrap(int height, int width) {
        int w_x = (this.x < 0) ? ( height - (Math.abs(this.x) % height) ) : (this.x % height);
        int w_y = (this.y < 0) ? ( width - (Math.abs(this.y) % width) ) : (this.y % width);
        return new Point(w_x, w_y);
    }

    public Vector<Point> getSubPoints() {
        Point start = new Point(this);
        Vector<Point> path = new Vector<>();

        if (start.x == 0) {
            if (start.y > 0) {
                for (int i = 0; i < start.y; i++) {
                    path.add(new Point(0, 1));
                }
            } else {
                for (int i = 0; i < -start.y; i++) {
                    path.add(new Point(0, -1));
                }
            }
        } else {
            if (start.x > 0) {
                for (int i = 0; i < start.x; i++) {
                    path.add(new Point(-1, 0));
                }
            } else {
                for (int i = 0; i < -start.x; i++) {
                    path.add(new Point(-1, 0));
                }
            }
        }

        return path;
    }
}
