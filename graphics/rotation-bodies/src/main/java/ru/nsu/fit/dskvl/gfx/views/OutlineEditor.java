package ru.nsu.fit.dskvl.gfx.views;

import ru.nsu.fit.dskvl.gfx.models.Point2D;
import ru.nsu.fit.dskvl.gfx.models.Spline;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

public class OutlineEditor extends JComponent {
    private final Spline shapeOutline;
    private final ArrayList<OutlineEditorPoint> editorPoints = new ArrayList<>();
    private OutlineEditorPoint activePoint;
    private double scale = 6;
    CoordinatesListener listener;
    private final MouseAdapter mouseHandler;
    private final Spline spline;

    public double getScale() {
        return scale;
    }

    public double getAspectRatio() {
        return aspectRatio;
    }

    double aspectRatio = 1.0;

    OutlineEditor(Spline spline, CoordinatesListener listener) {
        setLayout(null);
        shapeOutline = spline;
        this.listener = listener;
        this.spline = spline;
        var editor = this;
        mouseHandler = new MouseAdapter() {
            boolean drag = false;

            @Override
            public void mousePressed(MouseEvent e) {
                var point = e.getPoint();
                OutlineEditorPoint nextActive = null;
                for (var editorPoint : editorPoints)
                    if (editorPoint.getBounds(null).contains(point))
                        nextActive = editorPoint;

                if (nextActive == null) return;

                for (var editorPoint : editorPoints)
                    editorPoint.setActive(false);
                activePoint = nextActive;
                activePoint.setActive(true);
                drag = true;
                listener.updateActivePointPosition(activePoint);
            }

            @Override public void mouseReleased(MouseEvent e) { drag = false; }
            @Override
            public void mouseDragged(MouseEvent e) {
                if (!drag) return;

                var modelCoordinates =  activePoint.getModelCoordinates();
                modelCoordinates.x = ((double) e.getX() /editor.getWidth() - 0.5)*scale;
                modelCoordinates.y = ((double) e.getY() /editor.getHeight() - 0.5)*aspectRatio*scale ;
                listener.updateActivePointPosition(activePoint);
            }
        };

        addMouseMotionListener(mouseHandler);
        addMouseListener(mouseHandler);
    }

    public void addPoint(Point2D point) {
        var editorPoint = new OutlineEditorPoint(point, this, listener, spline, spline.getControlPoints().size()-1);
        editorPoint.setVisible(true);
        editorPoints.add(editorPoint);
        add(editorPoint);
        editorPoint.setVisible(true);
        editorPoint.addMouseMotionListener(mouseHandler);
        editorPoint.addMouseListener(mouseHandler);
    }

    public ArrayList<OutlineEditorPoint> getEditorPoints() { return editorPoints; }

    @Override public Dimension getMinimumSize()   { return new Dimension(240, 200); }
    @Override public Dimension getPreferredSize() { return new Dimension(240, 200); }

    @Override public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        var width = getWidth();
        var height = getHeight();
        aspectRatio = (double) height/width;

        g2.setColor(Color.BLACK);
        g2.fillRect(0,0, width, height);

        g2.setColor(Color.GRAY);
        g2.drawLine(0, height/2, width , height/2);
        g2.drawLine(width/2, 0, width/2, height);
        g2.drawLine(
                (int) ((0.5 + 0.5/scale)*width),
                (int) ((0.5 - 0.005)*height),
                (int) ((0.5 + 0.5/scale)*width) ,
                (int) ((0.5 + 0.005)*height));
        g2.drawLine(
                (int) ((0.5 - 0.005)*width),
                (int) ((0.5 - 0.5/(scale*aspectRatio))*height),
                (int) ((0.5 + 0.005)*width),
                (int) ((0.5 - 0.5/(scale*aspectRatio))*height));

        g2.setColor(Color.CYAN);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        for (var line : spline.getSplineLines()) {
            var begin = line.begin;
            var end = line.end;
            g2.drawLine(
                    (int) ((begin.x /scale + 0.5)*width),
                    (int) ((begin.y /(scale*aspectRatio) + 0.5)*height),
                    (int) ((end.x   /scale   + 0.5)*width),
                    (int) ((end.y   /(scale*aspectRatio)   + 0.5)*height)
            );
        }
//        var splinePoints = shapeOutline.getSplinePoints();
//        var end = splinePoints.get(0);
//        for (int i = 1; i < splinePoints.size(); i++) {
//            var begin = end;
//            end = splinePoints.get(i);
//
//            g2.drawLine(
//                    (int) ((begin.x /scale + 0.5)*width),
//                    (int) ((begin.y /(scale*aspectRatio) + 0.5)*height),
//                    (int) ((end.x   /scale   + 0.5)*width),
//                    (int) ((end.y   /(scale*aspectRatio)   + 0.5)*height)
//            );
//        }
    }

    public void setActivePoint(int index) {
        if (activePoint != null)
            activePoint.setActive(false);
        this.activePoint = editorPoints.get(index);
        activePoint.setActive(true);
    }

    public OutlineEditorPoint getActivePoint() { return activePoint; }
}
