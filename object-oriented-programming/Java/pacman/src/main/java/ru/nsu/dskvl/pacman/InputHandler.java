package ru.nsu.dskvl.pacman;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class InputHandler implements KeyListener {
    private CurrentKeyInput currentKeyInput = CurrentKeyInput.NONE;

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode();
        currentKeyInput = switch (code) {
            case KeyEvent.VK_UP -> CurrentKeyInput.UP;
            case KeyEvent.VK_DOWN ->CurrentKeyInput.DOWN;
            case KeyEvent.VK_LEFT -> CurrentKeyInput.LEFT;
            case KeyEvent.VK_RIGHT -> CurrentKeyInput.RIGHT;
            case KeyEvent.VK_ENTER -> CurrentKeyInput.ENTER;
            default -> CurrentKeyInput.NONE;
        };
    }

    @Override
    public void keyReleased(KeyEvent e) {
        currentKeyInput = CurrentKeyInput.NONE;
    }

    public CurrentKeyInput getCurrentKeyInput() {
        return currentKeyInput;
    }
}
