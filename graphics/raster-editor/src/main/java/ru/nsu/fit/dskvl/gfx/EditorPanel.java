package ru.nsu.fit.dskvl.gfx;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import javax.swing.*;

import static java.lang.Math.min;

public class EditorPanel extends JPanel {
	public void setCanvas(BufferedImage canvas) {
		this.canvas = canvas;
		currentWidth = canvas.getWidth();
		currentHeight = canvas.getHeight();
		repaint();
	}

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

	public EditorPanel(int width, int height)
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

	public void newCanvas(int width, int height) {
		int oldWidth = canvas.getWidth();
		int oldHeight = canvas.getHeight();
		var oldCanvas = canvas;
		canvas = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		this.currentWidth = width;
		this.currentHeight = height;
		clear();

		for (int y = 0; y < min(oldHeight, height); y++)
			for (int x = 0; x < min(oldWidth, width); x++)
				canvas.setRGB(x, y, oldCanvas.getRGB(x, y));
		repaint();
	}

	public void setTool(Tool tool) {
		tool.setColor(this.tool.getColor());
		this.tool = tool;
	}
	public Tool getTool() { return tool; }
	/**
	 * Performs actual component drawing
	 * @param g Graphics object to draw component to
	 */
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
