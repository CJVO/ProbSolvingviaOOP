import java.io.IOException;
import java.util.Scanner;

/**
 * @author Chukwuka, CS 10, Winter 2022, for the purpose of completing problem set 4
 * @author Anish, CS 10, Winter 2022, for the purpose of completing problem set 4
 */

public class BaconGameTestCases {
    public static void main(String[] args) throws IOException {
        // testing game with test files
        Scanner input = new Scanner(System.in);
        BaconGame newGame = new BaconGame(
                "Kevin Bacon",
                input,
                "actorsTest.txt",
                "moviesTest.txt",
                "movie-actorsTest.txt");
        newGame.playBaconGame();
    }
}
