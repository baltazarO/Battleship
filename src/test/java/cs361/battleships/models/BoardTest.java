package cs361.battleships.models;

import org.junit.Test;

import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class BoardTest {

    @Test
    public void testInvalidPlacement() {
        Board board = new Board();
        assertFalse(board.placeShip(new Ship("MINESWEEPER", true, new Square(1, 'A')), 11, 'C', true));
    }

    @Test
    public void howIsMinesweeper() {
        Ship hms = new Ship("MINESWEEPER", true, new Square(6, 'A'));
        assertEquals(2, hms.getOccupiedSquares().size());

        List<Square> spot = hms.getOccupiedSquares();
        int topBoxRow = spot.get(0).getRow();
        assertEquals(6, topBoxRow);

        int botBoxRow = spot.get(1).getRow();
        assertEquals(7, botBoxRow);
    }

}
