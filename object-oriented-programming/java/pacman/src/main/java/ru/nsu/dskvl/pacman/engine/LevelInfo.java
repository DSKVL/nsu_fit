package ru.nsu.dskvl.pacman.engine;

public class LevelInfo {
    private static int currentLevel = 0;

    private static double[] GhostNormalSpeed = new double[]{0.75, 0.85, 0.85, 0.85, 0.95};
    private static double[] GhostFrightSpeed = new double[]{0.5, 0.55, 0.55, 0.55, 0.60};
    private static double[] GhostTunnelSpeed = new double[]{0.40, 0.45, 0.45, 0.45, 0.50};
    private static double[] PacmanNormalSpeed = new double[]{0.8, 0.9, 0.9, 0.9, 1.0};
    private static double[] PacmanBuffedSpeed = new double[]{0.9, 0.95, 0.95, 0.95, 1.0};
    private static int[] FrightTime = new int[]{};

    public static double getGhostNormalSpeed() {
        return GhostFrightSpeed[currentLevel];
    }

    public static void setCurrentLevel(int currentLevel) {currentLevel = currentLevel;}

    public static double getGhostFrightSpeed() {
        return GhostFrightSpeed[currentLevel];
    }

    public static double getGhostTunnelSpeed() {
        return GhostTunnelSpeed[currentLevel];
    }

    public static double getPacmanNormalSpeed() {
        return PacmanNormalSpeed[currentLevel];
    }

    public static double getPacmanBuffedSpeed() {
        return PacmanBuffedSpeed[currentLevel];
    }

    public static int getFrightTime() {
        return 10000;
    }

    public static void increaseLevel() { currentLevel++; }
}
