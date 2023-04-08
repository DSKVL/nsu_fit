package ru.nsu.fit.dskvl.gfx;

import java.awt.image.BufferedImage;
import java.util.Arrays;

public class DitheringFilter implements Filter{
    private int[] colors;
    private final boolean ordered;
    private final int[] ks = new int[]{16, 16, 16};
    DitheringFilter(int[] colors, int thresholdParamter, boolean ordered) {
        this.colors = colors;
        for (int i = 0; i < 2; i++)
            ks[i] = calulateThresholdParameter(colors[i]);
        this.ordered = ordered;
    }
    DitheringFilter(int[] colors, int thresholdParamter){ this(colors, thresholdParamter, true); }
    DitheringFilter(int[] colors)                       { this(colors, 0,false); }
    DitheringFilter()                                   { this(new int[]{2, 2, 2},2, false); }

    public int[] getColors() {
        return colors;
    }
    public void setColors(int[] colors) {
        this.colors = colors;
        for (int i = 0; i < 2; i++)
            ks[i] = calulateThresholdParameter(colors[i]);
    }

    private static int calulateThresholdParameter(int colors) {
        if (colors < 3) return 1 << 6;
        else if (colors < 5) return 1 << 5;
        else if (colors < 17) return 1 << 4;
        else return 1 << 3;
    }

    public int[] getLUT(int colors) {
        var lut = new int[256];

        var quant = 256/(colors-1);
        var quantHalf = quant/2;
        int i = 0;

        for (int color = 0; color < colors - 1; color++) {
            for (; i < quantHalf + color*quant; i++)
                lut[i] = color*quant;
        }
        for (; i < 256; i++) {
            lut[i] = 255;
        }

        return lut;
    }

    void transform(int[] lut, int i, int j, int n, int shift, int k) {
        var nHalf = n/2;
        for (int jj = j; jj < j + nHalf; jj++)
            for (int ii = i; ii < i + nHalf; ii++) {
                lut[jj * k + ii] *= 4;
                lut[jj * k + ii] += shift;
            }
    }

    private void thresholdMatrix(int[] lut, int i, int j, int n, int k) {
        if (n == 1) {
            lut[j * k + i] = 0;
            return;
        }
        var nHalf = n/2;

        thresholdMatrix(lut, i, j, nHalf, k);
        transform(lut, i, j, n, 0, k);
        thresholdMatrix(lut, i+nHalf, j, nHalf, k);
        transform(lut, i+nHalf, j, n, 2, k);
        thresholdMatrix(lut, i,j+nHalf, nHalf, k);
        transform(lut, i, j+nHalf, n, 3, k);
        thresholdMatrix(lut,i+nHalf ,j+nHalf, nHalf, k);
        transform(lut, i+nHalf, j+nHalf, n, 1, k);

    }

    public double[] getThresholdMap(int k) {
        var lut = new int[k*k];
        thresholdMatrix(lut, 0,0, k, k);
        return Arrays.stream(lut).mapToDouble(i -> (double) i/(k*k)  - 0.5).toArray();
    }

    private int quantiseAndSpreadError(int i, int j, int[] channel, int width, int[] lut) {
        var intensity = channel[j * width + i];
        var dithered = lut[Filter.bound(intensity, 0, 255)];
        var error = intensity - dithered;
        channel[j * width + i + 1] += (7 * error) / 16;
        channel[(j + 1) * width + i - 1] += (3 * error) / 16;
        channel[(j + 1) * width + i] += (5 * error) / 16;
        channel[(j + 1) * width + i + 1] += error / 16;
        return dithered;
    }
    private int quantiseAndSpreadErrorLastColumn(int j, int[] channel, int width, int[] lut) {
        var intensity = channel[j*width + width-1];
        var dithered = lut[Filter.bound(intensity, 0, 255)];
        var error = intensity - dithered;
        channel[(j + 1) * width + width - 2] += (3 * error) / 8;
        channel[(j + 1) * width + width - 1] += (5 * error) / 8;
        return dithered;
    }
    private int quantiseAndSpreadErrorLastRow(int i, int[] channel, int width, int height, int[] lut) {
        var intensity = channel[(height-1)*width + i];
        var dithered = lut[Filter.bound(intensity, 0, 255)];
        var error = intensity - dithered;
        channel[(height-1)*width + i + 1] += (7 * error) / 8;
        return dithered;
    }
    int quantiseLastRowLastColumn(int[] channel, int width, int height, int[] lut) {
        var intensity = channel[height*width-1];
        return lut[Filter.bound(intensity, 0, 255)];
    }

    private void unordered(BufferedImage original, BufferedImage processed) {
        var width = original.getWidth();
        var height = original. getHeight();
        var lutR = getLUT(colors[0]);
        var lutG = getLUT(colors[1]);
        var lutB = getLUT(colors[2]);
        processed.getGraphics().drawImage(original, 0,0, null);
        int[] redChannel = new int[width*height];
        int[] blueChannel = new int[width*height];
        int[] greenChannel = new int[width*height];

        for (int j = 0; j < height; j++)
            for (int i = 0; i < width; i++) {
                int originalColor = processed.getRGB(i, j);
                redChannel[j*width + i]   = Filter.extractRed(originalColor);
                greenChannel[j*width + i]  = Filter.extractGreen(originalColor);
                blueChannel[j*width + i] = Filter.extractBlue(originalColor);
            }

        for (int j = 0; j < height-1; j++) {
            for (int i = 0; i < width - 1; i++) {
                var rDithered = quantiseAndSpreadError(i, j, redChannel,   width, lutR);
                var gDithered = quantiseAndSpreadError(i, j, greenChannel, width, lutG);
                var bDithered = quantiseAndSpreadError(i, j, blueChannel,  width, lutB);

                processed.setRGB(i, j, Filter.channelsToInt(rDithered, gDithered, bDithered));
            }
            var rDithered = quantiseAndSpreadErrorLastColumn(j, redChannel,   width, lutR);
            var gDithered = quantiseAndSpreadErrorLastColumn(j, greenChannel, width, lutG);
            var bDithered = quantiseAndSpreadErrorLastColumn(j, blueChannel,  width, lutB);

            processed.setRGB(width - 1, j, Filter.channelsToInt(rDithered, gDithered, bDithered));
        }
        for (int i = 0; i < width - 1; i++) {
            var rDithered = quantiseAndSpreadErrorLastRow(i, redChannel,   width, height, lutR);
            var gDithered = quantiseAndSpreadErrorLastRow(i, greenChannel, width, height, lutG);
            var bDithered = quantiseAndSpreadErrorLastRow(i, blueChannel,  width, height, lutB);

            processed.setRGB(i, height-1, Filter.channelsToInt(rDithered, gDithered, bDithered));
        }
        var rDithered = quantiseLastRowLastColumn(redChannel, width, height, lutR);
        var gDithered = quantiseLastRowLastColumn(greenChannel, width, height, lutG);
        var bDithered = quantiseLastRowLastColumn(blueChannel, width, height, lutB);

        processed.setRGB(width-1, (height-1),Filter.channelsToInt(rDithered, gDithered, bDithered));
    }

    private void ordered(BufferedImage original, BufferedImage processed) {
        int[][] luts = new int[][]{getLUT(colors[0]), getLUT(colors[1]), getLUT(colors[2])};
        double[] rs = new double[] {(double) 255/(colors[0] - 1), (double) 255/(colors[1] - 1), (double) 255/(colors[2] - 1)};
        double[][] thresholdMaps = new double[][]{getThresholdMap(ks[0]), getThresholdMap(ks[1]), getThresholdMap(ks[2])};

        var width = original.getWidth();
        var height = original. getHeight();

        for (int j = 0; j < height; j++)
            for (int i = 0; i < width; i++) {
                var thresholdR = thresholdMaps[0][(j%ks[0])*ks[0] + i%ks[0]];
                var thresholdG = thresholdMaps[1][(j%ks[1])*ks[1] + i%ks[1]];
                var thresholdB = thresholdMaps[2][(j%ks[2])*ks[2] + i%ks[2]];

                var color = original.getRGB(i, j);
                var r = luts[0][Filter.bound(Filter.extractRed(color)   + (int) (rs[0]*thresholdR), 0, 255)];
                var g = luts[1][Filter.bound(Filter.extractGreen(color) + (int) (rs[1]*thresholdG), 0, 255)];
                var b = luts[2][Filter.bound(Filter.extractBlue(color)  + (int) (rs[2]*thresholdB), 0, 255)];
                processed.setRGB(i, j, Filter.channelsToInt(r, g, b));
            }
    }

    public void process(BufferedImage original, BufferedImage processed) {
        if (ordered) ordered(original, processed);
        else unordered(original, processed);
    }

    public static DitheringFilter orderedDithering = new DitheringFilter(new int[]{16, 16, 16}, 6, true);
    public static DitheringFilter unorderedDithering = new DitheringFilter(new int[]{4, 8, 2});

}
