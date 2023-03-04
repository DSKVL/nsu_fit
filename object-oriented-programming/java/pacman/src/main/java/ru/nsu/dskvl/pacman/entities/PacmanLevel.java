package ru.nsu.dskvl.pacman.entities;

import ru.nsu.dskvl.pacman.BadGameInitializationException;
import ru.nsu.dskvl.pacman.visuals.Drawable;
import ru.nsu.dskvl.pacman.visuals.GameVisuals;

import java.awt.*;
import java.awt.geom.Point2D;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static java.lang.ClassLoader.getSystemResourceAsStream;

public class PacmanLevel {
    Tile[] tiles = new Tile[31 * 28];
    private int dotsLeft = 244;

    public PacmanLevel(int levelNumber, GameVisuals visuals) throws BadGameInitializationException {
        StringBuilder filePath = new StringBuilder("levels/level");
        filePath.append(levelNumber);

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(getSystemResourceAsStream(filePath.toString())))) {
            for (int Y = 0; Y < 31; Y++)
                for (int X = 0; X < 28; X++) {
                    char type = (char) reader.read();
                    tiles[Y * 28 + X] = new Tile(8 * X, 8 * Y + 24, type);
                    if (type == '.' || type == 'O') {
                        Entity dot = switch (type) {
                            case '.' -> new Dot(8 * X, 8 * Y + 24, visuals, tiles[Y * 28 + X]);
                            case 'O' -> new BigDot(8 * X, 8 * Y + 24, visuals, tiles[Y * 28 + X]);
                            default -> throw new IllegalStateException("Unexpected value: " + type);
                        };

                        tiles[Y * 28 + X].setObject(dot);
                    }
                }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int getDotsLeft() {return dotsLeft;}
    public void setDotsLeft(int dotsLeft) { this.dotsLeft = dotsLeft; }
    public void decreaseDotsleft() {
        dotsLeft--;
        System.out.println(dotsLeft);
    }

    public List<Drawable> getDisplayLayer() {
        List<Drawable> displays = Arrays
                .stream(tiles)
                .map(Tile::getDisplay)
                .collect(Collectors.toList());

        displays.addAll(Arrays
                .stream(tiles)
                .filter(tile -> tile.getObject() != null)
                .map(tile -> ((Dot) tile.getObject()).getDisplay())
                .collect(Collectors.toList()));

        return displays;
    }

    public Point2D getCellCenterCoordinatesAt(int x, int y) {
        return new Point2D.Double(tiles[y * 28 + x].getPosition().getX() + 4,
                tiles[y * 28 + x].getPosition().getY() + 4);
    }

    public Point2D getCellCenterCoordinatesAt(Point point) {
        return new Point2D.Double(tiles[point.y * 28 + point.x].getPosition().getX() + 4,
                tiles[point.y* 28 + point.x].getPosition().getY() + 4);
    }

    public Entity getEntityAt(int x, int y) {
        return tiles[y * 28 + x];
    }

    public Entity getEntityAt(Point point) {
        return tiles[point.y * 28 + point.x];
    }

    public void reset() {
        dotsLeft = 244;
        Arrays.stream(tiles).filter(x->{return x.getObject() != null;}).forEach((x) -> {((Dot)x.getObject()).setCollected(false);});
    }
}

