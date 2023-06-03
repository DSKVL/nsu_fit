package ru.nsu.fit.dskvl.gfx.models;

public class Line3D {
    public Vec4 begin, end;
    public Line3D(Vec4 begin, Vec4 end) {
        this.begin = begin;
        this.end = end;
    }

    public Line3D(Line2D line2D) {
        begin = new Vec4(line2D.begin);
        end = new Vec4(line2D.end);
    }

    public Line3D(Line3D line3D) {
        begin = line3D.begin;
        end = line3D.end;
    }

    public Line3D apply(Operator operator) {
        begin = begin.apply(operator);
        end = end.apply(operator);
        return this;
    }
}
