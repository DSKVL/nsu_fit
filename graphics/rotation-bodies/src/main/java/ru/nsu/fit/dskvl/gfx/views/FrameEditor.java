package ru.nsu.fit.dskvl.gfx.views;

import ru.nsu.fit.dskvl.gfx.models.Point2D;
import ru.nsu.fit.dskvl.gfx.models.RotationBody;
import ru.nsu.fit.dskvl.gfx.models.Spline;

import javax.swing.*;
import java.awt.*;

public class FrameEditor extends JFrame {
    private final OutlineEditor panel;
    RotationBody rotationBody;
    Spline spline;
    CoordinatesListener coordinatesListener = new CoordinatesListener();
    JSpinner spinnerN, spinnerPoint;

    public FrameEditor(int x, int y, BodyViewer bodyViewer) {
        setSize(x, y);
        setLocationByPlatform(true);
        setTitle("Shape editor");
        setMinimumSize(new Dimension(x, y));
        setLayout(new GridBagLayout());
        rotationBody = bodyViewer.getBody();
        spline = rotationBody.getFrame();
        panel = new OutlineEditor(spline, coordinatesListener);
        Insets insets = new Insets(10, 10, 10, 10);
        add(panel, new GridBagConstraints(0, 0,
                GridBagConstraints.REMAINDER, 1, 1.0, 6.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                insets, 7, 7));

        for (int i = 0; i < 8; i++)
            addPoint(new Point2D(i * ((double) 1 / 8) - 0.5 + 0.5/(8), 0.2));

        spinnerN = setupParameterSpinner("N", 0, 1,
                new SpinnerNumberModel(spline.getControlPoints().size(), 5, 64, 1));
        panel.setActivePoint(0);
        var currentPoint = panel.getActivePoint();
        var spinnerX = setupParameterSpinner("X", 1, 1,
                new SpinnerNumberModel(currentPoint.getModelCoordinates().x, -6.0, 6.0, 0.05));

        var spinnerY = setupParameterSpinner("Y", 1, 2,
                new SpinnerNumberModel(currentPoint.getModelCoordinates().y, -2.0, 2.0, 0.05));
        spinnerPoint = setupParameterSpinner("Point", 0, 2,
                new SpinnerNumberModel(currentPoint.getIndex(), 0, (int) spinnerN.getModel().getValue() - 1, 1));
        var spinnerA0 = setupParameterSpinner("A0", 3, 1,
                new SpinnerNumberModel(spline.getN(), 1, 500, 1));
        var spinnerM1 = setupParameterSpinner("M1", 4, 1,
                new SpinnerNumberModel(rotationBody.getM1(), 0, 50, 1));

        var spinnerA2 = setupParameterSpinner("A2", 5, 2,
                new SpinnerNumberModel(rotationBody.getCirclesAccuracy(), 1, 100, 1));

        spinnerN.addChangeListener(e -> {
            var totalPointsOld = spline.getControlPoints().size();
            var totalPointsNew = (int) spinnerN.getValue();
            var numberModel =(SpinnerNumberModel) spinnerPoint.getModel();
            var currentActivePointIndex = (int) numberModel.getValue();

            if (totalPointsOld > totalPointsNew) {
                for (int i = totalPointsOld; i > totalPointsNew; i--) {
                    spline.getControlPoints().remove(i-1);
                    var point = panel.getEditorPoints().get(i-1);
                    point.setVisible(false);
                    panel.remove(point);
                    panel.getEditorPoints().remove(i-1);
                }
                if (currentActivePointIndex >= totalPointsNew) {
                    panel.setActivePoint(totalPointsNew - 1);
                    spinnerPoint.setValue(totalPointsNew - 1);
                }
            } else {
                for (int i = totalPointsOld ; i < totalPointsNew; i++) {
                    addPoint(new Point2D(0, 0));
                }
            }
            spline.recalculate();
            panel.repaint();
            bodyViewer.repaint();
            setNSpinner(totalPointsNew);
        });

        spinnerX.addChangeListener(e -> {
            panel.getActivePoint().getModelCoordinates().x = (double) spinnerX.getValue();
            spline.recalculate();
            panel.repaint();
            bodyViewer.repaint();
        });

        spinnerY.addChangeListener(e -> {
            panel.getActivePoint().getModelCoordinates().y = (double) spinnerY.getValue();
            spline.recalculate();
            panel.repaint();
            bodyViewer.repaint();
        });

        spinnerPoint.addChangeListener( e -> {
            panel.setActivePoint((int) spinnerPoint.getValue());
            var point = panel.getActivePoint().getModelCoordinates();
            spinnerX.setValue(point.x);
            spinnerY.setValue(point.y);
            panel.repaint();
        });

        coordinatesListener.setAction((point) -> {
            var model = point.getModelCoordinates();
            spinnerX.setValue(model.x);
            spinnerY.setValue(model.y);
            spinnerPoint.setValue(point.getIndex());
            bodyViewer.repaint();
            panel.repaint();
        });

        spinnerA0.addChangeListener(e -> {
            spline.setN((int) spinnerA0.getValue());
            panel.repaint();
            bodyViewer.repaint();
        });

        spinnerM1.addChangeListener(e -> {
            rotationBody.setM1((int) spinnerM1.getValue());
            bodyViewer.repaint();
        });

        spinnerA2.addChangeListener(e -> {
            rotationBody.setCirclesAccuracy((int) spinnerA2.getValue());
            bodyViewer.repaint();
        });

        panel.setVisible(true);

        pack();
        setVisible(true);
    }

    public void incrementNSpinner() {
        spinnerN.setValue((int)spinnerN.getValue() + 1);
        var model = (SpinnerNumberModel) spinnerPoint.getModel();
        model.setMaximum((Integer) model.getMaximum() + 1);

    }

    public void setNSpinner(int N) {
        rotationBody.setM2(N-2);
        spinnerN.setValue(N);
        var model = (SpinnerNumberModel) spinnerPoint.getModel();
        model.setMaximum(N-1);
    }

    public void addPoint(Point2D point) {
        spline.getControlPoints().add(point);
        panel.addPoint(point);
        spline.recalculate();
        panel.repaint();
    }

    public void clear() {
        for (var point : panel.getEditorPoints()) {
            panel.remove(point);
        }
        spline.getControlPoints().clear();
    }

    private static GridBagConstraints constraint(int x, int y) {
        return new GridBagConstraints(x, y,
                1, 1, 1.0, 1.0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                new Insets(7, 7, 7, 7), 1, 1);
    };

    private JSpinner setupParameterSpinner(String name, int xGrid, int yGrid, SpinnerNumberModel spinnerNumberModel) {
        GridBagConstraints nameLabelConstraint = constraint(2*xGrid, yGrid);
        JLabel nameLabel = new JLabel(name);
        GridBagConstraints spinnerConstraint = constraint(2*xGrid + 1, yGrid);
        JSpinner spinner = new JSpinner(spinnerNumberModel);
        add(nameLabel, nameLabelConstraint);
        add(spinner, spinnerConstraint);
        nameLabel.setVisible(true);
        spinner.setVisible(true);
        return spinner;
    }
}
