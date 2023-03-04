package ru.nsu.fit.dskvl.snake.data;

import ru.nsu.fit.dskvl.snake.SnakeEntity;

import java.util.List;
import java.util.Map;

public class SnakeGameState {
    public SnakeGameState(Map<Integer, SnakeEntity> snakes, List<Point> foods, int stateNumber) {
        this.snakes = snakes;
        this.foods = foods;
        this.stateOrder = stateNumber;
    }

    public Map<Integer, SnakeEntity> getSnakes() {
        return snakes;
    }
    public List<Point> getFoods() {
        return foods;
    }
    public int getOrder() {
        return stateOrder;
    }

    Map<Integer, SnakeEntity> snakes;
    List<Point> foods;
    int stateOrder;
}
