import Direction;

@Serializable
enum class SnakeState {
    ALIVE = 0;  // Змея управляется игроком
    ZOMBIE = 1; // Змея принадлежала игроку, который вышел из игры, она продолжает движение куда глаза глядят
}

class Snake (
    state: SnakeState = SnakeState.ALIVE
    playerId: Int = 1
    points: List<Coord>
    headDirection: Direction  = Direction.RIGHT;
)