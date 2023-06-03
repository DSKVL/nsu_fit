package ru.nsu.fit.dskvl.gfx.models;

public class Line2D {
    public Point2D begin, end;

    public Line2D(Point2D begin, Point2D end) {
        this.begin = begin;
        this.end = end;
    }

    public Line3D apply(Operator operator) {
        return new Line3D(new Vec4(begin).apply(operator), new Vec4(end).apply(operator));
    }
}
