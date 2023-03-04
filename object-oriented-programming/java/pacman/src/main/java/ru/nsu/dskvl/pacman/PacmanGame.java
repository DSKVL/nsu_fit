package ru.nsu.dskvl.pacman;

import ru.nsu.dskvl.pacman.engine.*;
import ru.nsu.dskvl.pacman.entities.GhostEntity;
import ru.nsu.dskvl.pacman.entities.PacmanEntity;
import ru.nsu.dskvl.pacman.entities.PacmanLevel;
import ru.nsu.dskvl.pacman.visuals.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public class PacmanGame implements ActionListener {
    protected PacmanEngine engine;
    protected GameVisuals visuals = new GameVisuals();
    protected int timerDelay = 1000/60;
    protected Timer clock;

    public void start() {
        clock = new Timer(timerDelay, this);
        engine.start();
        clock.start();
    }

    public PacmanGame() throws BadGameInitializationException {
        //Level part
        PacmanLevel currentLevel = new PacmanLevel(1, visuals);

        //TODO load previous highscore
        //Score part
        Image[] highScoreLabel = PacmanSpriteManager.loadSpriteArray("HighscoreLabel", 8);
        Image[] numbers = PacmanSpriteManager.loadSpriteArray("Numbers", 10);
        ScoreManagerVisuals scoreManagerVisuals = new ScoreManagerVisuals(ScoreManager.getInstance(), highScoreLabel, numbers);

        //Entities part
        PacmanEntity pacman = new PacmanEntity();
        pacman.setSpeed(LevelInfo.getGhostNormalSpeed());
        Drawable pacmanDisplay = new DynamicEntityVisuals(pacman,
                PacmanSpriteManager.loadAnimation("Pacman", pacman, 3));

        GhostEntity blinky = new GhostEntity( "Blinky", currentLevel, pacman, null);
        GhostEntity inky = new GhostEntity("Inky", currentLevel, pacman, blinky);
        GhostEntity pinky = new GhostEntity( "Pinky", currentLevel, pacman, null);
        GhostEntity clyde = new GhostEntity( "Clyde", currentLevel, pacman, null);

        Drawable blinkyDisplay = new GhostEntityVisuals(blinky,
                PacmanSpriteManager.loadAnimation("Blinky", blinky, 2),
                PacmanSpriteManager.loadAnimation("ScaredGhost", blinky, 2),
                PacmanSpriteManager.loadAnimation("EatenGhost", blinky, 2));
        Drawable inkyDisplay = new GhostEntityVisuals(inky,
                PacmanSpriteManager.loadAnimation("Inky", inky, 2),
                PacmanSpriteManager.loadAnimation("ScaredGhost", inky, 2),
                PacmanSpriteManager.loadAnimation("EatenGhost", inky, 2));
        Drawable pinkyDisplay = new GhostEntityVisuals(pinky,
                PacmanSpriteManager.loadAnimation("Pinky", pinky, 2),
                PacmanSpriteManager.loadAnimation("ScaredGhost", pinky, 2),
                PacmanSpriteManager.loadAnimation("EatenGhost", pinky, 2));
        Drawable clydeDisplay = new GhostEntityVisuals(clyde,
                PacmanSpriteManager.loadAnimation("Clyde", clyde, 2),
                PacmanSpriteManager.loadAnimation("ScaredGhost", clyde, 2),
                PacmanSpriteManager.loadAnimation("EatenGhost", clyde, 2));

        //Modes part
        ModesManager.getInstance().setPacman(pacman);
        ModesManager.getInstance().setBlinky(blinky);
        ModesManager.getInstance().setInky(inky);
        ModesManager.getInstance().setPinky(pinky);
        ModesManager.getInstance().setClyde(clyde);

        LivesManager.getInstance().setGame(this);
        LivesManagerVisuals livesManagerVisuals = new LivesManagerVisuals(
                PacmanSpriteManager.loadSprite("lifeIcon.png"));

        //Visuals part
        visuals.getDisplayLayers().addAll(currentLevel.getDisplayLayer());
        visuals.getDisplayLayers().add(blinkyDisplay);
        visuals.getDisplayLayers().add(inkyDisplay);
        visuals.getDisplayLayers().add(pinkyDisplay);
        visuals.getDisplayLayers().add(clydeDisplay);
        visuals.getDisplayLayers().add(pacmanDisplay);
        visuals.getDisplayLayers().add(scoreManagerVisuals);
        visuals.getDisplayLayers().add(livesManagerVisuals);

        //Engine part
        engine = new PacmanEngine();
        engine.setLevel(currentLevel);
        engine.setPacmanEntity(pacman);
        engine.setGhosts(new GhostEntity[]{blinky, inky, pinky, clyde});
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        CurrentKeyInput input = visuals.getCurrentKeyInput();
        engine.update(input);
        visuals.draw();
    }

    public PacmanEngine getEngine() {return engine;}
}
