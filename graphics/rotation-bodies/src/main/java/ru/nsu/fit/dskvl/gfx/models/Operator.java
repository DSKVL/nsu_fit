package ru.nsu.fit.dskvl.gfx.models;

public record Operator (double xx, double xy, double xz, double xw,
                        double yx, double yy, double yz, double yw,
                        double zx, double zy, double zz, double zw,
                        double wx, double wy, double wz, double ww){

    public Operator() {
        this(
                0, 0, 0, 0,
                0, 0, 0, 0,
                0, 0, 0, 0,
                0, 0, 0, 1
        );
    }

    public Operator add(Operator o) {
        return new Operator(
                xx + o.xx, xy + o.xy, xz + o.xz, xw + o.xw,
                yx + o.yx, yy + o.yy, yz + o.yz, yw + o.yw,
                zx + o.zx, zy + o.zy, zz + o.zz, zw + o.zw,
                wx + o.wx, wy + o.wy, wz + o.wz, ww + o.ww
        );
    }

    public Operator sub(Operator o) {
        return new Operator(
                xx - o.xx, xy - o.xy, xz - o.xz, xw - o.xw,
                yx - o.yx, yy - o.yy, yz - o.yz, yw - o.yw,
                zx - o.zx, zy - o.zy, zz - o.zz, zw - o.zw,
                wx - o.wx, wy - o.wy, wz - o.wz, ww - o.ww
        );
    }

    public Operator scale(double s) {
        return new Operator(
                s, 0, 0, 0,
                0, s, 0, 0,
                0, 0, s, 0,
                0, 0, 0,1);
    }

    public Operator multiply(double s) {
        return new Operator(
                s*xx, s*xy, s*xz, s*xw,
                s*yx, s*yy, s*yz, s*yw,
                s*zx, s*zy, s*zz, s*zw,
                s*wx, s*wy, s*wz, s*ww);
    }

    //o*this
    public Operator compose(Operator o) {
        return new Operator(
                o.xx * xx + o.xy * yx + o.xz * zx + o.xw * wx,
                o.xx * xy + o.xy * yy + o.xz * zy + o.xw * wy,
                o.xx * xz + o.xy * yz + o.xz * zz + o.xw * wz,
                o.xx * xw + o.xy * yw + o.xz * zw + o.xw * ww,

                o.yx * xx + o.yy * yx + o.yz * zx + o.yw * wx,
                o.yx * xy + o.yy * yy + o.yz * zy + o.yw * wy,
                o.yx * xz + o.yy * yz + o.yz * zz + o.yw * wz,
                o.yx * xw + o.yy * yw + o.yz * zw + o.yw * ww,

                o.zx * xx + o.zy * yx + o.zz * zx + o.zw * wx,
                o.zx * xy + o.zy * yy + o.zz * zy + o.zw * wy,
                o.zx * xz + o.zy * yz + o.zz * zz + o.zw * wz,
                o.zx * xw + o.zy * yw + o.zz * zw + o.zw * ww,

                o.wx * xx + o.wy * yx + o.wz * zx + o.ww * wx,
                o.wx * xy + o.wy * yy + o.wz * zy + o.ww * wy,
                o.wx * xz + o.wy * yz + o.wz * zz + o.ww * wz,
                o.wx * xw + o.wy * yw + o.wz * zw + o.ww * ww);
    }

    public static final Operator I = new Operator(
            1, 0, 0, 0,
            0, 1, 0, 0,
            0, 0, 1, 0,
            0, 0, 0, 1
    );

    public static Operator scaleOperator(double s) {
        return new Operator(
                s, 0, 0, 0,
                0, s, 0, 0,
                0, 0, s, 0,
                0, 0, 0, 1
        );
    }

    public static Operator rotateOpearator(Vec4 axis, double angle) {
        var v = axis.normalize();
        var cos = Math.cos(angle);
        var sin = Math.sin(angle);

        return new Operator(
                cos + (1-cos)*v.x()*v.x(), (1-cos)*v.x()*v.y() - sin*v.z(), (1-cos)*v.x()*v.z() + sin*v.y(), 0,
                (1-cos)*v.y()*v.x() + sin*v.z(), cos + (1-cos)*v.y()*v.y(), (1-cos)*v.y()*v.z() - sin*v.x(), 0,
                (1-cos)*v.z()*v.x() - sin*v.y(), (1-cos)*v.z()*v.y() + sin*v.x(), cos + (1-cos)*v.z()*v.z(), 0,
                0, 0, 0, 1
        );
    }

    public Operator normalize() {
        return new Operator(
                xx/ww, xy/ww, xz/ww, xw/ww,
                yx/ww, yy/ww, yz/ww, yw/ww,
                zx/ww, zy/ww, zz/ww, zw/ww,
                wx/ww, wy/ww, wz/ww, 1);
    }
}
