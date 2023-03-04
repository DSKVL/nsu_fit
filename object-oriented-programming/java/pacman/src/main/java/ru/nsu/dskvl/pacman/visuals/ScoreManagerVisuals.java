package ru.nsu.dskvl.pacman.visuals;

import ru.nsu.dskvl.pacman.engine.ScoreManager;

import java.awt.*;
import java.util.Collections;
import java.util.stream.Collectors;

public class ScoreManagerVisuals extends Drawable {
    private final Image[] highScoreLabel;
    private final Image[] numbers;
    private final ScoreManager scoreManager;

    public ScoreManagerVisuals(ScoreManager scoreManager, Image[] highScoreLabel, Image[] numbers) {
        this.scoreManager = scoreManager;
        this.highScoreLabel = highScoreLabel;
        this.numbers = numbers;
    }

    public void draw(Graphics2D graphics2D) {
        graphics2D.drawImage(highScoreLabel[0], 72 * GameVisuals.scale, 0, null);
        graphics2D.drawImage(highScoreLabel[1], 80 * GameVisuals.scale, 0, null);
        graphics2D.drawImage(highScoreLabel[2], 88 * GameVisuals.scale, 0, null);
        graphics2D.drawImage(highScoreLabel[0], 96 * GameVisuals.scale, 0, null);
        graphics2D.drawImage(highScoreLabel[3], 112 * GameVisuals.scale, 0, null);
        graphics2D.drawImage(highScoreLabel[4], 120 * GameVisuals.scale, 0, null);
        graphics2D.drawImage(highScoreLabel[5], 128 * GameVisuals.scale, 0, null);
        graphics2D.drawImage(highScoreLabel[6], 136 * GameVisuals.scale, 0, null);
        graphics2D.drawImage(highScoreLabel[7], 144 * GameVisuals.scale, 0, null);

        var hs = scoreManager.getHighScore().toString()
                .chars().mapToObj(c -> (char) c).collect(Collectors.toList());
        Collections.reverse(hs);
        int pos = 128 * GameVisuals.scale;
        for (var digit : hs) {
            graphics2D.drawImage(numbers[Integer.parseInt(digit.toString())], pos, 8 * GameVisuals.scale, null);
            pos -= 8 * GameVisuals.scale;
        }

        var cs = scoreManager.getCurrentScore().toString()
                .chars().mapToObj(c -> (char) c).collect(Collectors.toList());
        Collections.reverse(cs);
        pos = 40 * GameVisuals.scale;
        for (var digit : cs) {
            graphics2D.drawImage(numbers[Integer.parseInt(digit.toString())], pos, 8 * GameVisuals.scale, null);
            pos -= 8 * GameVisuals.scale;
        }
    }
}
