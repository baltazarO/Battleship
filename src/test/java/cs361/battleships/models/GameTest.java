package cs361.battleships.models;

import org.junit.Test;
import static org.junit.Assert.*;

public class GameTest {

    @Test
    public void testPlace(){
        Game game = new Game();
        assertTrue(game.placeShip(new Ship("BATTLESHIP"), 1, 'B', true));
        assertTrue(game.placeShip(new Ship("MINESWEEPER"), 5, 'D', false));
        assertTrue(game.placeShip(new Ship("DESTROYER"), 9, 'A', false));
    }
}
