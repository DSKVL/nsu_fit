package ru.nsu.fit.dskvl.gfx;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicReference;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

import static javax.swing.JFileChooser.APPROVE_OPTION;
//scrolls
//radiobutton
//resizeexpanding
//centeroptions
//holes in shapes
/**
 * Main window class
 * @author Denis Koval
 */
public class InitMainWindow extends MainFrame {
	private static final int minimalWidth = 640;
	private static final int minimalHeight = 480;


	private final EditorPanel editorPanel;
	private final JButton colorButton;
	public InitMainWindow()
	{
		super(minimalWidth, minimalHeight, "Init application");

		try
		{
			addSubMenu("File", KeyEvent.VK_F);
			addSubMenu("Tools", KeyEvent.VK_T);
			addSubMenu("Help", KeyEvent.VK_H);
			addMenuItem("File/New", "Create empty canvas", KeyEvent.VK_N, "newCanvas");
			addMenuItem("File/Open", "Open a file", KeyEvent.VK_O, "openFile");
			addMenuItem("File/Save", "Save a file", KeyEvent.CTRL_DOWN_MASK | KeyEvent.VK_S, "saveFile");
			addMenuItem("File/Exit", "Exit application", KeyEvent.VK_X, "Exit.gif", "onExit");

			addMenuItem("Help/About...", "Shows program version and copyright information", KeyEvent.VK_A, "About.gif", "onAbout");

			addMenuItem("Tools/Line", "Draw a line", KeyEvent.VK_L, "setToolLineDrawer");
			addMenuItem("Tools/Span", "Fill an area with color", KeyEvent.VK_S, "setToolFiller");
			addMenuItem("Tools/Shape", "Draw polygon or star", KeyEvent.VK_P, "setToolShape");
			addMenuItem("Tools/Color", "Pick a color", KeyEvent.VK_C, "chooseColor");
			addMenuItem("Tools/Clear", "Clears canvas", KeyEvent.VK_B, "clearCanvas");

			ButtonGroup gr = new ButtonGroup();
			addToolBarRadioButton("Tools/Line", gr);
			gr.getElements().nextElement().setSelected(true);
			addToolBarSeparator();
			addToolBarRadioButton("Tools/Span", gr);
			addToolBarSeparator();
			addToolBarRadioButton("Tools/Shape", gr);
			addToolBarSeparator();
			addToolBarButton("Tools/Clear");
			addToolBarSeparator();
			addToolBarSeparator();
			addToolBarSeparator();

			colorButton = createToolBarButton("Tools/Color");
			colorButton.setForeground(Color.BLACK);
			colorButton.setBackground(Color.BLACK);
			toolBar.add(colorButton);

			var redButton = new JButton();
			redButton.setForeground(Color.RED);
			redButton.setBackground(Color.RED);

			var greenButton = new JButton();
			greenButton.setForeground(Color.GREEN);
			greenButton.setBackground(Color.GREEN);

			var blueButton = new JButton();
			blueButton.setForeground(Color.BLUE);
			blueButton.setBackground(Color.BLUE);
			var cyanButton = new JButton();
			cyanButton.setForeground(Color.CYAN);
			cyanButton.setBackground(Color.CYAN);
			var yellowButton = new JButton();
			yellowButton.setForeground(Color.YELLOW);
			yellowButton.setBackground(Color.YELLOW);
			var magentaButton = new JButton();
			magentaButton.setForeground(Color.MAGENTA);
			magentaButton.setBackground(Color.MAGENTA);

			addToolBarSeparator();
			toolBar.add(redButton);
			addToolBarSeparator();
			toolBar.add(greenButton);
			addToolBarSeparator();
			toolBar.add(blueButton);
			addToolBarSeparator();
			toolBar.add(cyanButton);
			addToolBarSeparator();
			toolBar.add(yellowButton);
			addToolBarSeparator();
			toolBar.add(magentaButton);
			addToolBarSeparator();
			addToolBarSeparator();
			addToolBarSeparator();
			addToolBarSeparator();


			addToolBarButton("Help/About...");
			addToolBarSeparator();
			addToolBarButton("File/Exit");

			editorPanel = new EditorPanel(minimalWidth, minimalHeight);
			redButton.addActionListener(e->{
				editorPanel.getTool().setColor(Color.RED);
				colorButton.setForeground(Color.RED);
				colorButton.setBackground(Color.RED);
			});
			greenButton.addActionListener(e->{
				editorPanel.getTool().setColor(Color.GREEN);
				colorButton.setForeground(Color.GREEN);
				colorButton.setBackground(Color.GREEN);
			});
			blueButton.addActionListener(e->{
				editorPanel.getTool().setColor(Color.BLUE);
				colorButton.setForeground(Color.BLUE);
				colorButton.setBackground(Color.BLUE);
			});
			cyanButton.addActionListener(e->{
				editorPanel.getTool().setColor(Color.CYAN);
				colorButton.setForeground(Color.CYAN);
				colorButton.setBackground(Color.CYAN);
			});
			yellowButton.addActionListener(e->{
				editorPanel.getTool().setColor(Color.YELLOW);
				colorButton.setForeground(Color.YELLOW);
				colorButton.setBackground(Color.YELLOW);
			});
			magentaButton.addActionListener(e->{
				editorPanel.getTool().setColor(Color.MAGENTA);
				colorButton.setForeground(Color.MAGENTA);
				colorButton.setBackground(Color.MAGENTA);

			});
			add(editorPanel);
		}
		catch(Exception e)
		{
			throw new RuntimeException(e);
		}
	}

	public void openFile() {
		var chooser = new JFileChooser();
		FileNameExtensionFilter filter = new FileNameExtensionFilter("Image Files", "jpg", "png", "gif", "jpeg", "bmp");
		chooser.setFileFilter(filter);
		var ret = chooser.showOpenDialog(this);
		if (ret != APPROVE_OPTION)
			return;
		var file = chooser.getSelectedFile();
		SwingUtilities.invokeLater(() ->{
			try {
				editorPanel.setCanvas(ImageIO.read(file));
			} catch (IOException e) {
				System.err.println("Unable to open");
			}
		});
	}

	public void saveFile() {
		var chooser = new JFileChooser();
		FileNameExtensionFilter filter = new FileNameExtensionFilter("Image Files", "jpg", "png", "gif", "jpeg", "bmp");
		chooser.setFileFilter(filter);
		var ret = chooser.showSaveDialog(this);
		if (ret != APPROVE_OPTION)
			return;
		var file = chooser.getSelectedFile();
		if(!file.getAbsolutePath().endsWith(".png")) {
			file = new File(file.getAbsolutePath().concat(".png"));
		}
		java.io.File finalFile = file;
		SwingUtilities.invokeLater(() ->{
			try {
				ImageIO.write(editorPanel.getCanvas(),
							"png",
						finalFile);
			} catch (IOException e) {
				System.err.println("Unable to open");
			}
		});
	}

	public void newCanvas() {
		var dialog = new JDialog(this, "Set thickness", true);
		var layout = new GridLayout(2, 2);
		JSpinner heightSpinner = new JSpinner(),
		     	 widthSpinner = new JSpinner();

		heightSpinner.setValue(editorPanel.getCurrentHeight());
		widthSpinner.setValue(editorPanel.getCurrentWidth());


		JButton okButton = new JButton ("OK"),
				cancelButton = new JButton("Cancel");
		okButton.addActionListener ( e -> dialog.setVisible(false));
		AtomicReference<Boolean> canceled = new AtomicReference<>(false);
		cancelButton.addActionListener(e->{
			canceled.set(true);
			dialog.setVisible(false);
		});

		dialog.setLayout(layout);
		dialog.add(widthSpinner);
		dialog.add(heightSpinner);
		dialog.add(okButton);
		dialog.add(cancelButton);
		dialog.setSize(300,120);
		dialog.setVisible(true);

		if (canceled.get()) return;
		int width = (int) widthSpinner.getValue();
		int height = (int) heightSpinner.getValue();
		SwingUtilities.invokeLater(()-> editorPanel.newCanvas(width, height));
	}

	public void setToolLineDrawer() {
		var dialog = new JDialog(this, "Set thickness", true);
		var layout = new GridLayout(2, 3);
		var label = new JLabel("Thickness");
		var slider = new JSlider(1, 32);
		var model = new SpinnerNumberModel(1, 1, 16, 1);
		var spinner = new JSpinner(model);

		slider.addChangeListener(e->spinner.setValue(slider.getValue()));
		spinner.addChangeListener(e->slider.setValue((int) spinner.getValue()));
		slider.setPaintTicks(true);
		JButton okButton = new JButton ("OK"),
				cancelButton = new JButton("Cancel");
		okButton.addActionListener ( e -> dialog.setVisible(false));
		AtomicReference<Boolean> canceled = new AtomicReference<>(false);
		cancelButton.addActionListener(e->{
			canceled.set(true);
			dialog.setVisible(false);
		});

		dialog.setLayout(layout);
		dialog.add(label);
		dialog.add(slider);
		dialog.add(spinner);
		dialog.add(okButton);
		dialog.add(cancelButton);
		dialog.setSize(300,120);
		dialog.setVisible(true);

		if (canceled.get()) return;

		editorPanel.setTool(new LineDrawer(slider.getValue()));
	}

	public void setToolFiller() {
		editorPanel.setTool(new Filler());
	}

	public void setToolShape() {
		var dialog = new JDialog(this, "Shape drawer", true);
		var layout = new GridLayout(6, 2);
		JLabel corners = new JLabel("Corners"),
				radius = new JLabel("Radius"),
				isStar = new JLabel("Is a star"),
				innerRadius = new JLabel("Inner radius"),
				angle = new JLabel("Angle");
		JCheckBox isStarCheckbox = new JCheckBox();
		JSlider cornersSlider = new JSlider(3, 16),
				radiusSlider = new JSlider(1, 100),
				innerRadiusSlider = new JSlider(1, 100),
				angleSlider = new JSlider(0, 100);

		SpinnerModel cornerSpinnerModel = new SpinnerNumberModel(3, 3, 16, 1);
		SpinnerModel radiusSpinnerModel = new SpinnerNumberModel(30, 3, 100, 1);
		SpinnerModel innerRadiusSpinnerModel = new SpinnerNumberModel(10, 3, 100, 1);
		SpinnerModel angleSpinnerModel = new SpinnerNumberModel(0, 0, 100, 1);

		JSpinner cornerSpinner = new JSpinner(cornerSpinnerModel),
				 radiusSpinner = new JSpinner(radiusSpinnerModel),
				 innerRadiusSpinner = new JSpinner(innerRadiusSpinnerModel),
				 angleSpinner = new JSpinner(angleSpinnerModel);

		cornerSpinner.addChangeListener(e->cornersSlider.setValue((Integer) cornerSpinner.getValue()));
		radiusSpinner.addChangeListener(e->radiusSlider.setValue((Integer) radiusSpinner.getValue()));
		innerRadiusSpinner.addChangeListener(e->innerRadiusSlider.setValue((Integer) innerRadiusSpinner.getValue()));
		angleSpinner.addChangeListener(e->angleSlider.setValue((Integer) angleSpinner.getValue()));

		cornersSlider.addChangeListener(e->cornerSpinner.setValue(cornersSlider.getValue()));
		radiusSlider.addChangeListener(e->radiusSpinner.setValue(radiusSlider.getValue()));
		innerRadiusSlider.addChangeListener(e->innerRadiusSpinner.setValue(innerRadiusSlider.getValue()));
		angleSlider.addChangeListener(e->angleSpinner.setValue(angleSlider.getValue()));

		cornersSlider.setPaintTicks(true);
		radiusSlider.setPaintTicks(true);
		innerRadiusSlider.setPaintTicks(true);
		angleSlider.setPaintTicks(true);

		JButton okButton = new JButton ("OK"),
				cancelButton = new JButton("Cancel");
		okButton.addActionListener ( e -> dialog.setVisible(false));
		AtomicReference<Boolean> canceled = new AtomicReference<>(false);
		cancelButton.addActionListener(e->{
			canceled.set(true);
			dialog.setVisible(false);
		});

		dialog.setLayout(layout);
		dialog.add(corners);
		dialog.add(cornersSlider);
		dialog.add(cornerSpinner);
		dialog.add(radius);
		dialog.add(radiusSlider);
		dialog.add(radiusSpinner);
		dialog.add(isStar);
		dialog.add(isStarCheckbox);
		dialog.add(new JLabel(""));
		dialog.add(innerRadius);
		dialog.add(innerRadiusSlider);
		dialog.add(innerRadiusSpinner);
		dialog.add(angle);
		dialog.add(angleSlider);
		dialog.add(angleSpinner);
		dialog.add(okButton);
		dialog.add(cancelButton);
		dialog.setSize(400,300);
		dialog.setVisible(true);

		if (canceled.get()) return;

		if (isStarCheckbox.isSelected())
			editorPanel.setTool(new ShapeDrawer(
					cornersSlider.getValue(),
					radiusSlider.getValue(),
					innerRadiusSlider.getValue(),
					(float) (2*Math.PI*((float)angleSlider.getValue()/100))));
		else
			editorPanel.setTool(new ShapeDrawer(
					cornersSlider.getValue(),
					radiusSlider.getValue(),
					(float) (2*Math.PI*((float)angleSlider.getValue()/100))));
	}

	public void chooseColor() {
		Color currentColor = JColorChooser.showDialog(null, "Choose a color", Color.BLACK);
		colorButton.setForeground(currentColor);
		colorButton.setBackground(currentColor);
		editorPanel.getTool().setColor(currentColor);
	}

	public void clearCanvas() {
		editorPanel.clear();
	}

	/**
	 * Help/About... - shows program version and copyright information
	 */
	public void onAbout()
	{
		JOptionPane.showMessageDialog(this, "Притворимся, что тут что-то полезное. Копирайт Денис Коваль 20202.", "About Init", JOptionPane.INFORMATION_MESSAGE);
	}
	
	/**
	 * File/Exit - exits application
	 */
	public void onExit()
	{
		System.exit(0);
	}
	
	/**
	 * Application main entry point
	 * @param args command line arguments (unused)
	 */
	public static void main(String[] args)
	{
		InitMainWindow mainFrame = new InitMainWindow();
		mainFrame.setVisible(true);
	}
}
