package ru.nsu.fit.dskvl.gfx;

import java.awt.image.BufferedImage;
import java.util.function.Function;

public interface Filter {
    void process(BufferedImage original, BufferedImage processed);

    static int extractRed(int p) {
        return (p >> 16) & 0xFF;
    }
    static int extractGreen(int p) {
        return (p >> 8) & 0xFF;
    }
    static int extractBlue(int p) {
        return p & 0xFF;
    }
    static int[] extractChannels(int p) {
        var result = new int[3];
        result[0] = extractRed(p);
        result[1] = extractGreen(p);
        result[2] = extractBlue(p);
        return result;
    }

    static int channelsToInt(int r, int g, int b) {
        return 0xFF000000 | ((r & 0xFF) << 16) | ((g & 0xFF) << 8) | (b & 0xFF);
    }

    static Function<int[][], Integer> forChannels(Function<int[], Integer> f) {
        return (int[][] rgb) -> channelsToInt(f.apply(rgb[0]), f.apply(rgb[1]), f.apply(rgb[2]));
    }

    static int bound(int val, int min, int max) {
        if (val > max) val = max;
        else if (val < min) val = min;
        return val;
    }
    static int threshold(int val, int threshold, int low, int high) {
        return (val >= threshold) ? high : low;
    }
}
