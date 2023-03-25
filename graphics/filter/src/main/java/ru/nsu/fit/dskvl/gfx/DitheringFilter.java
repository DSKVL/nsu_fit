package ru.nsu.fit.dskvl.gfx;

import java.awt.image.BufferedImage;

public class DitheringFilter implements Filter{
    private final int bitrate;
    private final boolean ordered;
    private final int thresholdParamter;
    DitheringFilter(int bitrate, int thresholdParamter, boolean ordered) {
        this.bitrate = bitrate;
        this.thresholdParamter = thresholdParamter;
        this.ordered = ordered;
    }
    DitheringFilter(int bitrate, int thresholdParamter) {
        this(bitrate, thresholdParamter, true);
    }
    DitheringFilter(int bitrate) {
        this(bitrate, 0,false);
    }
    DitheringFilter() {
        this(1,0, false);
    }
    public int[] getLUT(int bitrate) {
        var lut = new int[256];
        var quant = (int) (256/Math.pow(2, bitrate));
        for (int i = 0; i < 256; i++)
            lut[i] = (i-128)-(i-127)%quant + 128;
        return lut;
    }

    void transform(int[] lut, int i, int j, int n, int shift) {
        var nHalf = n/2;
        for (int jj = j; jj < j + nHalf; jj++)
            for (int ii = i; ii < i + nHalf; ii++) {
                lut[jj * thresholdParamter + ii] *= (4 * n * n);
                lut[jj * thresholdParamter + ii] += shift;
            }
    }

    private void thresholdMatrix(int[] lut, int i, int j, int n) {
        if (n == 1) lut[j*thresholdParamter + i] = 0;
        var nHalf = n/2;

        thresholdMatrix(lut, i, j, nHalf);
        transform(lut, i, j, n, 0);
        thresholdMatrix(lut, i+n, j, nHalf);
        transform(lut, i+n, j, n, 2);
        thresholdMatrix(lut, i,j+n, nHalf);
        transform(lut, i, j+n, n, 3);
        thresholdMatrix(lut,i+n ,j+n, nHalf);
        transform(lut, i+n, j+n, n, 1);

    }

    public int[] getThresholdMap() {
        var lut = new int[thresholdParamter*thresholdParamter];
        thresholdMatrix(lut, 0,0, thresholdParamter);
        return lut;
    }



    private void unordered(BufferedImage original, BufferedImage processed) {
        var width = original.getWidth();
        var height = original. getHeight();
        var lut = getLUT(bitrate);
        processed.getGraphics().drawImage(original, 0,0, null);
        int[] redChannel = new int[width*height];
        int[] blueChannel = new int[width*height];
        int[] greenChannel = new int[width*height];

        for (int j = 0; j < height; j++)
            for (int i = 0; i < width; i++) {
                int originalColor = processed.getRGB(i, j);
                redChannel[j*width + i] = (originalColor >> 16)&0xFF;
                blueChannel[j*width + i] = (originalColor >> 8)&0xFF;
                greenChannel[j*width + i] = (originalColor)&0xFF;
            }


        for (int j = 0; j < height; j++) {
            for (int i = 0; i < width - 1; i++) {
                var r = redChannel[j * width + i];
                var rDithered = (r < 256) ? lut[r] : 255;
                var rError = r - rDithered;
                redChannel[j * width + i + 1] += (7 * rError) / 16;
                redChannel[(j + 1) * width + i - 1] += (3 * rError) / 16;
                redChannel[(j + 1) * width + i] += (5 * rError) / 16;
                redChannel[(j + 1) * width + i + 1] += rError / 16;

                var g = greenChannel[j * width + i];
                var gDithered = (g < 256) ? lut[g] : 255;
                var gError = g - gDithered;
                greenChannel[j * width + i + 1] += (7 * gError) / 16;
                greenChannel[(j + 1) * width + i - 1] += (3 * gError) / 16;
                greenChannel[(j + 1) * width + i] += (5 * gError) / 16;
                greenChannel[(j + 1) * width + i + 1] += gError / 16;

                var b = blueChannel[j * width + i];
                var bDithered = (r < 256) ? lut[b] : 255;
                var bError = b - bDithered;
                blueChannel[j * width + i + 1] += (7 * bError) / 16;
                blueChannel[(j + 1) * width + i - 1] += (3 * bError) / 16;
                blueChannel[(j + 1) * width + i] += (5 * bError) / 16;
                blueChannel[(j + 1) * width + i + 1] += bError / 16;

                processed.setRGB(i, j, 0xFF000000 | (rDithered << 16) & 0xFF | (gDithered << 8) & 0xFF | (bDithered) & 0xFF);
            }
            var r = redChannel[j*width + width-1];
            var rDithered = (r < 256) ? lut[r] : 255;
            var g = greenChannel[j*width +  width-1];
            var gDithered = (g < 256) ? lut[g] : 255;
            var b = blueChannel[j*width +  width-1];
            var bDithered = (r < 256) ? lut[b] : 255;

            processed.setRGB(width-1, j,0xFF000000 | (rDithered<<16)&0xFF | (gDithered<<8)&0xFF | (bDithered)&0xFF);
        }
        for (int i = 0; i < width - 1; i++) {
            var r = redChannel[(height-1) * width + i];
            var rDithered = (r < 256) ? lut[r] : 255;


            var g = greenChannel[(height-1) * width + i];
            var gDithered = (g < 256) ? lut[g] : 255;

            var b = blueChannel[(height-1) * width + i];
            var bDithered = (r < 256) ? lut[b] : 255;

            processed.setRGB(i, (height-1), 0xFF000000 | (rDithered << 16) & 0xFF | (gDithered << 8) & 0xFF | (bDithered) & 0xFF);
        }
        var r = redChannel[(height-1)*width + width-1];
        var rDithered = (r < 256) ? lut[r] : 255;
        var g = greenChannel[(height-1)*width +  width-1];
        var gDithered = (g < 256) ? lut[g] : 255;
        var b = blueChannel[(height-1)*width +  width-1];
        var bDithered = (r < 256) ? lut[b] : 255;

        processed.setRGB(width-1, (height-1),0xFF000000 | (rDithered<<16)&0xFF | (gDithered<<8)&0xFF | (bDithered)&0xFF);
    }

    private void ordered(BufferedImage original, BufferedImage processed) {
        var lut = getLUT(bitrate);
        var thresholdMap = getThresholdMap();
        var width = original.getWidth();
        var height = original. getHeight();;

        for (int j = 0; j < height; j++)
            for (int i = 0; i < width; i++) {

            }
    }

    public void process(BufferedImage original, BufferedImage processed) {
        if (ordered) ordered(original, processed);
        else unordered(original, processed);
    }
}
