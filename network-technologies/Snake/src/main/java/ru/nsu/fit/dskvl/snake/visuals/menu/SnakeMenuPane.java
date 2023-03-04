package ru.nsu.fit.dskvl.snake.visuals.menu;

import ru.nsu.fit.dskvl.snake.data.SnakeGameAnnouncement;

import javax.swing.*;
import java.awt.*;
import java.util.function.UnaryOperator;

public class SnakeMenuPane extends JPanel {

    SnakeLocalMenuPane localMenu;

    public SnakeMenuPane() {
        super(new GridLayout(1, 2, 10, 10));

        localMenu = new SnakeLocalMenuPane();
        this.add(localMenu);
    }

    public JButton getCreateElement() {
        return localMenu.getCreateButton();
    }

    public void addGameAnnouncement(SnakeGameAnnouncement gameAnnouncement) {
        localMenu.addGameAnnouncement(gameAnnouncement);
    }

    public void removeGameAnnouncement(SnakeGameAnnouncement gameAnnouncement) {
        localMenu.removeGameAnnouncement(gameAnnouncement);
    }
    public void onPressConnect(UnaryOperator<SnakeGameAnnouncement> function) {
        localMenu.onPressConnect(function);
    }

    public void showCreateErrorMsg(String s) {
        localMenu.showCreateErrorMsg(s);
    }
}
