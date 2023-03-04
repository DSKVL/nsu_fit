package ru.nsu.dskvl.pacman.engine;

import ru.nsu.dskvl.pacman.PacmanGame;
import ru.nsu.dskvl.pacman.entities.PacmanEntity;

public class LivesManager {
    private static LivesManager uniqueInstance;
    private PacmanGame game;

    int lives = 5;

    private LivesManager() {}

    public static LivesManager getInstance() {
        if (uniqueInstance == null) {
            uniqueInstance = new LivesManager();
        }
        return uniqueInstance;
    }

    public void setGame(PacmanGame game) {
        this.game = game;
    }


    public void decrementLives() {
        lives--;
        if (lives == 0) {
            game.getEngine().gameover();
        }
    }

    public void setLives(int lives) {
        this.lives = lives;
    }

    public int getLives() {
        return lives;
    }
}
