package ru.nsu.fit.dskvl.gfx.views;


import ru.nsu.fit.dskvl.gfx.models.Point2D;
import ru.nsu.fit.dskvl.gfx.models.RotationBody;
import ru.nsu.fit.dskvl.gfx.models.Spline;

import java.awt.event.KeyEvent;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Scanner;
import javax.swing.*;

import static javax.swing.JFileChooser.APPROVE_OPTION;

public class InitMainWindow extends MainFrame {
    private static final int minimalWidth = 640;
    private static final int minimalHeight = 480;

    private final BodyViewer bodyViewer;
    private final FrameEditor frameEditor;
    public InitMainWindow() {
        super(minimalWidth, minimalHeight, "Init application");

        try {
            addSubMenu("File", KeyEvent.VK_F);
            addSubMenu("Settings", KeyEvent.VK_I);
            addSubMenu("Help", KeyEvent.VK_H);
            addMenuItem("File/Open", "Open a file", KeyEvent.VK_O, this::openFile);
            addMenuItem("File/Save", "Save a file", KeyEvent.CTRL_DOWN_MASK | KeyEvent.VK_S, this::saveFile);
            addMenuItem("File/Exit", "Exit application", KeyEvent.VK_X, "Exit.gif", this::onExit);
            addMenuItem("File/Init", "Init position", KeyEvent.VK_X, null, () -> {});

            addMenuItem("Help/About...", "Shows program version and copyright information", KeyEvent.VK_A, "About.gif", this::onAbout);

            setLocationRelativeTo(null);

            var spline = new Spline();
            var rotationBody = new RotationBody(spline);
            bodyViewer = new BodyViewer(rotationBody);
            add(bodyViewer);
            frameEditor = new FrameEditor(minimalWidth, 4*minimalHeight/3, bodyViewer);
            pack();
            bodyViewer.setVisible(true);
            setVisible(true);

            addToolBarSeparator();
            addToolBarButton("Help/About...");
            addToolBarButton("File/Exit");
            pack();
        }
        catch(Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void openFile() {
        var chooser = new JFileChooser();
        var ret = chooser.showOpenDialog(this);
        if (ret != APPROVE_OPTION)
            return;
        var file = chooser.getSelectedFile();
        SwingUtilities.invokeLater(() ->{
            try (Scanner sc = new Scanner(file)) {
                var spline = bodyViewer.getBody().getFrame().getControlPoints();
                frameEditor.clear();
                while(sc.hasNextDouble()) {
                    var x = sc.nextDouble();
                    var y = sc.nextDouble();
                    frameEditor.addPoint(new Point2D(x, y));
                }
                frameEditor.setNSpinner(spline.size());
                repaint();
            } catch (IOException e) {
                System.out.println("No such file.");
            }
        });
    }

    public void saveFile() {
        var chooser = new JFileChooser();
        var ret = chooser.showSaveDialog(this);
        if (ret != APPROVE_OPTION)
            return;
        var file = chooser.getSelectedFile();

        SwingUtilities.invokeLater(() ->{
            try (Writer wr = new FileWriter(file)) {
                var spline = bodyViewer.getBody().getFrame().getControlPoints();

                for (var point : spline) {
                    wr.write(((Double) point.x).toString());
                    wr.write(' ');
                    wr.write(((Double) point.y).toString());
                    wr.write(' ');
                }
            } catch (IOException e) {
                System.out.println("No such file.");
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
