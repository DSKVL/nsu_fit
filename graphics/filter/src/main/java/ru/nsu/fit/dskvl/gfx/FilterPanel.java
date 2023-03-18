package ru.nsu.fit.dskvl.gfx;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import javax.swing.*;

import static java.lang.Math.min;


//TODO while processing
// this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
//...
//this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));

//TODO gamma correction - two signs after point
//TODO Black-White filter - 0.299R + 0.587G + 0.114B (ARGB in int, rs 16, 8, 0 &0xFF

//Сверточные по каналам.
//TODO convolution filters - нормировка, границы. Для границ предварительно отнормируем матрицу,
//создадим вспомогательные для границ с другой нормировкой. Ну или просто зеркалим границы.

//Фильтры робертса и собеля - настройка порогов пользователем, потому что результат может оказаться большим.
//Тиснение домножить на 128

//Акварелизация - медианное усреднение(5х5) -> ядро резкости.

//Дизеринг. Флойд-стейнберг. Ошибка раньше времени не отсекается.
//Упорядоченный дизеринг.
/*Идея: изначально диапазон [0:255]. Делим пространство на некоторое
*
*/
public class FilterPanel extends JPanel {

    public int getCurrentWidth() {
        return currentWidth;
    }

    public int getCurrentHeight() {
        return currentHeight;
    }

    public BufferedImage getCanvas() {
        return canvas;
    }

    private BufferedImage canvas;
    private Tool tool = new LineDrawer();

    private int currentWidth;
    private int currentHeight;

    public void setCurrentWidth(int currentWidth) {
        this.currentWidth = currentWidth;
    }

    public void setCurrentHeight(int currentHeight) {
        this.currentHeight = currentHeight;
    }

    public FilterPanel(int width, int height)
    {
        currentWidth = width;
        currentHeight = height;
        canvas = new BufferedImage(currentWidth, currentHeight, BufferedImage.TYPE_INT_ARGB);

        clear();

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                SwingUtilities.invokeLater(() -> {
                    tool.onClick(e, canvas);
                    repaint();
                });
            }
        });
    }

    public void clear() {
        var g = canvas.getGraphics();
        g.setColor(Color.WHITE);
        g.fillRect(0,0,currentWidth, currentHeight);
        g.dispose();
    }

    public void setImage(BufferedImage canvas) {
        //TODO
        this.canvas = canvas;
        currentWidth = canvas.getWidth();
        currentHeight = canvas.getHeight();
        repaint();
    }

    public void changeViewedImage() {
        //TODO
    }

    public void clickImage(int x, int y) {
        //TODO
    }

    public void setTool(Tool tool) {
        tool.setColor(this.tool.getColor());
        this.tool = tool;
    }
    public Tool getTool() { return tool; }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(canvas, 0, 0, null);
    }
    @Override
    public Dimension getPreferredSize() {
        return new Dimension(currentWidth, currentHeight);
    }
}
