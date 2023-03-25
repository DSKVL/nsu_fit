package ru.nsu.fit.dskvl.gfx;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.*;

//НЕ ЗАБЫТЬ! SwingUtils.invokeLater();
//revalidate()
//TODO while processing
// this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
//...
//this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));

//Акварелизация - медианное усреднение(5х5) -> ядро резкости.

//Дизеринг. Флойд-стейнберг. Ошибка раньше времени не отсекается.
//Упорядоченный дизеринг.
/*Идея: изначально диапазон [0:255]. Делим пространство на некоторое
* МАТРИЦА НОРМИРОВАННАЯ, К ИНЕДКСУ LUT r=255/(N-1), N- число цветов
* c_new = lut[c_old + r*(M(ij mod n )-1/2)]

 */
public class FilterPanel extends JPanel {
    private BufferedImage processedImage;
    private BufferedImage originalImage;
    private boolean showsProcessed = true;
    private boolean fits = true;

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

//    public void changeFit() {
//        if (fits) {
//            imagePanel.realSize();
//            return;
//        }
//        imagePanel.fitScreen();
//    }

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
