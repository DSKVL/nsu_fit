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
//scroll on open
//degs

/**
 * Main window class
 * @author Denis Koval
 */
public class InitMainWindow extends MainFrame {
	private static final int minimalWidth = 640;
	private static final int minimalHeight = 480;

	private final JScrollPane scrollPane;
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
			addMenuItem("File/New", "Create empty canvas", KeyEvent.VK_N, this::newCanvas);
			addMenuItem("File/Open", "Open a file", KeyEvent.VK_O, this::openFile);
			addMenuItem("File/Save", "Save a file", KeyEvent.CTRL_DOWN_MASK | KeyEvent.VK_S, this::saveFile);
			addMenuItem("File/Exit", "Exit application", KeyEvent.VK_X, "Exit.gif", this::onExit);

			addMenuItem("Help/About...", "Shows program version and copyright information", KeyEvent.VK_A, "About.gif", this::onAbout);

			ButtonGroup toolbarToolGr = new ButtonGroup();
			var lineMenuButton = addJRadioButtonMenuItem("Tools/Line", "Draw a line", KeyEvent.VK_L, this::setToolLineDrawer);
			lineMenuButton.setSelected(true);
			var spanMenuButton = addJRadioButtonMenuItem("Tools/Span", "Fill an area with color", KeyEvent.VK_S, this::setToolFiller);
			var shapeMenuButton = addJRadioButtonMenuItem("Tools/Shape", "Draw polygon or star", KeyEvent.VK_P, this::setToolShape);
			toolbarToolGr.add(lineMenuButton);
			toolbarToolGr.add(spanMenuButton);
			toolbarToolGr.add(shapeMenuButton);

			addMenuItem("Tools/Color", "Pick a color", KeyEvent.VK_C, this::chooseColor);
			addMenuItem("Tools/Clear", "Clears canvas", KeyEvent.VK_B, this::clearCanvas);

			ButtonGroup gr = new ButtonGroup();
			ButtonGroup toolbarColorGr = new ButtonGroup();
			var lineToolBarButton = addToolBarToggleButton("Tools/Line", gr);
			//lineToolBarButton.setIcon(new ImageIcon(getClass().getResource("line.gif")));
			gr.getElements().nextElement().getModel().setPressed(true);
			addToolBarSeparator();
			var spanToolBarButton =	addToolBarToggleButton("Tools/Span", gr);
			//spanToolBarButton.setIcon(new ImageIcon(getClass().getResource("span.gif")));
			addToolBarSeparator();
			var shapeToolBarButton = addToolBarToggleButton("Tools/Shape", gr);
			//shapeToolBarButton.setIcon(new ImageIcon(getClass().getResource("shape.gif")));
			addToolBarSeparator();
			lineToolBarButton.addActionListener(e->lineMenuButton.setSelected(true));
			lineMenuButton.addActionListener(e->lineToolBarButton.setSelected(true));
			spanToolBarButton.addActionListener(e->spanMenuButton.setSelected(true));
			spanMenuButton.addActionListener(e->spanToolBarButton.setSelected(true));
			shapeToolBarButton.addActionListener(e->shapeMenuButton.setSelected(true));
			shapeMenuButton.addActionListener(e->shapeToolBarButton.setSelected(true));
			addToolBarButton("Tools/Clear");
			addToolBarSeparator();

			colorButton = createToolBarButton("Tools/Color");
			colorButton.setForeground(Color.BLACK);
			colorButton.setBackground(Color.BLACK);
			toolBar.add(colorButton);

			editorPanel = new EditorPanel(minimalWidth, minimalHeight);
			addColor(Color.RED, toolbarColorGr);
			addColor(Color.GREEN, toolbarColorGr);
			addColor(Color.BLUE, toolbarColorGr);
			addColor(Color.CYAN, toolbarColorGr);
			addColor(Color.MAGENTA, toolbarColorGr);
			addColor(Color.YELLOW, toolbarColorGr);
			colorButton.addActionListener(e->toolbarColorGr.clearSelection());
			toolBar.addSeparator(new Dimension(50, 10));
			addToolBarButton("Help/About...");
			addToolBarSeparator();
			addToolBarButton("File/Exit");

			scrollPane = new JScrollPane(editorPanel);
			add(scrollPane);
			pack();
			setLocationRelativeTo(null);
		}
		catch(Exception e)
		{
			throw new RuntimeException(e);
		}
	}

	public void addColor(Color color, ButtonGroup gr) {
		var button = new JToggleButton();
		gr.add(button);
		button.setPreferredSize(new Dimension(25, 25));
		button.setForeground(color);
		button.setBackground(color);
		addToolBarSeparator();
		toolBar.add(button);
		button.addActionListener(e->{
			editorPanel.getTool().setColor(color);
			colorButton.setForeground(color);
			colorButton.setBackground(color);
		});
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
				scrollPane.revalidate();
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
		dialog.pack();
		dialog.setLocationRelativeTo(null);
		dialog.setVisible(true);

		if (canceled.get()) return;
		int width = (int) widthSpinner.getValue();
		int height = (int) heightSpinner.getValue();
		SwingUtilities.invokeLater(()-> {
			editorPanel.newCanvas(width, height);
			pack();
		});
	}

	public void setToolLineDrawer() {
		var dialog = new JDialog(this, "Set thickness", true);
		var layout = new GridLayout(2, 3);
		var label = new JLabel("Thickness");
		var slider = new JSlider(1, 32);
		var model = new SpinnerNumberModel(1, 1, 16, 1);
		var spinner = new JSpinner(model);
		slider.setValue(1);

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
		dialog.pack();
		dialog.setLocationRelativeTo(null);
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
		ShapeDrawer drawer = new ShapeDrawer();
		if (editorPanel.getTool() instanceof ShapeDrawer)
			drawer = (ShapeDrawer) editorPanel.getTool();

		JCheckBox isStarCheckbox = new JCheckBox();
		JSlider cornersSlider = new JSlider(3, 16),
				radiusSlider = new JSlider(1, 100),
				innerRadiusSlider = new JSlider(1, 100),
				angleSlider = new JSlider(0, 360);
		cornersSlider.setValue(drawer.getCorners());
		radiusSlider .setValue(drawer.getOuterRadius());
		innerRadiusSlider.setValue(drawer.getInnerRadius());
		angleSlider.setValue((int) (drawer.getAngle()*180/Math.PI));

		SpinnerModel cornerSpinnerModel = new SpinnerNumberModel(drawer.getCorners(), 3, 16, 1);
		SpinnerModel radiusSpinnerModel = new SpinnerNumberModel(drawer.getOuterRadius(), 3, 100, 1);
		SpinnerModel innerRadiusSpinnerModel = new SpinnerNumberModel(drawer.getInnerRadius(), 3, 100, 1);
		SpinnerModel angleSpinnerModel = new SpinnerNumberModel(angleSlider.getValue(), 0, 360, 1);

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
		dialog.pack();
		dialog.setLocationRelativeTo(null);
		dialog.setVisible(true);

		if (canceled.get()) return;

		if (isStarCheckbox.isSelected())
			editorPanel.setTool(new ShapeDrawer(
					cornersSlider.getValue(),
					radiusSlider.getValue(),
					innerRadiusSlider.getValue(),
					(float) (Math.PI*((float)angleSlider.getValue()/180))));
		else
			editorPanel.setTool(new ShapeDrawer(
					cornersSlider.getValue(),
					radiusSlider.getValue(),
					(float) (Math.PI*((float)angleSlider.getValue()/180))));
	}

	public void chooseColor() {
		Color currentColor = JColorChooser.showDialog(null, "Choose a color", Color.BLACK);
		colorButton.setForeground(currentColor);
		colorButton.setBackground(currentColor);
		editorPanel.getTool().setColor(currentColor);
	}

	public void clearCanvas() {
		SwingUtilities.invokeLater(()-> {
			editorPanel.clear();
			repaint();
		});
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
