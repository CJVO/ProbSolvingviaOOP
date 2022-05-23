import java.io.IOException;
import java.util.Scanner;

/**
 * @author Chukwuka, CS 10, Winter 2022, for the purpose of completing problem set 4
 * @author Anish, CS 10, Winter 2022, for the purpose of completing problem set 4
 */

public class BaconGameDriver {
    public static void main(String[] args) throws IOException {
        // testing game with actual files
        Scanner input = new Scanner(System.in);
        BaconGame newGame = new BaconGame(
                "Kevin Bacon",
                input,
                "bacon/actors.txt",
                "bacon/movies.txt",
                "bacon/movie-actors.txt");
        newGame.playBaconGame();
    }
}
