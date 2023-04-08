package ru.nsu.fit.dskvl.gfx;

import java.awt.image.BufferedImage;

public class Rotate implements Filter {
    int degrees = 180;
    double angle = Math.PI;

    public int getAngle() {
        return degrees;
    }

    public void setAngle(int degrees) {
        this.degrees = degrees;
        this.angle = degrees*Math.PI/180;
        this.sin = Math.sin(angle);
        this.cos = Math.cos(angle);
    }

    double sin = 0;
    double cos = 1;

    Rotate() {
    }

    Rotate(int degrees) {
        this.degrees = degrees;
        this.angle = degrees*Math.PI/180;
        this.sin = Math.sin(angle);
        this.cos = Math.cos(angle);
    }

    @Override
    public void process(BufferedImage original, BufferedImage processed) {
        var width = processed.getWidth();
        var height = processed.getHeight();

        for (int j = 0; j < height; j++)
            for (int i = 0; i < width; i++) {
                double iRotated = (i- (double) width /2)*cos - (j- (double) height /2)*sin + (double) width/2;
                double jRotated = (i- (double) width /2)*sin + (j- (double) height /2)*cos + (double) height/2;

                int iFloor = (int) Math.floor(iRotated);
                int iCeil  = (int) Math.ceil(iRotated);
                int jFloor = (int) Math.floor(jRotated);
                int jCeil  = (int) Math.ceil(jRotated);

                if (      iFloor < 0 || iFloor >= width
                        ||iCeil  < 0 || iCeil  >= width
                        ||jFloor < 0 || jFloor >= height
                        ||jCeil  < 0 || jCeil  >= height) {
                    processed.setRGB(i, j, -1);
                    continue;
                }

                double iErr = iRotated - iFloor;
                double jErr = jRotated - jFloor;

                var ij   = Filter.extractChannels(original.getRGB(iFloor, jFloor));
                var i1j  = Filter.extractChannels(original.getRGB(iCeil,  jFloor));
                var ij1  = Filter.extractChannels(original.getRGB(iFloor, jCeil));
                var i1j1 = Filter.extractChannels(original.getRGB(iCeil,  jCeil));

                var channels = new int[3][4];

                for (int c = 0; c < 3; c++ ) {
                    channels[c][0] = ij[c];
                    channels[c][1] = i1j[c];
                    channels[c][2] = ij1[c];
                    channels[c][3] = i1j1[c];
                }

                var bilinearInterpolation = Filter.forChannels(colorChs ->
                        (int) ((colorChs[0]*(1-iErr) + colorChs[1]*iErr)*(1-jErr)
                        + (colorChs[2]*(1-iErr) + colorChs[3]*iErr)*jErr)
                );

                processed.setRGB(i, j, bilinearInterpolation.apply(channels));
            }
    }
    public static Rotate filter = new Rotate(180);
}
