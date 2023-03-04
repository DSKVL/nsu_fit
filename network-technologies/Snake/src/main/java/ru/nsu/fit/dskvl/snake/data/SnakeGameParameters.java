package ru.nsu.fit.dskvl.snake.data;

public record SnakeGameParameters(
        String name,
        int width,
        int height,
        SnakeRole role,
        int delay,
        int foodStatic,
        int announcePeriod
) {
    public SnakeGameParameters newRole(SnakeRole newRole) {
        return new SnakeGameParameters(name, width, height, newRole, delay, foodStatic, announcePeriod);
    }
}
