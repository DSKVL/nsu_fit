package ru.nsu.fit.dskvl.gfx;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

public interface Tool {
    void onClick(MouseEvent e, BufferedImage img);
    void setColor(Color color);
    Color getColor();
}
