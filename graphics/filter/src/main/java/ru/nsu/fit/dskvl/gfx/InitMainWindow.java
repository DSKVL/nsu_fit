package ru.nsu.fit.dskvl.gfx;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

import static javax.swing.JFileChooser.APPROVE_OPTION;

public class InitMainWindow extends MainFrame {
    private static final int minimalWidth = 640;
    private static final int minimalHeight = 480;

    private final JScrollPane scrollPane;
    private final FilterPanel filterPanel;

    private final ButtonGroup menuGroup = new ButtonGroup();
    private final ButtonGroup toolbarGroup = new ButtonGroup();

    public InitMainWindow() {
        super(minimalWidth, minimalHeight, "Init application");

        try {
            addSubMenu("File", KeyEvent.VK_F);
            addSubMenu("Image", KeyEvent.VK_I);
            addSubMenu("Filters", KeyEvent.VK_T);
            addSubMenu("Help", KeyEvent.VK_H);
            addMenuItem("File/Open", "Open a file", KeyEvent.VK_O, this::openFile);
            addMenuItem("File/Real size", "Fit to screen or real size", 0, this::changeView);
            addMenuItem("File/Save", "Save a file", KeyEvent.CTRL_DOWN_MASK | KeyEvent.VK_S, this::saveFile);
            addMenuItem("File/Exit", "Exit application", KeyEvent.VK_X, "Exit.gif", this::onExit);
            addMenuItem("Image/Interpolation", "Choose interpolation option", 0, null, this::interpolationMode);
            addMenuItem("Help/About...", "Shows program version and copyright information", KeyEvent.VK_A, "About.gif", this::onAbout);


            scrollPane = new JScrollPane();
            add(scrollPane);
            setLocationRelativeTo(null);
            pack();
            setVisible(true);

            filterPanel = new FilterPanel(minimalWidth, minimalHeight);
            var imagePanel = new JImagePanel(scrollPane, filterPanel);
            filterPanel.setImagePanel(imagePanel);


            addFilter("Black and white", "Converts to black and white", 0, setUpFilter(AreaFilter.blackAndWhite));
            addFilter("Inversion", "Inverses intensities", 0, setUpFilter(AreaFilter.inversion));
            addFilter("Blur", "Blurs the image", KeyEvent.VK_B, this::setUpBlur);
            addFilter("Sharpen", "Sharpens the image", 0, setUpFilter(AreaFilter.sharpen3));
            addFilter("Emboss", "Embosses the image", 0, setUpFilter(AreaFilter.emboss3 ));
            addFilter("Gamma Correction", "Corrects gamma", 0, this::setUpGammaCorrection);
            addFilter("Roberts", "Border detection", 0, this::setUpRobertsOperator);
            addFilter("Sobel", "Border detection", 0, this::setUpSobelOperator);
            addFilter("Dithering", "Dithers", 0, this::setUpOrderedDithering);
            addFilter("Floyd-Steinberg", "Dithers", 0, this::setUpUnorderedDithering);
            addFilter("Rotate", "Rotates", 0, this::setupRotate);
            addFilter("Aquarelizsation", "Nice filter", 0, setUpFilter(AquarelisationFilter.filter));
            addFilter("Erode", "Erodes", 0, this::setUpErode);
            addFilter("Dilate", "Dilates", 0, this::setUpDilate);


            addToolBarSeparator();
            addToolBarToggleButton("File/Real size");
            addToolBarButton("Help/About...");
            addToolBarButton("File/Exit");
            pack();
        }
        catch(Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void changeView() {
        filterPanel.changeFit();
    }

    public void interpolationMode() {
        Object[] options = new Object[]{"Bilinear", "Bicubic", "Nearest"};
        int parameter = JOptionPane.showOptionDialog(null, "Interpolation options",
                "Interpolation",
                JOptionPane.DEFAULT_OPTION, JOptionPane.DEFAULT_OPTION, null, options, options[0]);
        switch (parameter) {
            case 1 -> filterPanel.setHint(RenderingHints.VALUE_INTERPOLATION_BICUBIC);
            case 2-> filterPanel.setHint(RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
            default ->  filterPanel.setHint(RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        }
    }

    private BoundedRangeModel spinnerNMtoSliderNM(SpinnerNumberModel model) {
        if (model.getValue() instanceof Integer) {
            var length =  ((Integer) model.getMaximum() - (Integer) model.getMinimum())/(Integer) model.getStepSize();
            var value =  ((Integer) model.getValue() - (Integer) model.getMinimum())/(Integer) model.getStepSize();
            return new DefaultBoundedRangeModel(value, 1, 0, length+1);
        } else if (model.getValue() instanceof Double) {
            var length = (int) (((Double)  model.getMaximum() - (Double) model.getMinimum())/(Double) model.getStepSize());
            var value = (int) (((Double) model.getValue() - (Double) model.getMinimum())/(Double) model.getStepSize());
            return new DefaultBoundedRangeModel(value, 1, 0, length);
        }
        return null;
    }

    private void bindSliderAndSpinner(JSlider slider, JSpinner spinner, SpinnerNumberModel model) {
        if (model.getValue() instanceof Integer) {
            slider.addChangeListener(e -> spinner.setValue(slider.getValue()*(Integer) model.getStepSize()
                    + (Integer) model.getMinimum()));
            spinner.addChangeListener(e -> slider.setValue(((Integer) model.getValue()
                    - (Integer) model.getMinimum())/ (Integer) model.getStepSize()));
        } else if (model.getValue() instanceof Double) {
            slider.addChangeListener(e -> spinner.setValue(slider.getValue()*(Double) model.getStepSize()
                    + (Double) model.getMinimum()));
            spinner.addChangeListener(e -> slider.setValue((int)(((Double) spinner.getValue()
                    - (Double) model.getMinimum())/ (Double) model.getStepSize())));
        }
    }

    private Optional<Object[]> showDialog(String title, String[] parameters, SpinnerNumberModel[] models) {
        var parametersCount = parameters.length;
        var dialog = new JDialog(this, title, true);
        var layout = new GridLayout(1 + parametersCount, 3, 10, 15);
        var labels = new JLabel[parametersCount];
        var spinners = new JSpinner[parametersCount];
        var sliders = new JSlider[parametersCount];
        for (int i = 0; i < parametersCount; i++) {
            labels[i] = new JLabel(parameters[i]);
            spinners[i] = new JSpinner(models[i]);
            sliders[i] = new JSlider(spinnerNMtoSliderNM(models[i]));
            bindSliderAndSpinner(sliders[i], spinners[i], models[i]);
        }

        JButton okButton = new JButton ("OK"),
                cancelButton = new JButton("Cancel");
        okButton.addActionListener ( e -> dialog.setVisible(false));
        AtomicReference<Boolean> canceled = new AtomicReference<>(false);
        cancelButton.addActionListener(e->{
            canceled.set(true);
            dialog.setVisible(false);
        });


        var panel = new JPanel(layout);
        int eb = 10;
        panel.setBorder(BorderFactory.createEmptyBorder(eb, eb, eb, eb));
        for (int i = 0; i < parametersCount; i++) {
            panel.add(labels[i]);
            panel.add(spinners[i]);
            panel.add(sliders[i]);
        }
        panel.add(okButton);
        panel.add(cancelButton);
        dialog.add(panel);
        dialog.pack();
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);

        if (canceled.get()) return Optional.empty();
        var res = new Object[parametersCount];
        for (int i = 0; i < parametersCount; i++)
            res[i] = spinners[i].getValue();
        return Optional.of(res);
    }

    private void setUpBlur() {
        var model = new SpinnerNumberModel(BlurFilter.blur.getDiameter(), 3, 11, 2);

        showDialog("Kernel size", new String[]{"Size"}, new SpinnerNumberModel[]{model}).ifPresent(
                kernelSize -> SwingUtilities.invokeLater(()->{
                    var size = (int) kernelSize[0];
                    this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

                    BlurFilter.blur.setDiameter(size);
                    filterPanel.setCurrentFilter(BlurFilter.blur);

                    this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                })
        );
    }

    public void setUpGammaCorrection() {
        var model = new SpinnerNumberModel(GammaFilter.gammaCorrection.getGamma(), 0.01, 3.0, 0.01);

        showDialog("Gamma correction", new String[]{"Gamma"}, new SpinnerNumberModel[]{model}).ifPresent(
                gamma -> SwingUtilities.invokeLater(()->{
                    this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                    GammaFilter.gammaCorrection.setGamma((Double) gamma[0]);
                    filterPanel.setCurrentFilter(GammaFilter.gammaCorrection);
                    this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                })
        );
    }

    public void setUpSobelOperator() {
        var model = new SpinnerNumberModel(SobelOperator.filter.getThreshold(), 1, 255, 1);

        showDialog("Sobel operator", new String[]{"Threshold"}, new SpinnerNumberModel[]{model}).ifPresent(
                threshold -> SwingUtilities.invokeLater(()->{
                    this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                    SobelOperator.filter.setThreshold((int) threshold[0]);
                    filterPanel.setCurrentFilter(SobelOperator.filter);
                    this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                })
        );
    }

    public void setUpRobertsOperator() {
        var model = new SpinnerNumberModel(RobertsOperator.filter.getThreshold(), 1, 255, 1);

        showDialog("Roberts operator", new String[]{"Threshold"}, new SpinnerNumberModel[]{model}).ifPresent(
                threshold -> SwingUtilities.invokeLater(()->{
                    this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                    RobertsOperator.filter.setThreshold((int) threshold[0]);
                    filterPanel.setCurrentFilter(RobertsOperator.filter);
                    this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                })
        );
    }

    public void setUpOrderedDithering() {
        var colors = DitheringFilter.orderedDithering.getColors();
        var r = new SpinnerNumberModel(colors[0], 2, 128, 1);
        var g = new SpinnerNumberModel(colors[1], 2, 128, 1);
        var b = new SpinnerNumberModel(colors[2], 2, 128, 1);

        showDialog("Ordered dithering", new String[]{"Reds", "Greens", "Blues"}, new SpinnerNumberModel[]{r, g, b}).ifPresent(
                colorsObj -> SwingUtilities.invokeLater(()->{
                    this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                    var cols = new int[3];
                    for (int i = 0; i < 3; i++)
                        cols[i] = (int) colorsObj[i];
                    DitheringFilter.orderedDithering.setColors(cols);
                    filterPanel.setCurrentFilter(DitheringFilter.orderedDithering);
                    this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                })
        );
    }

    public void setUpUnorderedDithering() {
        var colors = DitheringFilter.unorderedDithering.getColors();
        var r = new SpinnerNumberModel(colors[0], 2, 128, 1);
        var g = new SpinnerNumberModel(colors[1], 2, 128, 1);
        var b = new SpinnerNumberModel(colors[2], 2, 128, 1);

        showDialog("Unordered dithering", new String[]{"Reds", "Greens", "Blues"}, new SpinnerNumberModel[]{r, g, b}).ifPresent(
                colorsObj -> SwingUtilities.invokeLater(()->{
                    this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                    var cols = new int[3];
                    for (int i = 0; i < 3; i++)
                        cols[i] = (int) colorsObj[i];

                    DitheringFilter.unorderedDithering.setColors(cols);
                    filterPanel.setCurrentFilter(DitheringFilter.unorderedDithering);
                    this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                })
        );
    }

    private void setupRotate() {
        var angleModel = new SpinnerNumberModel(Rotate.filter.getAngle(), 0, 360, 1);

        showDialog("Rotate", new String[]{"Angle"}, new SpinnerNumberModel[]{angleModel}).ifPresent(
                angleObj -> SwingUtilities.invokeLater(()->{
                    this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                    int degrees = (Integer) angleObj[0];
                    Rotate.filter.setAngle(degrees);
                    filterPanel.setCurrentFilter(Rotate.filter);
                    this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                })
        );
    }

    private void setUpErode() {
        var sizeModel = new SpinnerNumberModel(Erode.filter.getDiameter(), 1, 13, 2);

        showDialog("Erode", new String[]{"Size"}, new SpinnerNumberModel[]{sizeModel}).ifPresent(
                sizeObj -> SwingUtilities.invokeLater(()->{
                    this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                    int size = (Integer) sizeObj[0];
                    Erode.filter = new Erode(size);
                    filterPanel.setCurrentFilter(Erode.filter);
                    this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                })
        );
    }

    private void setUpDilate() {
        var sizeModel = new SpinnerNumberModel(Dilate.filter.getDiameter(), 1, 13, 2);

        showDialog("Dilate", new String[]{"Size"}, new SpinnerNumberModel[]{sizeModel}).ifPresent(
                sizeObj -> SwingUtilities.invokeLater(()->{
                    this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                    int size = (Integer) sizeObj[0];
                    Dilate.filter = new Dilate(size);
                    filterPanel.setCurrentFilter(Dilate.filter);
                    this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                })
        );
    }

    private Runnable setUpFilter(Filter filter) {
        return () -> SwingUtilities.invokeLater(() -> {
            this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            filterPanel.setCurrentFilter(filter);
            this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        });
    }

    public void addFilter(String name, String tooltip, int hotkey, Runnable setUpFilter) {
        var menuButton = addJRadioButtonMenuItem("Filters/" + name, tooltip, hotkey, setUpFilter);
        menuGroup.add(menuButton);
        var toolBarButton = addToolBarToggleButton("Filters/" + name);
        toolbarGroup.add(toolBarButton);
        toolBarButton.addActionListener(e->menuButton.setSelected(true));
        menuButton.addActionListener(e->toolBarButton.setSelected(true));
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
                filterPanel.setImage(ImageIO.read(file));
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
        var candidateFile = chooser.getSelectedFile();

        var file = (candidateFile.getAbsolutePath().endsWith(".png")) ? candidateFile
                    : new File(candidateFile.getAbsolutePath().concat(".png"));
        SwingUtilities.invokeLater(() ->{
            try {
                ImageIO.write(filterPanel.getProcessedImage(),
                        "png", file);
            } catch (IOException e) {
                System.err.println("Unable to open");
            }
        });
    }

    public void onAbout() { JOptionPane.showMessageDialog(this,
      "Притворимся, что тут что-то полезное. Копирайт Денис Коваль 20202.", "About Init", JOptionPane.INFORMATION_MESSAGE); }
    public void onExit()
    {
        System.exit(0);
    }
    public static void main(String[] args)  { new InitMainWindow(); }
}
