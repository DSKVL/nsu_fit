package ru.nsu.fit.dskvl.gfx.views;

import ru.nsu.fit.dskvl.gfx.models.Line3D;
import ru.nsu.fit.dskvl.gfx.models.Operator;
import ru.nsu.fit.dskvl.gfx.models.RotationBody;
import ru.nsu.fit.dskvl.gfx.models.Vec4;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Point2D;

public class BodyViewer extends JComponent {
    private final RotationBody body;
    private Operator ModelOperator = Operator.I;
    private final Operator ViewOperator = new Operator(
            1, 0, 0, 0,
            0, 1, 0, 0,
            0, 0, 1, 10,
            0, 0, 0, 1
    );
    private double farClip = 8;
    private double nearClip = 6;
    private double fov = 0.4*Math.PI;
    private Operator ProjectionOperator = new Operator(
            1 / Math.tan(fov), 0, 0, 0,
            0, 1 / Math.tan(fov), 0, 0,
            0, 0, farClip / (farClip - nearClip), -(farClip * nearClip) / (farClip - nearClip),
            0, 0, -1, 0
    );
    private Operator VPOperator = ViewOperator.compose(ProjectionOperator);
    private Operator MVPOperator = ModelOperator.compose(VPOperator);

    private static final Line3D xAxis = new Line3D(new Vec4(-1, 0, 0, 1), new Vec4(1, 0, 0, 1));
    private static final Line3D yAxis = new Line3D(new Vec4(0, -1, 0, 1), new Vec4(0, 1, 0, 1));
    private static final Line3D zAxis = new Line3D(new Vec4(0, 0, -1, 1), new Vec4(0, 0, 1, 1));

    public BodyViewer(RotationBody body) {
        this.body = body;
        var mouseHandler = new MouseAdapter() {
            private Point2D origin;

            @Override
            public void mousePressed(MouseEvent e) {
                super.mousePressed(e);
                origin = e.getPoint();
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                super.mousePressed(e);
                origin = e.getPoint();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);
                origin = null;
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                super.mouseDragged(e);
                var point = e.getPoint();
                if (origin == null) return;
                var delta = new Vec4(point.getX() - origin.getX(), point.getY() - origin.getY(), 0, 1);
                origin = point;
                var modifiers = e.getModifiersEx();
                if ((modifiers & InputEvent.BUTTON3_DOWN_MASK) == InputEvent.BUTTON3_DOWN_MASK) {
                    if (delta.x() == 0 && delta.y() == 0) return;
                    rotateModel(new Vec4(-delta.y(), delta.x(), 0, 1),0.01*delta.length());
                    repaint();
                } else if ((modifiers & InputEvent.BUTTON2_DOWN_MASK) == InputEvent.BUTTON2_DOWN_MASK) {

                }
            }
        };
        var mouseWheelHandler = new MouseWheelListener() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                var delta = e.getPreciseWheelRotation()*0.2;

                if (nearClip + delta < 10 && nearClip + delta > 0) {
                    nearClip += delta;
                    farClip += delta;
                }repaint();
            }
        };

        addMouseMotionListener(mouseHandler);
        addMouseListener(mouseHandler);
        addMouseWheelListener(mouseWheelHandler);
    }

    private static void drawLine(Line3D line, int width, int height, Graphics2D g2) {
        var begin = line.begin.normalize();
        var end = line.end.normalize();

        if (begin.z() <= 0 || begin.z() >= 1 || end.z() <= 0 || end.z() >= 1)
            return;

        var x1 = (int) (width * (begin.x() / begin.w() + 0.5));
        var y1 = (int) (height * (begin.y() / begin.w() + 0.5));

        var x2 = (int) (width * (end.x() / end.w() + 0.5));
        var y2 = (int) (height * (end.y() / end.w() + 0.5));
        g2.drawLine(x1, y1, x2, y2);
    }

    @Override
    public void paintComponent(Graphics g) {
        var g2 = (Graphics2D) g;
        var width = getWidth();
        var height = getHeight();

        double aspect = (double) width/height;
        double tan = 0.5/(nearClip);

        var xx = nearClip / (tan*aspect);
        var yy = nearClip / (tan);
        var zz = farClip / (farClip - nearClip);
        var zw = farClip * nearClip / (farClip - nearClip);

        ProjectionOperator = new Operator(
                xx, 0, 0, 0,
                0, yy, 0, 0,
                0, 0, zz, zw,
                0, 0, 1, 0
        );
        VPOperator = ViewOperator.compose(ProjectionOperator);
        MVPOperator = ModelOperator.compose(VPOperator);

        g2.setColor(Color.WHITE);
        g2.fillRect(0, 0, width, height);

        g2.setColor(Color.BLACK);
        var circlesIterator = body.circlesIterator();
        var circleIt = circlesIterator.next();
        for (; circlesIterator.hasNext(); circleIt = circlesIterator.next())
            for (var line = circleIt.next(); circleIt.hasNext(); line = circleIt.next())
                drawLine(line.apply(MVPOperator), width, height, g2);
        if (body.getM2() != 0)
        for (var line = circleIt.next(); circleIt.hasNext(); line = circleIt.next())
            drawLine(line.apply(MVPOperator), width, height, g2);

        var outlinesIterator = body.outlinesIterator();
        for (var outlineIt = outlinesIterator.next(); outlinesIterator.hasNext(); outlineIt = outlinesIterator.next()) {
            Line3D line;
            for (line = outlineIt.next(); outlineIt.hasNext(); line = outlineIt.next()) {
                drawLine(line.apply(MVPOperator), width, height, g2);
            }
            drawLine(line.apply(MVPOperator), width, height, g2);
        }
    }

    private void rotateModel(Vec4 axis, double degree) {
        ModelOperator = ModelOperator.compose(Operator.rotateOpearator(axis, degree));
        MVPOperator = ModelOperator.compose(VPOperator);
    }

    public RotationBody getBody() {
        return body;
    }
}
