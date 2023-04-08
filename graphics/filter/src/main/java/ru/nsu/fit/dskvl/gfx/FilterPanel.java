package ru.nsu.fit.dskvl.gfx;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.*;

//НЕ ЗАБЫТЬ! SwingUtils.invokeLater();
//revalidate()
//Акварелизация - медианное усреднение(5х5) -> ядро резкости.

public class FilterPanel extends JPanel {
    private BufferedImage processedImage;
    private BufferedImage originalImage;
    private boolean showsProcessed = true;
    private boolean fits = false;

    private JImagePanel imagePanel;
    private Filter currentFilter;

    private int currentWidth;
    private int currentHeight;

    public FilterPanel(int width, int height) {
        currentWidth = width;
        currentHeight = height;

        try {
            var imgURL = getClass().getResource("defaultImage.jpg");
            if (imgURL == null) throw new IOException();
            processedImage = ImageIO.read(imgURL);
            originalImage = ImageIO.read(imgURL);
        } catch (IOException e) {
            System.err.println("Unable to open defaultImage.jpg");
        }
    }
    public void setImagePanel(JImagePanel imagePanel) {
        this.imagePanel = imagePanel;
        imagePanel.setImage(processedImage, true);
    }
    public BufferedImage getProcessedImage() { return processedImage; }

    public void setHint(Object obj) {
        imagePanel.setInterpolationHint(obj);
    }

    public static BufferedImage deepCopy(BufferedImage bi) {
        var cm = bi.getColorModel();
        boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
        var raster = bi.copyData(bi.getRaster().createCompatibleWritableRaster());
        return new BufferedImage(cm, raster, isAlphaPremultiplied, null);
    }

    public void setImage(BufferedImage canvas) {
        this.originalImage = canvas;
        this.processedImage = deepCopy(canvas);
        currentFilter.process(originalImage, processedImage);
        imagePanel.setImage(originalImage, true);
        showsProcessed = false;

        currentWidth = canvas.getWidth();
        currentHeight = canvas.getHeight();
    }

    public void changeViewedImage() {
        imagePanel.setImage(showsProcessed ? originalImage : processedImage, false);
        showsProcessed = !showsProcessed;
    }

    public void clickImage(int x, int y) {
        //TODO
    }

    public void changeFit() {
        if (!fits) {
            imagePanel.realSize();
            fits = !fits;
            return;
        }
        imagePanel.fitScreen();
        fits = !fits;
    }

    public void setCurrentFilter(Filter currentFilter) {
        this.currentFilter = currentFilter;
        currentFilter.process(originalImage, processedImage);
        imagePanel.getScrollPane().paintAll(imagePanel.getScrollPane().getGraphics());
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(currentWidth, currentHeight);
    }
}
