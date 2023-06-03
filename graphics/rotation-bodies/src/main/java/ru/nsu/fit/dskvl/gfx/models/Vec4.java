package ru.nsu.fit.dskvl.gfx.models;

public record Vec4 (double x , double y,
        double z, double w) {

    Vec4(Point2D point2D) {
        this(point2D.x, point2D.y, 0, 1);
    }

    public Vec4 apply(Operator op) {
        return new Vec4(
                op.xx()*x + op.xy()*y + op.xz()*z + op.xw()*w,
                op.yx()*x + op.yy()*y + op.yz()*z + op.yw()*w,
                op.zx()*x + op.zy()*y + op.zz()*z + op.zw()*w,
                op.wx()*x + op.wy()*y + op.wz()*z + op.ww()*w);
    }

    public Vec4 normalize() { return new Vec4(x/ length(), y/length(), z/ length(), 1); }
    public double length() { return Math.sqrt(x*x + y*y + z*z); }
    public static Vec4 xAxis = new Vec4(1, 0, 0, 1);
    public static Vec4 zAxis = new Vec4(0, 0, 1, 1);
}
