package cs361.battleships.models;

import org.junit.Test;

import java.util.*;

import static org.junit.Assert.*;

public class BoardTest {

    @Test
    public void testInvalidPlacement() {
        Board board = new Board();


        assertFalse(board.placeShip(new Ship("MINESWEEPER"), 11, 'C', true));
    }

    @Test
    public void testValidPlacement() {
        Board board = new Board();

        assertTrue(board.placeShip(new Ship("MINESWEEPER"), 5, 'C', false));
        assertTrue(board.placeShip(new Ship("DESTROYER"), 1, 'E', true));

        assertFalse(board.placeShip(new Ship("MINESWEEPER"), 5, 'B', false));

        assertFalse(board.placeShip(new Ship("DESTROYER"), 2, 'E', true));

    }

    @Test
    public void testAttack() {
        Board board = new Board();
        board.placeShip(new Ship("DESTROYER"), 1, 'E', true);
        Result shouldHit = board.attack(1, 'E');
        Result notHit = board.attack(1, 'A');
        board.placeShip(new Ship("MINESWEEPER"), 1, 'A', true);
        Result hitLeft = board.attack(1,'A');
        board.placeShip(new Ship("BATTLESHIP"), 7, 'J', true);
        Result hitRight = board.attack(7,'J');
        Result hitBottom = board.attack(10,'J');
        assertEquals(new Result(AtackStatus.HIT).getResult(), shouldHit.getResult());
        assertEquals(new Result(AtackStatus.HIT).getResult(), hitLeft.getResult());
        assertEquals(new Result(AtackStatus.HIT).getResult(), hitRight.getResult());
        assertEquals(new Result(AtackStatus.HIT).getResult(), hitBottom.getResult());
        assertNotEquals(new Result(AtackStatus.HIT).getResult(), notHit.getResult());

        assertEquals(new Result(AtackStatus.INVALID).getResult(), board.attack(11, 'K').getResult());
    }
}
