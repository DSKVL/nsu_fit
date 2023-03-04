package ru.nsu.fit.dskvl.gfx;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.Optional;

public class LineDrawer implements Tool {
    private Optional<Point> previousPoint = Optional.empty();
    private Color color = Color.BLACK;
    private int thickness = 1;

    LineDrawer() {}
    LineDrawer(int thickness) {
        this.thickness=thickness;
    }

    private void drawPositiveSlopeLessThanOne(Point a, Point b, BufferedImage img) {
        var x = a.x;
        var y = a.y;
        var dx = b.x - a.x;
        var dy = b.y - a.y;
        var err = -dx;
        for (var i = 0; i < dx; i++) {
            x++;
            err += 2*dy;
            if (err > 0) {
                err -= 2*dx;
                y++;
            }
            img.setRGB(x, y, color.getRGB());
        }

    }
    private void drawNegativeSlopeLessThanOne(Point a, Point b, BufferedImage img) {

        var x = a.x;
        var y = a.y;
        var dx = b.x - a.x;
        var dy = a.y - b.y;
        var err = -dx;
        for (var i = 0; i < dx; i++) {
            x++;
            err += 2*dy;
            if (err > 0) {
                err -= 2*dx;
                y--;
            }
            img.setRGB(x, y, color.getRGB());
        }
    }

    private void drawPositiveSlopeGreaterThanOne(Point a, Point b, BufferedImage img) {


        var x = a.x;
        var y = a.y;
        var dx = b.x - a.x;
        var dy = b.y - a.y;
        var err = -dy;
        for (var i = 0; i < dy; i++) {
            y++;
            err += 2*dx;
            if (err > 0) {
                err -= 2*dy;
                x++;
            }
            img.setRGB(x, y, color.getRGB());
        }
    }
    private void drawNegativeSlopeGreaterThanOne(Point a, Point b, BufferedImage img) {
        var x = b.x;
        var y = b.y;
        var dx = b.x - a.x;
        var dy = a.y - b.y;
        var err = -dy;
        for (var i = 0; i < dy; i++) {
            y++;
            err += 2*dx;
            if (err > 0) {
                err -= 2*dy;
                x--;
            }
            img.setRGB(x, y, color.getRGB());
        }
    }


    public void drawLine(Point a, Point b, BufferedImage img) {
        if (thickness != 1) {
            var g = img.getGraphics();
            Graphics2D g2 = (Graphics2D) g;
            g2.setStroke(new BasicStroke(thickness));
            g2.setColor(color);
            g2.drawLine(a.x, a.y, b.x,b.y);
        }

        Point leftPoint;
        Point rightPoint;
        if (a.x > b.x) {
            leftPoint = b;
            rightPoint = a;
        } else {
            leftPoint = a;
            rightPoint = b;
        }

        var slope = ((float)(rightPoint.y - leftPoint.y))/(rightPoint.x - leftPoint.x);

        if (slope > 0) {
            if (slope < 1)
                drawPositiveSlopeLessThanOne(leftPoint, rightPoint, img);
            else
                drawPositiveSlopeGreaterThanOne(leftPoint, rightPoint, img);
        } else {
            if (slope > -1)
                drawNegativeSlopeLessThanOne(leftPoint, rightPoint, img);
            else
                drawNegativeSlopeGreaterThanOne(leftPoint, rightPoint, img);
        }
    }

    @Override
    public void onClick(MouseEvent e, BufferedImage img) {
        previousPoint.ifPresentOrElse(
                point ->  {
                    drawLine(point, e.getPoint(), img);
                    previousPoint = Optional.empty();
                },
                () -> previousPoint = Optional.of(e.getPoint()));
    }

    @Override
    public void setColor(Color col) {
        color = col;
    }

    @Override
    public Color getColor() {
        return color;
    }
}

