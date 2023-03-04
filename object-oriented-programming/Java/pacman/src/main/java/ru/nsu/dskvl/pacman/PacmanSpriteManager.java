package ru.nsu.dskvl.pacman;

import ru.nsu.dskvl.pacman.entities.Entity;
import ru.nsu.dskvl.pacman.visuals.Animation;
import ru.nsu.dskvl.pacman.visuals.GameVisuals;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import static java.lang.ClassLoader.getSystemResourceAsStream;

public class PacmanSpriteManager {
    private static final Map<String, Image> wallSpritesMap = new HashMap<>();
    private static final Properties wallConfig = new Properties();
    private static final Properties wallBoundariesConfig = new Properties();

    static  {
        try (InputStream cfgFile = getSystemResourceAsStream("spriteconfig.properties")) {
            if (cfgFile != null) {
                wallConfig.load(cfgFile);
            }
        } catch (IOException e) {
            System.err.println("Cannot load configuration file");
        }

        try (InputStream cfgFile = getSystemResourceAsStream("levels/boundaries.properties")) {
            if (cfgFile != null) {
                wallBoundariesConfig.load(cfgFile);
            }
        } catch (IOException e) {
            System.err.println("Cannot load configuration file");
        }
    }

    public static Image loadWallSprite(Character ch) throws BadGameInitializationException {
        String st = ch.toString();
        if (wallSpritesMap.containsKey(st)) {
            return wallSpritesMap.get(st);
        } else {
            if (wallConfig.containsKey(st)) {
                Image sprite = loadSprite(wallConfig.get(st).toString());
                wallSpritesMap.put(st, sprite);
                return sprite;
            }
        }
        throw new BadGameInitializationException("Bad level file");
    }

    public static Animation loadAnimation(String animationName, Entity state, int totalFrames) {
        Image[] sprites = new Image[4 * totalFrames];
        StringBuilder path = new StringBuilder("Animations/");
        path.append(animationName).append("/").append(0).append(".png");
        int pos = 12 + animationName.length();

        for (int i = 0; i < 4 * totalFrames; i++) {
            sprites[i] = loadSprite(path.toString());
            path.replace(pos, pos + Integer.toString(i).length(), Integer.toString(i + 1));
        }

        return new Animation(sprites, state, totalFrames);
    }

    public static Image[] loadSpriteArray(String path, int length) {
        Image[] spriteArray = new Image[length];
        StringBuilder fileName = new StringBuilder(path);
        fileName.append("/0.png");
        int pos = path.length() + 1;

        for (int i = 0; i < length; i++) {
            spriteArray[i] = loadSprite(fileName.toString());
            fileName.replace(pos, pos + Integer.toString(i).length(), Integer.toString(i+1));
        }
        return spriteArray;
    }

    public static Image loadSprite(String fileName) {
        Image sprite = null;
        try {
            sprite = ImageIO.read(PacmanSpriteManager.class.getClassLoader().getResourceAsStream(fileName));
            sprite = sprite.getScaledInstance(GameVisuals.scale *sprite.getWidth(null),
                    GameVisuals.scale*sprite.getWidth(null), Image.SCALE_DEFAULT);
        } catch (Exception e) {e.printStackTrace();}
        return sprite;
    }

    public static Rectangle loadHitbox(Character type) throws BadGameInitializationException {
        String st = type.toString();
        if (wallBoundariesConfig.containsKey(st)) {
            String[] strings = wallBoundariesConfig.get(st).toString().split(" ");
            return new Rectangle(Integer.parseInt(strings[0]),
                    Integer.parseInt(strings[1]),
                    Integer.parseInt(strings[2]),
                    Integer.parseInt(strings[3]));
        }
        throw new BadGameInitializationException("Bad boundaries file");
    }
}
