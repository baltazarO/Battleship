package cs361.battleships.models;

import org.junit.Test;

import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class BoardTest {

    @Test
    public void testInvalidPlacement() {
        Board board = new Board();


        assertFalse(board.placeShip(new Ship("MINESWEEPER"), 11, 'C', true));
        board.placeShip(new Ship("MINESWEEPER"), 5, 'C', false);
        assertTrue(board.placeShip(new Ship("DESTROYER"), 1, 'E', true));
    }

}
