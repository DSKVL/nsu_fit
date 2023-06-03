package ru.nsu.fit.dskvl.gfx.views;

import ru.nsu.fit.dskvl.gfx.models.Point2D;
import ru.nsu.fit.dskvl.gfx.models.Spline;

import javax.swing.*;

import java.awt.*;

public class OutlineEditorPoint extends JComponent {
    private final Point2D modelCoordinates;
    private final OutlineEditor parent;
    private final double relativeDiameter = 0.03;
    private boolean isActive = false;
    private final int index;

    public OutlineEditorPoint(Point2D coordinates, OutlineEditor parent, CoordinatesListener listener, Spline spline, int index) {
        this.modelCoordinates = coordinates;
        this.parent = parent;
        this.index = index;
        setSize(getWidth(), getHeight());
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        var g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(isActive ? Color.YELLOW : Color.WHITE);
        g2.setStroke(new BasicStroke(1));
        g2.fillOval(1, 1, getWidth()-2, getHeight()-2);
    }

    @Override public int getX() {
        return (int) (((modelCoordinates.x/parent.getScale()) + 0.5)*parent.getWidth() - getWidth()/2);
    }
    @Override public int getY() {
        return (int) ((0.5 + modelCoordinates.y/(parent.getScale()*parent.getAspectRatio()))*parent.getHeight() - getHeight()/(2));
    }
    @Override public int getWidth() { return (int) (relativeDiameter*parent.getWidth()); }
    @Override public int getHeight() {
        return (int) (relativeDiameter*parent.getWidth());
    }
    @Override public Dimension getPreferredSize() {
        return new Dimension(30, 30);
    }
    @Override public Dimension getMinimumSize() {
        return getPreferredSize();
    }
    @Override public Dimension getMaximumSize() {
        return getPreferredSize();
    }
    public void     setActive(boolean status)           { this.isActive = status; }
    public boolean  isActive()                          { return isActive; }
    public Point2D  getModelCoordinates()               { return modelCoordinates; }
    public int      getIndex()                          { return index; }
}
