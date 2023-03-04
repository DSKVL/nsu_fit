import ru.nsu.dskvl.pacman.BadGameInitializationException;
import ru.nsu.dskvl.pacman.PacmanGame;

public class Main {
    public static void main(String[] args) {
        PacmanGame game;
        try {
            game = new PacmanGame();
        } catch (BadGameInitializationException e) {
            return;
        }

        game.start();
    }
}
