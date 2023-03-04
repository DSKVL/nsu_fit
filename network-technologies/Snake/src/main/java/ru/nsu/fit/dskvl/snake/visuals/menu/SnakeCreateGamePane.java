package ru.nsu.fit.dskvl.snake.visuals.menu;

import ru.nsu.fit.dskvl.snake.data.SnakeGameParameters;
import ru.nsu.fit.dskvl.snake.data.SnakeRole;

import javax.swing.*;
import java.awt.*;

public class SnakeCreateGamePane extends JPanel {

    JTextField gameName;
    SpinnerNumberModel widthBoxModel;
    SpinnerNumberModel heightBoxModel;
    SpinnerNumberModel foodNumBoxModel;
    SpinnerNumberModel delayBoxModel;
    JButton launchButton;
    JButton cancelButton;

    public SnakeCreateGamePane() {
        GroupLayout layout = new GroupLayout(this);
        this.setLayout(layout);

        JLabel paneName = new JLabel("Create new game:");

        gameName = new JTextField("Unnamed game");
        gameName.setCaretPosition(gameName.getText().length());

        JLabel widthLabel = new JLabel("Field width: ");
        widthBoxModel = new SpinnerNumberModel(20, 10, 100, 1);
        JSpinner widthBox = new JSpinner(widthBoxModel);

        JLabel heightLabel = new JLabel(" x Field height: ");
        heightBoxModel = new SpinnerNumberModel(20, 10, 100, 1);
        JSpinner heightBox = new JSpinner(heightBoxModel);

        JPanel dimsPane = new JPanel();
        dimsPane.setLayout(new BoxLayout(dimsPane, BoxLayout.X_AXIS));
        dimsPane.add(widthLabel);
        dimsPane.add(widthBox);
        dimsPane.add(heightLabel);
        dimsPane.add(heightBox);

        JPanel foodPane = new JPanel();
        foodPane.setLayout(new BoxLayout(foodPane, BoxLayout.X_AXIS));
        JLabel foodNumBoxLabel = new JLabel("Minimal number of foods: ");
        foodNumBoxModel = new SpinnerNumberModel(1, 0, 100, 1);
        JSpinner foodNumBox = new JSpinner(foodNumBoxModel);
        foodPane.add(foodNumBoxLabel);
        foodPane.add(foodNumBox);

        JPanel delayPane = new JPanel();
        delayPane.setLayout(new BoxLayout(delayPane, BoxLayout.X_AXIS));
        JLabel delayBoxLabel = new JLabel("Delay between steps (in milliseconds): ");
        delayBoxModel = new SpinnerNumberModel(150, 100, 3000, 100);
        JSpinner delayBox = new JSpinner(delayBoxModel);
        delayPane.add(delayBoxLabel);
        delayPane.add(delayBox);

        launchButton = new JButton("Launch");

        cancelButton = new JButton("Cancel");

        paneName.setFont(new Font(paneName.getFont().getName(), paneName.getFont().getStyle(), 20));
        gameName.setMaximumSize(new Dimension(500, 20));
        dimsPane.setMaximumSize(new Dimension(500, 20));
        foodPane.setMaximumSize(new Dimension(500, 20));
        delayPane.setMaximumSize(new Dimension(500, 20));
        launchButton.setMaximumSize(new Dimension(50, 20));
        cancelButton.setMaximumSize(new Dimension(50, 20));

        // Set borders
        paneName.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        gameName.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        dimsPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        foodPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        delayPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        launchButton.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        cancelButton.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));


        layout.setHorizontalGroup(
                layout.createParallelGroup()
                        .addComponent(paneName)
                        .addComponent(gameName)
                        .addComponent(dimsPane)
                        .addComponent(foodPane)
                        .addComponent(delayPane)
                        .addComponent(launchButton)
                        .addComponent(cancelButton));
        layout.setVerticalGroup(
                layout.createSequentialGroup()
                        .addComponent(paneName)
                        .addComponent(gameName)
                        .addComponent(dimsPane)
                        .addComponent(foodPane)
                        .addComponent(delayPane)
                        .addComponent(launchButton)
                        .addComponent(cancelButton));
    }

    public JButton getLaunchElement() {
        return launchButton;
    }

    public SnakeGameParameters getGameParameters() {
        return new SnakeGameParameters(
                gameName.getText(),
                widthBoxModel.getNumber().intValue(),
                heightBoxModel.getNumber().intValue(),
                SnakeRole.NONE,
                delayBoxModel.getNumber().intValue(),
                foodNumBoxModel.getNumber().intValue(),
                1000);
    }

    public JButton getCancelElement() {
        return cancelButton;
    }

}
