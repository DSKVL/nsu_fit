package ru.nsu.dskvl.pacman.engine;

import ru.nsu.dskvl.pacman.entities.GhostEntity;
import ru.nsu.dskvl.pacman.entities.GhostMode;
import ru.nsu.dskvl.pacman.entities.PacmanEntity;
import ru.nsu.dskvl.pacman.entities.PacmanMode;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ModesManager implements ActionListener {
    private static ModesManager uniqueInstance;
    private Timer clock = new Timer(LevelInfo.getFrightTime(), this);

    private GhostEntity blinky;
    private GhostEntity inky;
    private GhostEntity pinky;
    private GhostEntity clyde;
    private PacmanEntity pacman;
    private int eatenReward = 0;

    private ModesManager() {
        clock.setRepeats(false);
    }

    public static ModesManager getInstance() {
        if (uniqueInstance == null) {
            uniqueInstance = new ModesManager();
        }
        return uniqueInstance;
    }

    public int getEatenReward() {
        return eatenReward;
    }

    public void setEatenReward(int eatenReward) {
        this.eatenReward = eatenReward;
    }

    public void bigDotEaten() {
        pacman.setPlayerMode(PacmanMode.BUFFED);
        pacman.setSpeed(LevelInfo.getGhostFrightSpeed());

        blinky.setMode(GhostMode.FRIGHTEND);
        blinky.setClockPaused(true);
        inky.setMode(GhostMode.FRIGHTEND);
        inky.setClockPaused(true);
        pinky.setMode(GhostMode.FRIGHTEND);
        pinky.setClockPaused(true);
        clyde.setMode(GhostMode.FRIGHTEND);
        clyde.setClockPaused(true);

        eatenReward += 200;
        clock.start();
    }

    private void setToDefaults() {
        pacman.setPlayerMode(PacmanMode.NORMAL);
        pacman.setSpeed(LevelInfo.getPacmanBuffedSpeed());

        if (blinky.getMode() == GhostMode.FRIGHTEND)
            blinky.setMode(GhostMode.CHASE);

        if (inky.getMode() == GhostMode.FRIGHTEND)
            inky.setMode(GhostMode.CHASE);

        if (pinky.getMode() == GhostMode.FRIGHTEND)
            pinky.setMode(GhostMode.CHASE);

        if (clyde.getMode() == GhostMode.FRIGHTEND)
            clyde.setMode(GhostMode.CHASE);

        eatenReward = 0;
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        setToDefaults();
    }

    public void setBlinky(GhostEntity blinky) {
        this.blinky = blinky;
    }
    public void setInky(GhostEntity inky) {
        this.inky = inky;
    }
    public void setPinky(GhostEntity pinky) {
        this.pinky = pinky;
    }
    public void setClyde(GhostEntity clyde) {
        this.clyde = clyde;
    }
    public void setPacman(PacmanEntity pacman) {
        this.pacman = pacman;
    }
}
