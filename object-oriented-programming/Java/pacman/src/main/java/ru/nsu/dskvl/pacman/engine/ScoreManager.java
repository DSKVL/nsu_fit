package ru.nsu.dskvl.pacman.engine;

public class ScoreManager {
    private static ScoreManager uniqueInstance = null;
    private int currentScore = 0;
    private int highScore = 0;

    private ScoreManager() {}

    public static ScoreManager getInstance() {
        if (uniqueInstance == null) {
            uniqueInstance = new ScoreManager();
        }
        return uniqueInstance;
    }

    public Integer getCurrentScore() {
        return currentScore;
    }
    public void setCurrentScore(int currentScore) {
        this.currentScore = currentScore;
    }
    public Integer getHighScore() {
        return highScore;
    }
    public void setHighScore(int highScore) {
        this.highScore = highScore;
    }

    public void addScore(int points) {
        currentScore += points;
        if (currentScore > highScore) {
            highScore = currentScore;
        }
    }
}
