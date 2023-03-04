import Snake;

class GameState {
    shift: Coord = Coord();

    required int32 state_order = 1;   // Порядковый номер состояния, уникален в пределах игры, монотонно возрастает
    repeated Snake snakes = 2;        // Список змей
    repeated Coord foods = 3;         // Список клеток с едой
    required GamePlayers players = 4; // Актуальнейший список игроков
}
