package ru.nsu.fit.dskvl.gfx;

import java.awt.image.BufferedImage;

public interface Filter {
    void process(BufferedImage original, BufferedImage processed);
}
