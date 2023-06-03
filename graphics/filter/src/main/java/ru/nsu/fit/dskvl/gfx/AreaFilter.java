package ru.nsu.fit.dskvl.gfx;

import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.Comparator;
import java.util.function.Function;

public class AreaFilter implements Filter{
    protected final int diameter;
    protected final int radius;
    protected final int dataSize;
    protected Function<int[][], Integer> f;

    public AreaFilter(int diameter, Function<int[][], Integer> f) {
        this.diameter = diameter;
        this.radius = diameter / 2;
        this.dataSize = diameter * diameter;
        this.f = f;
    }

    public static BufferedImage expandMirrored(BufferedImage img, int amount) {
        var width = img.getWidth();
        var height = img.getHeight();
        var expanded = new BufferedImage(width + 2 * amount, height + 2 * amount, img.getType());

        for (var j = 0; j < amount; j++) {
            for (var i = 0; i < amount; i++) expanded.setRGB(i, j, img.getRGB(amount - i, amount - j));
            for (var i = 0; i < width; i++) expanded.setRGB(i + amount, j, img.getRGB(i, amount - j));
            for (var i = 0; i < amount; i++)
                expanded.setRGB(width - 1 + amount + i, j, img.getRGB(width - 1 - i, amount - j));
        }
        for (var j = 0; j < height; j++) {
            for (var i = 0; i < amount; i++) expanded.setRGB(i, j + amount, img.getRGB(amount - i, j));
            for (var i = 0; i < width; i++) expanded.setRGB(i + amount, j + amount, img.getRGB(i, j));
            for (var i = 0; i < amount; i++)
                expanded.setRGB(width - 1 + amount + i, j + amount, img.getRGB(width - 1 - i, j));
        }
        for (var j = 0; j < amount; j++) {
            for (var i = 0; i < amount; i++)
                expanded.setRGB(i, height - 1 + amount + j, img.getRGB(amount - i, height - 1 - j));
            for (var i = 0; i < width; i++)
                expanded.setRGB(i + amount, height - 1 + amount + j, img.getRGB(i, height - 1 - j));
            for (var i = 0; i < amount; i++)
                expanded.setRGB(width - 1 + amount + i, height - 1 + amount + j, img.getRGB(width - 1 - i, height - 1 - j));
        }

        return expanded;
    }

    public static void compose(AreaFilter areaFilter1, AreaFilter areaFilter2, BufferedImage original, BufferedImage processed) {
        var copy = FilterPanel.deepCopy(original);
        areaFilter1.process(original, copy);
        areaFilter2.process(copy, processed);
    }

    private static void extractPixels(BufferedImage img, int i, int j, int width, int height, int[] destination) {
        int idx = 0;
        height += j;
        width += i;
        for (int y = j; y < height; y++)
            for (int x = i; x < width; x++)
                destination[idx++] = img.getRGB(x, y);
    }

    public void process(BufferedImage original, BufferedImage processed) {
        var img = expandMirrored(original, radius);
        var buf = new int[dataSize];

        int[][] channels = new int[3][];
        for (var j = 0; j < original.getHeight(); j++)
            for (var i = 0; i < original.getWidth(); i++) {
                extractPixels(img, i, j, diameter, diameter, buf);

                channels[0] = Arrays.stream(buf).map(Filter::extractRed).toArray();
                channels[1] = Arrays.stream(buf).map(Filter::extractGreen).toArray();
                channels[2] = Arrays.stream(buf).map(Filter::extractBlue).toArray();

                img.setRGB(i, j, f.apply(channels));
            }

        for (var j = 0; j < original.getHeight(); j++)
            for (var i = 0; i < original.getWidth(); i++)
                processed.setRGB(i, j, img.getRGB(i + radius, j + radius));
    }


    public static int innerProduct(int[] v1, int[] v2) {
        int sum = 0;
        for (int i = 0; i < v2.length; i++)
            sum += v1[i] * v2[i];
        return sum;
    }
    public static int convolution(int[][] data, int[] mtx, int scalar, int shift) {
        return Filter.forChannels((int[] ch) ->
                Filter.bound( (innerProduct(ch, mtx)/scalar) + shift, 0, 0xFF)).apply(data);
    }
    public static int convolution(int[][] data, int[] mtx, int scalar) {
        return convolution(data, mtx, scalar, 0);
    }

    public static final int[] GAUSSIAN_BLUR_KERNEL_3 = new int[]{
            1, 2, 1,
            2, 4, 2,
            1, 2, 1
    };
    public static final int[] GAUSSIAN_BLUR_KERNEL_5 = new int[]{
            1, 4, 6, 4, 1,
            4, 16, 24, 16, 4,
            6, 24, 36, 24, 6,
            4, 16, 24, 16, 4,
            1, 4, 6, 4, 1
    };
    public static final int[] SHARPEN_KERNEL_3 = new int[]{
            0, -1, 0,
            -1, 5, -1,
            0, -1, 0
    };
    public static final int[] SHARPEN_KERNEL2_3 = new int[]{
            -1, -1, -1,
            -1,  9, -1,
            -1, -1, -1
    };
    public static final int[] EMBOSSING_KERNEL_3 = new int[]{
            0, 1, 0,
            -1, 0, 1,
            0, -1, 0
    };
    public static final int[] SOBEL_Y_KERNEL_3 = new int[]{
            1, 2, 1,
            0, 0, 0,
            -1, -2, -1
    };
    public static final int[] SOBEL_X_KERNEL_3 = new int[]{
            1, 0, -1,
            2, 0, -2,
            1, 0, -1
    };
    public static final int[] ROBERTS_1_KERNEL_2 = new int[]{
            1, 0,
            0, -1
    };
    public static final int[] ROBERTS_2_KERNEL_2 = new int[]{
            0, 1,
            -1, 0
    };
    public static final AreaFilter blackAndWhite = new AreaFilter(1, rgb -> {
        var intensity = ((int) (0.299 * rgb[0][0] + 0.587 * rgb[1][0] + 0.114 * rgb[2][0])) & 0xFF;
        return 0xFF000000 | intensity << 16 | intensity << 8 | intensity;
    });
    public static final AreaFilter inversion = new AreaFilter(1, Filter.forChannels(ch -> 0xFF - ch[0]));
    public static final AreaFilter gaussianBlur3 = new AreaFilter(3, rgb -> AreaFilter.convolution(rgb, AreaFilter.GAUSSIAN_BLUR_KERNEL_3, 16));
    public static final AreaFilter gaussianBlur5 = new AreaFilter(5, rgb -> AreaFilter.convolution(rgb, AreaFilter.GAUSSIAN_BLUR_KERNEL_5, 256));
    public static AreaFilter medianFilter(int diameter) {
        return new AreaFilter(diameter, Filter.forChannels(ch -> {
            Arrays.sort(ch);
            if (ch.length % 2 == 0)
                return (ch[ch.length/2] + ch[ch.length/2 - 1])/2;
            else
                return ch[ch.length/2];
        }));
    }
    public static final AreaFilter sharpen3 = new AreaFilter(3, rgb -> AreaFilter.convolution(rgb, AreaFilter.SHARPEN_KERNEL_3, 1));
    public static final AreaFilter sharpen3_2 = new AreaFilter(3, rgb -> AreaFilter.convolution(rgb, AreaFilter.SHARPEN_KERNEL2_3, 1));
    public static final AreaFilter emboss3 = new AreaFilter(3, rgb -> AreaFilter.convolution(rgb, AreaFilter.EMBOSSING_KERNEL_3, 1, 128));
}

class GammaFilter extends AreaFilter {
    double gamma = 1.0;

    public GammaFilter() {
        super(1 , null);
        f = Filter.forChannels((int[] channel) -> (int) (255 * Math.pow((double) channel[0] / 255, gamma)));
    };

    public void setGamma(double gamma) {
        this.gamma = gamma;
    }

    public double getGamma() {
        return gamma;
    }

    public static final GammaFilter gammaCorrection = new GammaFilter();
}

class BlurFilter extends AreaFilter {
    int diameter = 3;
    AreaFilter currentFilter = AreaFilter.gaussianBlur3;
    BlurFilter() {
        super(3, null);
    }

    public int getDiameter() {
        return diameter;
    }

    public void setDiameter(int size) {
        this.diameter = size;
        if (size == 3) currentFilter = AreaFilter.gaussianBlur3;
        else if (size == 5) currentFilter = AreaFilter.gaussianBlur5;
        else if (size == 7 | size == 9 | size == 11)
            currentFilter = AreaFilter.medianFilter(size);
    }

    @Override
    public void process(BufferedImage original, BufferedImage processed) {
        currentFilter.process(original, processed);
    }

    public static BlurFilter blur = new BlurFilter();
}

class SobelOperator extends AreaFilter {
    int threshold = 128;

    SobelOperator() {
        super(3, rgb -> (Filter.forChannels(ch -> {
            var g1 = Math.pow(innerProduct(ch, SOBEL_Y_KERNEL_3), 2);
            var g2 = Math.pow(innerProduct(ch, SOBEL_X_KERNEL_3), 2);
            var sqrt = (int) Math.sqrt(g1 + g2);
            return Filter.threshold(sqrt, 128, 0, 0xFF);
        }).apply(rgb) != 0xFF000000) ? 0xFFFFFFFF : 0xFF000000);
    }

    public int getThreshold() {
        return threshold;
    }

    public void setThreshold(int threshold) {
        this.threshold = threshold;
        f = rgb -> (Filter.forChannels(ch -> {
            var g1 = Math.pow(innerProduct(ch, SOBEL_Y_KERNEL_3), 2);
            var g2 = Math.pow(innerProduct(ch, SOBEL_X_KERNEL_3), 2);
            var sqrt = (int) Math.sqrt(g1 + g2);
            return Filter.threshold(sqrt, threshold, 0, 0xFF);
        }).apply(rgb) != 0xFF000000) ? 0xFFFFFFFF : 0xFF000000;
    }

    public static SobelOperator filter= new SobelOperator();
}

class RobertsOperator extends AreaFilter {
    int threshold = 128;
    static int[] chSub = new int[4];

    RobertsOperator() {
        super(3, rgb -> (Filter.forChannels(ch -> {
            chSub[0] = ch[4]; chSub[1] = ch[5]; chSub[2] = ch[7]; chSub[3] = ch[8];
            var g1 = Math.pow(innerProduct(chSub, ROBERTS_1_KERNEL_2), 2);
            var g2 = Math.pow(innerProduct(chSub, ROBERTS_2_KERNEL_2), 2);
            var sqrt = (int) Math.sqrt(g1 + g2);
            return Filter.threshold(128, sqrt, 0xFF, 0);
        }).apply(rgb)!= 0xFF000000) ? 0xFFFFFFFF : 0xFF000000);
    }

    public int getThreshold() {
        return threshold;
    }

    public void setThreshold(int threshold) {
        this.threshold = threshold;
        f = rgb -> (Filter.forChannels(ch -> {
            chSub[0] = ch[4]; chSub[1] = ch[5]; chSub[2] = ch[7]; chSub[3] = ch[8];
            var g1 = Math.pow(innerProduct(chSub, ROBERTS_1_KERNEL_2), 2);
            var g2 = Math.pow(innerProduct(chSub, ROBERTS_2_KERNEL_2), 2);
            var sqrt = (int) Math.sqrt(g1 + g2);
            return Filter.threshold(threshold, sqrt, 0xFF, 0);
        }).apply(rgb)!= 0xFF000000) ? 0xFFFFFFFF : 0xFF000000;
    }

    public static RobertsOperator filter = new RobertsOperator();
}

class AquarelisationFilter implements Filter {
    static int[] buf = new int[13];
    //private final static AreaFilter median = AreaFilter.medianFilter(3);
    private final static AreaFilter median = new AreaFilter(5, Filter.forChannels(ch -> {
        buf[0] = ch[2];
        buf[1] = ch[6];
        buf[2] = ch[7];
        buf[3] = ch[8];
        buf[4] = ch[10];
        buf[5] = ch[11];
        buf[6] = ch[12];
        buf[7] = ch[13];
        buf[8] = ch[14];
        buf[9] = ch[16];
        buf[10] = ch[17];
        buf[11] = ch[18];
        buf[12] = ch[22];

        Arrays.sort(buf);
        return buf[6];
    }));
    private final AreaFilter sharpen = AreaFilter.sharpen3;
    @Override
    public void process(BufferedImage original, BufferedImage processed) {
        var copy = FilterPanel.deepCopy(original);
        median.process(original, copy);
        sharpen.process(copy, processed);
    }
    public static AquarelisationFilter filter = new AquarelisationFilter();
}
class Erode extends AreaFilter {
    int diameter = 3;

    public Erode(int diameter) {
        super(diameter , rgb -> {
            int[] intensities = new int[diameter*diameter];
            for (int i = 0; i < diameter*diameter; i++) {
                intensities[i] = (int)(0.299 * rgb[0][i] + 0.587 * rgb[1][i] + 0.114 * rgb[2][i]);
                }

            var minOpt  = Arrays.stream(intensities)
                    .min();
            if (minOpt.isPresent()){
                int min = minOpt.getAsInt();
                return Filter.channelsToInt(min, min, min);
            } else
                return 0;
        });
        this.diameter = diameter;
    }

    public int getDiameter() {
        return diameter;
    }

    public static Erode filter = new Erode(3);
}
class Dilate extends AreaFilter {
    int diameter = 3;

    public Dilate(int diameter) {
        super(diameter , rgb -> {
            int[] intensities = new int[diameter*diameter];
            for (int i = 0; i < diameter*diameter; i++) {
                intensities[i] = (int)(0.299 * rgb[0][i] + 0.587 * rgb[1][i] + 0.114 * rgb[2][i]);
            }

            var maxOpt  = Arrays.stream(intensities)
                    .max();
            if (maxOpt.isPresent()){
                int max = maxOpt.getAsInt();
                return Filter.channelsToInt(max, max, max);
            } else
                return 0;
        });
        this.diameter = diameter;
    }

    public int getDiameter() {
        return diameter;
    }

    public static Dilate filter = new Dilate(3);
}