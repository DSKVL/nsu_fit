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
            addMenuItem("File/Save", "Save a file", KeyEvent.CTRL_DOWN_MASK | KeyEvent.VK_S, this::saveFile);
            addMenuItem("File/Exit", "Exit application", KeyEvent.VK_X, "Exit.gif", this::onExit);
            addMenuItem("Help/About...", "Shows program version and copyright information", KeyEvent.VK_A, "About.gif", this::onAbout);


            scrollPane = new JScrollPane();
            add(scrollPane);
            setLocationRelativeTo(null);
            pack();
            setVisible(true);

            filterPanel = new FilterPanel(minimalWidth, minimalHeight);
            var imagePanel = new JImagePanel(scrollPane, filterPanel);
            filterPanel.setImagePanel(imagePanel);


            //addJRadioButtonMenuItem("Image/Fit", "Change image size", KeyEvent.VK_P, filterPanel::changeFit);

            addFilter("Black and white", "Converts to black and white", 0, setUpFilter(AreaFilter.blackAndWhite));
            addFilter("Inversion", "Inverses intensities", 0, setUpFilter(AreaFilter.inversion));
            addFilter("Blur", "Blurs the image", KeyEvent.VK_B, this::setUpBlur);
            addFilter("Sharpen", "Sharpens the image", 0, setUpFilter(AreaFilter.sharpen3));
            addFilter("Emboss", "Embosses the image", 0, setUpFilter(AreaFilter.emboss3 ));
            addFilter("Gamma Correction", "Corrects gamma", 0, this::setUpGammaCorrection);
            addFilter("Roberts", "Border detection", 0, this::setUpRobertsOperator);
            addFilter("Sobel", "Border detection", 0, this::setUpSobelOperator);
            addFilter("Dithering", "DIthers", 0, this::setUpDithering);

            addToolBarSeparator();
            addToolBarButton("Help/About...");
            addToolBarButton("File/Exit");
            pack();
        }
        catch(Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Optional<Object> oneParametricDialog(String title, String parameter, SpinnerNumberModel model) {
        var dialog = new JDialog(this, title, true);
        var layout = new GridLayout(2, 3);
        var label = new JLabel(parameter);
        var spinner = new JSpinner(model);

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
        dialog.add(spinner);
        dialog.add(okButton);
        dialog.add(cancelButton);
        dialog.setSize(300,120);
        dialog.pack();
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);

        if (canceled.get()) return Optional.empty();
        return Optional.of(spinner.getValue());
    }

    private void setUpBlur() {
        var model = new SpinnerNumberModel(3, 3, 11, 2);

        oneParametricDialog("Kernel size", "Size", model).ifPresent(
                kernelSize -> SwingUtilities.invokeLater(()->{
                    var size = (int) kernelSize;
                    this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

                    if (size == 3) filterPanel.setCurrentFilter(AreaFilter.gaussianBlur3);
                    else if (size == 5) filterPanel.setCurrentFilter(AreaFilter.gaussianBlur5);
                    else if (size == 7 | size == 9 | size == 11)
                        filterPanel.setCurrentFilter(AreaFilter.medianFilter((int) kernelSize));

                    this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                })
        );
    }

    public void setUpGammaCorrection() {
        var model = new SpinnerNumberModel(1.0, 0.01, 3.0, 0.01);

        oneParametricDialog("Gamma correction", "Gamma", model).ifPresent(
                gamma -> SwingUtilities.invokeLater(()->{
                    this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                    filterPanel.setCurrentFilter(AreaFilter.gammaCorrection((double) gamma));
                    this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                })
        );
    }

    public void setUpSobelOperator() {
        var model = new SpinnerNumberModel(128, 1, 255, 1);

        oneParametricDialog("Sobel operator", "Threshold", model).ifPresent(
                threshold -> SwingUtilities.invokeLater(()->{
                    this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                    filterPanel.setCurrentFilter(AreaFilter.sobelOperator((int) threshold));
                    this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                })
        );
    }

    public void setUpRobertsOperator() {
        var model = new SpinnerNumberModel(128, 1, 255, 1);

        oneParametricDialog("Roberts operator", "Threshold", model).ifPresent(
                threshold -> SwingUtilities.invokeLater(()->{
                    this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                    filterPanel.setCurrentFilter(AreaFilter.robertsOperator((int) threshold));
                    this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                })
        );
    }

    public void setUpDithering() {
        var model = new SpinnerNumberModel(4, 1, 8, 1);
        oneParametricDialog("Unordered dithering", "Bitrate", model).ifPresent(
                bitrate -> SwingUtilities.invokeLater(()->{
                    this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                    filterPanel.setCurrentFilter(new DitheringFilter((int) bitrate, 2));
                    this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                })
        );
    }

    private Runnable setUpFilter(AreaFilter areaFilter) {
        return () -> SwingUtilities.invokeLater(() -> filterPanel.setCurrentFilter(areaFilter));
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
