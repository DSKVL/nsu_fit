package ru.nsu.fit.dskvl.snake;

import ru.nsu.fit.dskvl.snake.data.Point;
import ru.nsu.fit.dskvl.snake.data.SnakeDirection;
import ru.nsu.fit.dskvl.snake.data.SnakeGameState;

import java.util.*;

public class GameLogic {

    final int START_SIZE = 2;

    int height;
    int width;

    int highest_id = 0;
    Map<Integer, SnakeEntity> snakes = new HashMap<>();
    List<Point> foods = new ArrayList<Point>();
    int foodCount;
    int stateNum = 0;


    public GameLogic(int width, int height, int food_static) {
        this.width = width;
        this.height = height;
        foodCount = food_static;
        spawnFoods();
    }

    private void spawnFoods() {
        boolean canSpawn = true;
        while (foods.size() < foodCount && canSpawn) {
            canSpawn = spawnRandomFood();
        }
    }

    private boolean spawnRandomFood() {
        List<Point> availableSpaces = fieldCollection(height, width);

        for (var snake: snakes.values()) {
            List<Point> body = snake.getBody();
            Point currentPoint = snake.getHead();

            for (var body_part: body) {
                if (body_part != body.get(0))
                    currentPoint = currentPoint.add(body_part).wrap(height, width);
                availableSpaces.remove(currentPoint);
            }
        }

        for (var food: foods)
            availableSpaces.remove(food);

        if (!availableSpaces.isEmpty()) {
            Point spawn = availableSpaces.get(new Random().nextInt(availableSpaces.size()));
            return spawnFood(spawn);
        }

        return false;
    }

    private boolean spawnFood(Point food) {
        for (var snake: snakes.values()) {
            List<Point> body = snake.getBody();
            Point currentPoint = snake.getHead();
            for (var body_part: body) {
                if (body_part != body.get(0))
                    currentPoint = currentPoint.add(body_part).wrap(height, width);

                if (currentPoint.equals(food))
                    return false;
            }
        }

        foods.add(food);
        return true;
    }

    public int addSnake() {
        Point startPos = locateSpawn();

        if (startPos == null)
            return -1;

        var snake = new SnakeEntity(highest_id, startPos, START_SIZE);
        snakes.put(highest_id, snake);

        foodCount++;
        spawnFoods();

        return highest_id++;
    }

    List<Point> fieldCollection(int height, int width) {
        var res = new ArrayList<Point>(width*height);
        for (int x = 0; x < height; x++)
            for (int y = 0; y < width; y++)
                res.add(new Point(x, y));

        return res;
    }

    public Point locateSpawn() {
        Point startPos = null;

        for (int x = 0; x < height; x++) {
            for (int y = 0; y < width; y++) {
                startPos = new Point(x, y);

                for (int a = -2; a <= 2; a++) {
                    for (int b = -2; b <= 2; b++) {
                        assert startPos != null;
                        Point p = startPos.add(new Point(a, b)).wrap(height, width);

                        // Check if the chosen area has any snakes
                        for (var snake: snakes.values()) {
                            List<Point> body = snake.getBody();
                            Point currentPoint = snake.getHead();

                            for (var body_part: body) {
                                if (body_part != body.get(0))
                                    currentPoint = currentPoint.add(body_part).wrap(height, width);

                                if (p.equals(currentPoint)) {
                                    startPos = null;
                                    break;
                                }
                            }
                        }

                        if (startPos == null)
                            break;

                        for (var food: foods)
                            if (p == food) {
                                startPos = null;
                                break;
                            }

                    }
                    if (startPos == null)
                        break;
                }

                if (startPos != null)
                    break;
            }

            if (startPos != null)
                break;
        }
        return startPos;
    }

    public SnakeGameState nextState() {
        for (var snake: snakes.values()) {
            snake.addHead(height, width);
            int foodEaten = foods.indexOf(snake.getHead());
            if (foodEaten != -1) {
                foods.remove(foodEaten);
            } else {
                snake.removeTail();
            }
        }

        List<Integer> killList = new ArrayList<>();
        for (var snake1: snakes.values()) {
            for (var snake2: snakes.values()) {
                Point head = snake1.getHead();
                Point currentPoint = snake2.getHead();

                List<Point> body = snake2.getBody();
                for (var body_part: body) {
                    if (body_part != body.get(0))
                        currentPoint = currentPoint.add(body_part).wrap(height, width);

                    if (currentPoint.equals(head))
                        if (!(snake1.id == snake2.id && body_part == body.get(0))) {
                            killList.add(snake1.id);
                            if (snake1.id != snake2.id)
                                snake2.addScore(1);
                        }
                }
            }
        }

        for (var killId: killList) {
            killSnake(killId);
        }

        spawnFoods();

        return new SnakeGameState(snakes, foods, stateNum++);
    }

    public boolean canSpawn() {
        return (locateSpawn() != null);
    }

    private void killSnake(int id) {
        SnakeEntity snake = snakes.remove(id);
        snakes.remove(id);

        foodCount--;

        List<Point> body = snake.getBody();
        Point currentPoint = snake.getHead();
        for (var body_part: body) {
            if (body_part != body.get(0))
                currentPoint = currentPoint.add(body_part).wrap(height, width);

            if (1 == new Random().nextInt(2)) {
                spawnFood(currentPoint);
            }
        }
    }

    public void steerSnake(int id, SnakeDirection direction) {
        if (snakes.containsKey(id))
            snakes.get(id).steer(direction);
    }

    public SnakeGameState currentState() {
        return new SnakeGameState(snakes, foods, stateNum);
    }

    public int getScore(int id) {
        if (snakes.containsKey(id))
            return snakes.get(id).score;
        else
            return -1;
    }
}
