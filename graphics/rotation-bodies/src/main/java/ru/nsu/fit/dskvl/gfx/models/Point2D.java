package ru.nsu.fit.dskvl.gfx.models;

public class Point2D {
    public double x = 0;
    public double y = 0;

    public Point2D(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public Point2D(Point2D other) {
        this.x = other.x;
        this.y = other.y;
    }

    public Point2D multiply(double scalar) {
        this.x *= scalar;
        this.y *= scalar;
        return this;
    }

    public Point2D add(Point2D other) {
        this.x += other.x;
        this.y += other.y;
        return this;
    }

    public Point2D apply(Operator operator) {
        //todo
        return this;
    }
}
