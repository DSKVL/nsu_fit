package ru.nsu.fit.dskvl.gfx;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;

import java.awt.image.BufferedImage;
import java.util.ArrayList;


public class ShapeDrawer implements Tool {
    private final ArrayList<Point> points = new ArrayList<>();
    private Color color = Color.BLACK;
    ShapeDrawer(int corners, int radius, float angle) {
        double dAngle = 2*Math.PI/corners;
        for(int i = 0; i < corners; i++) {
            points.add(new Point((int) (radius*Math.cos(angle)), (int) (radius*Math.sin(angle))));
            angle += dAngle;
        }
    }
    ShapeDrawer(int corners, int outerRadius, int innerRadius, float angle) {
        double dAngle = 2*Math.PI/(2*corners);
        for(int i = 0; i < corners; i++) {
            points.add(new Point((int) (outerRadius*Math.cos(angle)), (int) (outerRadius*Math.sin(angle))));
            angle += dAngle;
            points.add(new Point((int) (innerRadius*Math.cos(angle)), (int) (innerRadius*Math.sin(angle))));
            angle += dAngle;
        }
    }


    @Override
    public void onClick(MouseEvent e, BufferedImage img) {
        SwingUtilities.invokeLater(() -> {
            var lineDrawer = new LineDrawer(1);
            lineDrawer.setColor(color);
            var centerX = e.getX();
            var centerY = e.getY();

            for (int i = 0; i < points.size()-1; i++) {
                var a = new Point(points.get(i).x + centerX, points.get(i).y+centerY);
                var b = new Point(points.get(i+1).x + centerX, points.get(i+1).y+centerY);
                lineDrawer.drawLine(a, b, img);
            }
            var a = new Point(points.get(0).x + centerX, points.get(0).y+centerY);
            var b = new Point(points.get(points.size()-1).x + centerX,
                    points.get(points.size()-1).y+centerY);
            lineDrawer.drawLine(a, b, img);
        });
    }

    @Override
    public void setColor(Color color) { this.color = color; }

    @Override
    public Color getColor() {
        return color;
    }
}
