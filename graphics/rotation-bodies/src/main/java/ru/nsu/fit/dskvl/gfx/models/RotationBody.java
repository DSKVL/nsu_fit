package ru.nsu.fit.dskvl.gfx.models;


import java.util.ArrayList;
import java.util.Iterator;

public class RotationBody {
    private int M1 = 8;
    private int M2 = 5;
    private int circlesAccuracy = 10;
    private Spline frame;

    public int getM1() { return M1; }
    public void setM1(int m1) {
        M1 = m1;
    }
    public int getM2() {
        return M2 + 1;
    }
    public void setM2(int m2) {
        M2 = m2 - 1;
    }
    public int getCirclesAccuracy() { return circlesAccuracy; }
    public void setCirclesAccuracy(int circlesAccuracy) { this.circlesAccuracy = circlesAccuracy; }


    public Spline getFrame() {
        return frame;
    }

    public RotationBody(Spline frame) {
        this.frame = frame;
    }

    public class CircleIterator implements Iterator<Line3D> {

        Vec4 point;
        Operator rotate;
        int M;
        int currentM = 0;

        CircleIterator(int M, Vec4 p, Vec4 axile) {
            point = p;
            this.M = M;
            rotate = Operator.rotateOpearator(axile, 2*Math.PI/(M*M1));
        }
        @Override
        public boolean hasNext() {
            return currentM <= M*M1;
        }

        @Override
        public Line3D next() {
            var begin = point;
            point = point.apply(rotate);
            currentM++;
            var end = point;
            return new Line3D(begin, end);
        }
    }

    public class CirclesIterator implements Iterator<CircleIterator> {
        private int currentM = 0;
        private final ArrayList<Point2D> splinePoints;
        CirclesIterator() {
            splinePoints = frame.getSplinePoints();
        }

        @Override
        public boolean hasNext() {
            return currentM <= M2;
        }

        @Override
        public CircleIterator next() {
            var point = new Vec4(splinePoints.get((int) ((double) currentM/(M2)*(splinePoints.size()-1))));
            currentM++;
            return new CircleIterator(circlesAccuracy, point, Vec4.xAxis);
        }
    }

    public CirclesIterator circlesIterator() {
        return new CirclesIterator();
    }

    public class OutlineIterator implements Iterator<Line3D> {
        private final Operator rotate;
        private final Iterator<Line2D> iterator;

        OutlineIterator(Vec4 axile, double degree) {
            rotate = Operator.rotateOpearator(axile, degree);
            ArrayList<Line2D> splineLines = frame.getSplineLines();
            iterator = splineLines.iterator();
        }

        @Override
        public boolean hasNext() { return iterator.hasNext(); }

        @Override
        public Line3D next() {
            return new Line3D(iterator.next()).apply(rotate);
        }
    }

    public class OutlinesIterator implements Iterator<OutlineIterator> {
        private int currentM = 0;

        @Override
        public boolean hasNext() {
            return currentM <= M1;
        }

        @Override
        public OutlineIterator next() {
            return new OutlineIterator(Vec4.xAxis, ((double) currentM++ /M1)*2*Math.PI);
        }
    }

    public OutlinesIterator outlinesIterator() {
        return new OutlinesIterator();
    }
}
