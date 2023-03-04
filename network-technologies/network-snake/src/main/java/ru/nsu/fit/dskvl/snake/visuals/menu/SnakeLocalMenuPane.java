package ru.nsu.fit.dskvl.snake.visuals.menu;

import ru.nsu.fit.dskvl.snake.data.SnakeGameAnnouncement;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.function.UnaryOperator;

public class SnakeLocalMenuPane extends JPanel {

    HashMap<String, SnakeGameAnnouncement> gameList;
    JList<String> gameListView;
    DefaultListModel<String> gameListNames;
    JButton connectButton;
    JButton createButton;

    SnakeLocalMenuPane() {
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        JLabel paneName = new JLabel("Снаке");

        gameList = new HashMap<>();
        gameListNames = new DefaultListModel<>();
        gameListView = new JList<>(gameListNames);
        gameListView.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        createButton = new JButton("Start");
        createButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        createButton.setBorder(BorderFactory.createEmptyBorder(10, 100, 10, 85));

        connectButton = new JButton("Connect");
        connectButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        connectButton.setBorder(BorderFactory.createEmptyBorder(10, 100, 10, 65));

        paneName.setAlignmentX(Component.LEFT_ALIGNMENT);
        paneName.setFont(new Font(paneName.getFont().getName(), paneName.getFont().getStyle(), 20));
        paneName.setBorder(BorderFactory.createEmptyBorder(100, 100, 10, 10));

        gameListView.setAlignmentX(Component.LEFT_ALIGNMENT);
        gameListView.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        this.add(paneName, BorderLayout.PAGE_START);
        this.add(gameListView, BorderLayout.CENTER);
        this.add(connectButton, BorderLayout.PAGE_END);
        this.add(createButton, BorderLayout.PAGE_END);
    }

    public JButton getCreateButton() {
        return createButton;
    }

    public void addGameAnnouncement(SnakeGameAnnouncement gameAnnouncement) {
        if (!gameList.containsKey(gameAnnouncement.name)) {
            gameList.put(gameAnnouncement.name, gameAnnouncement);
            gameListNames.addElement(gameAnnouncement.name);
            gameListView.setVisible(true);
        }
    }

    public void removeGameAnnouncement(SnakeGameAnnouncement gameAnnouncement) {
        gameList.remove(gameAnnouncement.name);
        gameListNames.removeElement(gameAnnouncement.name);
        gameList.remove(gameAnnouncement.name);
    }

    public void onPressConnect(UnaryOperator<SnakeGameAnnouncement> function) {
        connectButton.addActionListener(e -> {
            if (gameListView.getSelectedIndex() >= 0) {
                SnakeGameAnnouncement gameAnnouncement = gameList.get(gameListNames.get(gameListView.getMinSelectionIndex()));
                function.apply(gameAnnouncement);
            }
        });
    }

    public void showCreateErrorMsg(String s) {
        System.out.println(s);
    }

}
