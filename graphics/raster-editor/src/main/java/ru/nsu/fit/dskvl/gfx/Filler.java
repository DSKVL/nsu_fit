package ru.nsu.fit.dskvl.gfx;

import javax.imageio.IIOException;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Stack;

public class Filler implements Tool {
    private Color color = Color.BLACK;

    @Override
    public Color getColor() {
        return color;
    }

    record Span (int leftX, int rightX, int y) {}

    private Span locateSpan(int xStart, int yStart, int oldColor, BufferedImage img) {
        var imgWidth = img.getWidth();

        var x = xStart;
        while (x < imgWidth && img.getRGB(x, yStart) == oldColor )
            x++;
        var right = x-1;

        x = xStart;
        while (x >= 0 && img.getRGB(x, yStart) == oldColor)
            x--;
        var left = x+1;

        return new Span(left, right, yStart);
    }

    private void fillSpan(Span span, int color, BufferedImage img) {
        for (var i = span.leftX(); i <= span.rightX(); i++)
            img.setRGB(i, span.y, color);
    }

    private void locateSpans(int leftBorder, int rightBorder, int y, int color, BufferedImage img, Stack<Span> spansStack) {
        for (int x = leftBorder; x < rightBorder; x++) {
            if (img.getRGB(x, y) == color) {
                var nextSpan = locateSpan(x, y, color, img);
                spansStack.push(nextSpan);

                x = nextSpan.rightX + 1;
            }
        }
    }

    private void fill(Point p, int oldColor, int newColor, BufferedImage img) {
        var imgHeight = img.getHeight();
        Stack<Span> spansStack = new Stack<>();
        spansStack.push(locateSpan(p.x, p.y, oldColor, img));
        while(!spansStack.isEmpty()) {
            var currentSpan = spansStack.pop();
            fillSpan(currentSpan, newColor, img);

            if (currentSpan.y+1 < imgHeight)
                locateSpans(currentSpan.leftX, currentSpan.rightX, currentSpan.y+1, oldColor, img, spansStack);
            if (currentSpan.y-1 >= 0)
                locateSpans(currentSpan.leftX, currentSpan.rightX, currentSpan.y-1, oldColor, img, spansStack);
        }

    }

    @Override
    public void onClick(MouseEvent e, BufferedImage img) {
        var bufferedImage = (BufferedImage) img;
        var oldColor = bufferedImage.getRGB(e.getX(), e.getY());
        if (oldColor != color.getRGB())
            fill(e.getPoint(), oldColor, color.getRGB(), bufferedImage);
    }

    @Override
    public void setColor(Color color) {
        this.color = color;
    }
}
