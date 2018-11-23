package cs361.battleships.models;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class BoardTest {

    private Board board;

    @Before
    public void setUp() {
        board = new Board();
    }

    @Test
    public void testInvalidPlacement() {
        assertFalse(board.placeShip(new Ship("MINESWEEPER"), 11, 'C', true,false));
    }

    @Test
    public void testPlaceMinesweeper() {
        assertTrue(board.placeShip(new Ship("MINESWEEPER"), 1, 'A', true,false));
    }

    @Test
    public void testPlaceOverEdge() {
        assertFalse(board.placeShip(new Ship("MINESWEEPER"), 6, 'J', false, false));
        assertFalse(board.placeShip(new Ship("MINESWEEPER"), 10, 'E', true, false));
    }

    @Test
    public void testPlaceTwice() {
        assertTrue(board.placeShip(new Ship("MINESWEEPER"),4,'A',false,false));
        assertFalse(board.placeShip(new Ship("MINESWEEPER"),2,'A',false,false));
    }

    @Test
    public void testPlaceOverlap(){
        assertTrue(board.placeShip(new Ship("MINESWEEPER"),4,'A',false,false));
        assertFalse(board.placeShip(new Ship("MINESWEEPER"),4,'B',false,false));
    }

    @Test
    public void testAttackEmptySquare() {
        board.placeShip(new Ship("MINESWEEPER"), 1, 'A', true,false);
        Result result = board.attack(2, 'E');
        assertEquals(AtackStatus.MISS, result.getResult());
    }

    @Test
    public void testAttackShip() {
        Ship minesweeper = new Ship("MINESWEEPER");
        board.placeShip(minesweeper, 1, 'A', true,false);
        minesweeper = board.getShips().get(0);
        Result result = board.attack(1, 'A');
        assertEquals(AtackStatus.HIT, result.getResult());
        assertEquals(minesweeper, result.getShip());
    }

    @Test
    public void testAttackSameSquareMultipleTimes() {
        Ship minesweeper = new Ship("MINESWEEPER");
        board.placeShip(minesweeper, 1, 'A', true,false);
        board.attack(1, 'A');
        Result result = board.attack(1, 'A');
        assertEquals(AtackStatus.INVALID, result.getResult());
    }

    @Test
    public void testAttackSameEmptySquareMultipleTimes() {
        Result initialResult = board.attack(1, 'A');
        assertEquals(AtackStatus.MISS, initialResult.getResult());
        Result result = board.attack(1, 'A');
        assertEquals(AtackStatus.INVALID, result.getResult());
    }

    @Test
    public void testSurrender() {
        board.placeShip(new Ship("MINESWEEPER"), 1, 'A', true,false);
        board.attack(1, 'A');
        var result = board.attack(2, 'A');
        assertEquals(AtackStatus.SURRENDER, result.getResult());
    }

    @Test
    public void testPlaceMultipleShipsOfSameType() {
        assertTrue(board.placeShip(new Ship("MINESWEEPER"), 1, 'A', true,false));
        assertFalse(board.placeShip(new Ship("MINESWEEPER"), 5, 'D', true,false));
    }

    @Test
    public void testCantPlaceMoreThan3Ships() {
        assertTrue(board.placeShip(new Ship("MINESWEEPER"), 1, 'A', true,false));
        assertTrue(board.placeShip(new Ship("BATTLESHIP"), 5, 'D', true,false));
        assertTrue(board.placeShip(new Ship("DESTROYER"), 6, 'A', false,false));
        assertFalse(board.placeShip(new Ship(""), 8, 'A', false,false));
    }

    @Test
    public void testCQonBoard() {
        board.placeShip(new Ship("BATTLESHIP"), 5, 'D', true,false);
        board.placeShip(new Ship("MINESWEEPER"), 1, 'A', true,false);

        assertEquals(AtackStatus.CRITICAL, board.attack(7, 'D').getResult());
        assertEquals(AtackStatus.SUNK, board.attack(7, 'D').getResult());
    }

    @Test
    public void testCQonBoardAlternateLocation() {
        board.placeShip(new Ship("BATTLESHIP"), 5, 'D', true,true);
        board.placeShip(new Ship("MINESWEEPER"), 1, 'A', true,true);

        assertEquals(AtackStatus.CRITICAL, board.attack(6, 'D').getResult());
        assertEquals(AtackStatus.SUNK, board.attack(6, 'D').getResult());
    }

    @Test
    public void testMove3ShipsWest() {
        board.placeShip(new Ship("BATTLESHIP"), 5, 'D', true,true);
        board.placeShip(new Ship("MINESWEEPER"), 2, 'B', true,true);
        board.placeShip(new Ship("DESTROYER"), 9, 'G', false,true);
        board.move(true,0);
        assertTrue(board.getShips().get(0).isAtLocation(new Square(5,'C')));
        assertTrue(board.getShips().get(0).isAtLocation(new Square(6,'C')));
        assertTrue(board.getShips().get(0).isAtLocation(new Square(7,'C')));
        assertTrue(board.getShips().get(0).isAtLocation(new Square(8,'C')));

        assertTrue(board.getShips().get(1).isAtLocation(new Square(2,'A')));
        assertTrue(board.getShips().get(1).isAtLocation(new Square(3,'A')));

        assertTrue(board.getShips().get(2).isAtLocation(new Square(9,'F')));
        assertTrue(board.getShips().get(2).isAtLocation(new Square(9,'G')));
        assertTrue(board.getShips().get(2).isAtLocation(new Square(9,'H')));
    }

    @Test
    public void testMoveOverlapNorthBoundry(){
        board.placeShip(new Ship("BATTLESHIP"), 1, 'D', false,false);
        board.placeShip(new Ship("MINESWEEPER"), 2, 'D', false,false);
        board.move(true,1);
        assertTrue(board.getShips().get(0).isAtLocation(new Square(1,'D')));
        assertTrue(board.getShips().get(0).isAtLocation(new Square(1,'E')));
        assertTrue(board.getShips().get(0).isAtLocation(new Square(1,'F')));
        assertTrue(board.getShips().get(0).isAtLocation(new Square(1,'G')));

        assertTrue(board.getShips().get(1).isAtLocation(new Square(2,'D')));
        assertTrue(board.getShips().get(1).isAtLocation(new Square(2,'E')));
    }

    @Test
    public void testMoveOntoMissesPlayersBoard(){
        board.placeShip(new Ship("BATTLESHIP"), 5, 'D', false,false);
        Result result = board.attack(4,'D');
        assertEquals(AtackStatus.MISS, result.getResult());
        assertEquals(1, board.getAttacks().size());
        board.move(true,1);
        assertEquals(1, board.getAttacks().size());
        assertEquals(AtackStatus.INVALID, board.getAttacks().get(0).getResult());
    }

    @Test
    public void testMoveOntoMissesOpponentsBoard(){
        board.placeShip(new Ship("BATTLESHIP"), 5, 'D', false,false);
        Result result = board.attack(4,'D');
        assertEquals(AtackStatus.MISS, result.getResult());
        assertEquals(1, board.getAttacks().size());
        board.move(false,1);
        assertEquals(1, board.getAttacks().size());
        assertEquals(AtackStatus.INVALID, board.getAttacks().get(0).getResult());
    }

    @Test
    public void testMoveHitCQPlayersBoard(){
        board.placeShip(new Ship("BATTLESHIP"), 5, 'D', false,false);
        board.placeShip(new Ship("MINESWEEPER"), 9, 'B', false,false);
        Result result = board.attack(5,'F');
        assertEquals(AtackStatus.CRITICAL, result.getResult());
        board.move(true,1);
        result = board.attack(4,'F');
        assertEquals(AtackStatus.SUNK, result.getResult());
    }

    @Test
    public void testMoveHitCQOpponentsBoard(){
        board.placeShip(new Ship("BATTLESHIP"), 5, 'D', false,false);
        board.placeShip(new Ship("MINESWEEPER"), 9, 'B', false,false);
        Result result = board.attack(5,'F');
        assertEquals(AtackStatus.CRITICAL, result.getResult());
        board.move(false,1);
        result = board.attack(4,'F');
        assertEquals(AtackStatus.SUNK, result.getResult());
    }

    @Test
    public void testMoveHitPlayersBoard(){
        board.placeShip(new Ship("BATTLESHIP"), 5, 'D', false,false);
        board.placeShip(new Ship("MINESWEEPER"), 9, 'B', false,false);
        Result result = board.attack(5,'E');
        assertEquals(AtackStatus.HIT, result.getResult());
        board.move(true,1);
        result = board.attack(4,'E');
        assertEquals(AtackStatus.INVALID, result.getResult());
    }

    @Test
    public void testMoveHitOpponentsBoard(){
        board.placeShip(new Ship("BATTLESHIP"), 5, 'D', false,false);
        board.placeShip(new Ship("MINESWEEPER"), 9, 'B', false,false);
        Result result = board.attack(5,'E');
        assertEquals(AtackStatus.HIT, result.getResult());
        board.move(false,1);
        result = board.attack(5,'E');
        assertEquals(AtackStatus.INVALID, result.getResult());
        result = board.attack(4,'E');
        assertEquals(AtackStatus.INVALID, result.getResult());
        result = board.attack(4,'F');
        assertEquals(AtackStatus.CRITICAL, result.getResult());
        result = board.attack(4,'F');
        assertEquals(AtackStatus.SUNK, result.getResult());
    }

    @Test
    public void testMoveHitLocationOpponentsBoard() {
        board.placeShip(new Ship("BATTLESHIP"), 5, 'D', false, false);
        board.placeShip(new Ship("MINESWEEPER"), 9, 'B', false, false);
        Result result = board.attack(5, 'E');
        assertEquals(AtackStatus.HIT, result.getResult());
        result = board.attack(4,'E');
        assertEquals(AtackStatus.MISS, result.getResult());
        board.move(false, 1);
        assertEquals(AtackStatus.HIT, board.getAttacks().get(0).getResult());
        Square testSquare = new Square(5,'E');
        assertEquals(testSquare, board.getAttacks().get(0).getLocation());
        assertEquals(AtackStatus.INVALID, board.getAttacks().get(1).getResult());
    }

    @Test
    public void testPhantomHitsOpponentsBoard() {
        board.placeShip(new Ship("BATTLESHIP"), 1, 'A', true, false);
        Result result = board.attack(1, 'A');
        assertEquals(AtackStatus.HIT, result.getResult());
        result = board.attack(2, 'A');
        assertEquals(AtackStatus.HIT, result.getResult());
        result = board.attack(3, 'A');
        assertEquals(AtackStatus.CRITICAL, result.getResult());
        result = board.attack(4, 'A');
        assertEquals(AtackStatus.HIT, result.getResult());
        result = board.attack(5, 'A');
        assertEquals(AtackStatus.MISS, result.getResult());

        board.move(false, 3);
        assertEquals(AtackStatus.HIT, board.getAttacks().get(0).getResult());
        assertEquals(AtackStatus.HIT, board.getAttacks().get(1).getResult());
        assertEquals(AtackStatus.CRITICAL, board.getAttacks().get(2).getResult());
        assertEquals(AtackStatus.HIT, board.getAttacks().get(3).getResult());
        assertEquals(AtackStatus.INVALID, board.getAttacks().get(4).getResult());
        assertEquals(5, board.getAttacks().size());
    }

    @Test
    public void testOverlapSunkShip(){
        board.placeShip(new Ship("MINESWEEPER"), 9, 'B', false, false);
        board.placeShip(new Ship("BATTLESHIP"), 8, 'A', false, false);
        Result result = board.attack(9, 'C');
        assertEquals(AtackStatus.SUNK, result.getResult());
        board.move(true, 3);
        assertTrue(board.getShips().get(1).isAtLocation(new Square(8,'A')));
        assertTrue(board.getShips().get(1).isAtLocation(new Square(8,'B')));
        assertTrue(board.getShips().get(1).isAtLocation(new Square(8,'C')));
        assertTrue(board.getShips().get(1).isAtLocation(new Square(8,'D')));
    }
}
