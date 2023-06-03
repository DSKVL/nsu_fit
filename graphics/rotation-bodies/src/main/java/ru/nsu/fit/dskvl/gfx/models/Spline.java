package ru.nsu.fit.dskvl.gfx.models;

import java.util.ArrayList;

public class Spline {
    private final ArrayList<Point2D> controlPoints = new ArrayList<>();
    private final ArrayList<Point2D> splinePoints = new ArrayList<>();

    public ArrayList<Line2D> getSplineLines() {
        return splineLines;
    }

    private final ArrayList<Line2D> splineLines = new ArrayList<>();
    private int N = 100;
    private double stepN = 1.0/N;

    public Spline(int numberOfPoints) {
        for (int i = 0; i < numberOfPoints; i++)
            controlPoints.add(new Point2D(i * ((double) 1 / numberOfPoints) - 0.5 + 0.5/(numberOfPoints), 0.2));
        recalculate();
    }

    public void recalculate() {
        splinePoints.clear();
        splineLines.clear();
        for (int i = 0; i < controlPoints.size() - 3; i++) {
            var polynomial = calculatePolynomial(i);
            var segmentPoints = segmentPoints(polynomial);
            splinePoints.addAll(segmentPoints);
        }
        for (int i = 1; i < splinePoints.size(); i++) {
            splineLines.add(new Line2D(splinePoints.get(i-1),splinePoints.get(i)));
        }
    }

    public ArrayList<Point2D> segmentPoints(Point2D[] polynomial) {
        double t = 0;
        var segmentPoints = new ArrayList<Point2D>();
        for (int i = 0 ; i <= N; i++, t+=stepN) {
            Point2D A, B, C, D;
            A = new Point2D(polynomial[0]);
            B = new Point2D(polynomial[1]);
            C = new Point2D(polynomial[2]);
            D = new Point2D(polynomial[3]);
            var point = A.multiply(t*t*t)
                    .add(B.multiply(t*t))
                    .add(C.multiply(t))
                    .add(D);
            segmentPoints.add(point);
        }
        return segmentPoints;
    }

    public Spline() {
        this(0);
    }

    private Point2D[] calculatePolynomial(int index) {
        //A*t^3 + B*t^2 + C*t + D
        Point2D A, B, C, D;

        Point2D A0 = new Point2D(controlPoints.get(index));
        Point2D A1 = new Point2D(controlPoints.get(index+1));
        Point2D A2 = new Point2D(controlPoints.get(index+2));
        Point2D A3 = new Point2D(controlPoints.get(index+3));

        A = A0.multiply(-1)
                .add(A1.multiply(3))
                .add(A2.multiply(-3))
                .add(A3)
                .multiply(1.0/6);

        Point2D B0 = new Point2D(controlPoints.get(index));
        Point2D B1 = new Point2D(controlPoints.get(index+1));
        Point2D B2 = new Point2D(controlPoints.get(index+2));

        B = B0.multiply(3)
                .add(B1.multiply(-6))
                .add(B2.multiply(3))
                .multiply(1.0/6);

        Point2D C0 = new Point2D(controlPoints.get(index));
        Point2D C2 = new Point2D(controlPoints.get(index + 2));

        C = C0.multiply(-3)
                .add(C2.multiply(3))
                .multiply(1.0/6);

        Point2D D0 = new Point2D(controlPoints.get(index));
        Point2D D1 = new Point2D(controlPoints.get(index + 1));
        Point2D D2 = new Point2D(controlPoints.get(index + 2));

        D = D0.add(D1.multiply(4))
                .add(D2)
                .multiply(1.0/6);
        return new Point2D[]{A, B, C, D};
    }

    public ArrayList<Point2D> getControlPoints() {
        return controlPoints;
    }
    public ArrayList<Point2D> getSplinePoints() { return splinePoints; }

    public void setN(int n) {
        N = n;
        stepN = 1.0/N;
        recalculate();
    }
    public int getN() {
        return N;
    }
}
