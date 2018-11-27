package cs361.battleships.models;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class ShipTest {

    @Test
    public void testPlaceMinesweeperHorizontaly() {
        Ship minesweeper = new Ship("MINESWEEPER");
        minesweeper.place('A', 1, false, false);
        List<Square> occupiedSquares = minesweeper.getOccupiedSquares();
        ArrayList<Object> expected = new ArrayList<>();
        expected.add(new Square(1, 'A'));
        expected.add(new Square(1, 'B'));
        assertEquals(expected, occupiedSquares);
    }

    @Test
    public void testPlaceMinesweeperVertically() {
        Ship minesweeper = new Ship("MINESWEEPER");
        minesweeper.place('A', 1, true, false);
        List<Square> occupiedSquares = minesweeper.getOccupiedSquares();
        ArrayList<Object> expected = new ArrayList<>();
        expected.add(new Square(1, 'A'));
        expected.add(new Square(2, 'A'));
        assertEquals(expected, occupiedSquares);
    }

    @Test
    public void testPlaceDestroyerHorizontaly() {
        Ship minesweeper = new Ship("DESTROYER");
        minesweeper.place('A', 1, false, false);
        List<Square> occupiedSquares = minesweeper.getOccupiedSquares();
        ArrayList<Object> expected = new ArrayList<>();
        expected.add(new Square(1, 'A'));
        expected.add(new Square(1, 'B'));
        expected.add(new Square(1, 'C'));
        assertEquals(expected, occupiedSquares);
    }

    @Test
    public void testPlaceDestroyerVertically() {
        Ship minesweeper = new Ship("DESTROYER");
        minesweeper.place('A', 1, true, false);
        List<Square> occupiedSquares = minesweeper.getOccupiedSquares();
        ArrayList<Object> expected = new ArrayList<>();
        expected.add(new Square(1, 'A'));
        expected.add(new Square(2, 'A'));
        expected.add(new Square(3, 'A'));
        assertEquals(expected, occupiedSquares);
    }

    @Test
    public void testPlaceBattleshipHorizontaly() {
        Ship minesweeper = new Ship("BATTLESHIP");
        minesweeper.place('A', 1, false, false);
        List<Square> occupiedSquares = minesweeper.getOccupiedSquares();
        ArrayList<Object> expected = new ArrayList<>();
        expected.add(new Square(1, 'A'));
        expected.add(new Square(1, 'B'));
        expected.add(new Square(1, 'C'));
        expected.add(new Square(1, 'D'));
        assertEquals(expected, occupiedSquares);
    }

    @Test
    public void testPlaceBattleshipVertically() {
        Ship minesweeper = new Ship("BATTLESHIP");
        minesweeper.place('A', 1, true, false);
        List<Square> occupiedSquares = minesweeper.getOccupiedSquares();
        ArrayList<Object> expected = new ArrayList<>();
        expected.add(new Square(1, 'A'));
        expected.add(new Square(2, 'A'));
        expected.add(new Square(3, 'A'));
        expected.add(new Square(4, 'A'));
        assertEquals(expected, occupiedSquares);
    }

    @Test
    public void testShipOverlaps() {
        Ship minesweeper1 = new Ship("MINESWEEPER");
        minesweeper1.place('A', 1, true, false);

        Ship minesweeper2 = new Ship("MINESWEEPER");
        minesweeper2.place('A', 1, true, false);

        assertTrue(minesweeper1.overlaps(minesweeper2));
    }

    @Test
    public void testShipsDontOverlap() {
        Ship minesweeper1 = new Ship("MINESWEEPER");
        minesweeper1.place('A', 1, true, false);

        Ship minesweeper2 = new Ship("MINESWEEPER");
        minesweeper2.place('C', 2, true, false);

        assertFalse(minesweeper1.overlaps(minesweeper2));
    }

    @Test
    public void testIsAtLocation() {
        Ship minesweeper = new Ship("BATTLESHIP");
        minesweeper.place('A', 1, true, false);

        assertTrue(minesweeper.isAtLocation(new Square(1, 'A')));
        assertTrue(minesweeper.isAtLocation(new Square(2, 'A')));
    }

    @Test
    public void testHit() {
        Ship minesweeper = new Ship("BATTLESHIP");
        minesweeper.place('A', 1, true, false);

        Result result = minesweeper.attack(1, 'A');
        assertEquals(AtackStatus.HIT, result.getResult());
        assertEquals(minesweeper, result.getShip());
        assertEquals(new Square(1, 'A'), result.getLocation());
    }

    @Test
    public void testSink() {
        Ship minesweeper = new Ship("MINESWEEPER");
        minesweeper.place('A', 1, true, false);

        minesweeper.attack(1, 'A');
        Result result = minesweeper.attack(2, 'A');

        assertEquals(AtackStatus.SUNK, result.getResult());
        assertEquals(minesweeper, result.getShip());
        assertEquals(new Square(2, 'A'), result.getLocation());
    }

    @Test
    public void testOverlapsBug() {
        Ship minesweeper = new Ship("MINESWEEPER");
        Ship destroyer = new Ship("DESTROYER");
        minesweeper.place('C', 5, false, false);
        destroyer.place('C', 5, false, false);
        assertTrue(minesweeper.overlaps(destroyer));
    }

    @Test
    public void testAttackSameSquareTwice() {
        Ship minesweeper = new Ship("MINESWEEPER");
        minesweeper.place('A', 1, true, false);
        var result = minesweeper.attack(1, 'A');
        assertEquals(AtackStatus.HIT, result.getResult());
        result = minesweeper.attack(1, 'A');
        assertEquals(AtackStatus.INVALID, result.getResult());
    }

    @Test
    public void testEquals() {
        Ship minesweeper1 = new Ship("MINESWEEPER");
        minesweeper1.place('A', 1, true, false);
        Ship minesweeper2 = new Ship("MINESWEEPER");
        minesweeper2.place('A', 1, true, false);
        assertTrue(minesweeper1.equals(minesweeper2));
        assertEquals(minesweeper1.hashCode(), minesweeper2.hashCode());
    }

    @Test
    public void testCaptainsQuartersLocation() {

        Ship mineSw = new Ship("MINESWEEPER");
        mineSw.place('A', 1, false, false);
        Ship destr = new Ship("DESTROYER");
        destr.place('H', 5, true, false);
        Ship battleS = new Ship("BATTLESHIP");
        battleS.place('C', 4, true, false);

        assertEquals('B', mineSw.getOccupiedSquares().get(1).getColumn());
        assertEquals(1, mineSw.getOccupiedSquares().get(1).getRow());
        assertNotEquals('A', mineSw.getOccupiedSquares().get(1).getColumn());

        assertEquals('H', destr.getOccupiedSquares().get(1).getColumn());
        assertEquals(6, destr.getOccupiedSquares().get(1).getRow());
        assertNotEquals(7, destr.getOccupiedSquares().get(1).getRow());

        assertEquals('C', battleS.getOccupiedSquares().get(2).getColumn());
        assertEquals(6, battleS.getOccupiedSquares().get(2).getRow());
        assertNotEquals(5, battleS.getOccupiedSquares().get(2).getRow());
    }

    @Test
    public void testCaptainsQuartersSunk() {

        Ship mineSw = new Ship("MINESWEEPER");
        mineSw.place('A', 1, false, false);
        Ship destr = new Ship("DESTROYER");
        destr.place('H', 5, true, false);
        Ship battleS = new Ship("BATTLESHIP");
        battleS.place('C', 4, true, false);

        assertEquals(AtackStatus.SUNK, mineSw.attack(1, 'B').getResult());
        assertEquals(0, mineSw.getArmour());
        assertEquals(AtackStatus.CRITICAL, destr.attack(6, 'H').getResult());
        assertEquals(1, destr.getArmour());
        assertEquals(AtackStatus.SUNK, destr.attack(6, 'H').getResult());
        assertEquals(0, destr.getArmour());
        assertEquals(AtackStatus.CRITICAL, battleS.attack(6, 'C').getResult());
        assertEquals(1, battleS.getArmour());
        assertEquals(AtackStatus.SUNK, battleS.attack(6, 'C').getResult());
        assertEquals(0, battleS.getArmour());
    }

    @Test
    public void testHittingCQTooMuch() {

        Ship mineSw = new Ship("MINESWEEPER");
        mineSw.place('A', 1, false, false);
        Ship destr = new Ship("DESTROYER");
        destr.place('H', 5, true, false);

        mineSw.attack(1, 'B');
        assertEquals(AtackStatus.INVALID, mineSw.attack(1, 'B').getResult());

        destr.attack(6, 'H');
        destr.attack(6, 'H');
        assertEquals(AtackStatus.INVALID, destr.attack(6, 'H').getResult());
    }

    @Test
    public void testRotateCQHorizontal() {
        Ship mineSw = new Ship("MINESWEEPER");
        mineSw.place('A', 1, false, true);
        Ship batship = new Ship("BATTLESHIP");
        batship.place('A', 8, false, true);
        assertEquals(AtackStatus.SUNK, mineSw.attack(1,'A').getResult());
        batship.attack(8,'B');
        assertEquals(AtackStatus.SUNK, batship.attack(8,'B').getResult());
    }

    @Test
    public void testRotateCQVertical() {
        Ship mineSw = new Ship("MINESWEEPER");
        mineSw.place('A', 1, true, true);
        Ship batship = new Ship("BATTLESHIP");
        batship.place('A', 7, true, true);
        assertEquals(AtackStatus.SUNK, mineSw.attack(1,'A').getResult());
        batship.attack(8,'A');
        assertEquals(AtackStatus.SUNK, batship.attack(8,'A').getResult());
    }

    @Test
    public void testMoveWest(){
        Ship mineSw = new Ship("MINESWEEPER");
        mineSw.place('E', 5, false, true);
        mineSw.move(0);
        assertTrue(mineSw.isAtLocation(new Square(5,'D')));
        assertTrue(mineSw.isAtLocation(new Square(5,'E')));
    }

    @Test
    public void testMoveNorth(){
        Ship mineSw = new Ship("MINESWEEPER");
        mineSw.place('E', 5, false, true);
        mineSw.move(1);
        assertTrue(mineSw.isAtLocation(new Square(4,'E')));
        assertTrue(mineSw.isAtLocation(new Square(4,'F')));
    }

    @Test
    public void testMoveEast(){
        Ship mineSw = new Ship("MINESWEEPER");
        mineSw.place('E', 5, false, true);
        mineSw.move(2);
        assertTrue(mineSw.isAtLocation(new Square(5,'G')));
        assertTrue(mineSw.isAtLocation(new Square(5,'F')));
    }

    @Test
    public void testMoveSouth(){
        Ship mineSw = new Ship("MINESWEEPER");
        mineSw.place('E', 5, false, true);
        mineSw.move(3);
        assertTrue(mineSw.isAtLocation(new Square(6,'E')));
        assertTrue(mineSw.isAtLocation(new Square(6,'F')));
    }

    @Test
    public void testMoveWestBoundry(){
        Ship mineSw = new Ship("MINESWEEPER");
        mineSw.place('A', 7, false, true);
        mineSw.move(0);
        assertTrue(mineSw.isAtLocation(new Square(7,'B')));
        assertTrue(mineSw.isAtLocation(new Square(7,'A')));
    }

    @Test
    public void testMoveNorthBoundry(){
        Ship mineSw = new Ship("MINESWEEPER");
        mineSw.place('G', 1, false, true);
        mineSw.move(1);
        assertTrue(mineSw.isAtLocation(new Square(1,'G')));
        assertTrue(mineSw.isAtLocation(new Square(1,'H')));
    }

    @Test
    public void testMoveEastBoundry(){
        Ship mineSw = new Ship("MINESWEEPER");
        mineSw.place('I', 3, false, true);
        mineSw.move(2);
        assertTrue(mineSw.isAtLocation(new Square(3,'J')));
        assertTrue(mineSw.isAtLocation(new Square(3,'I')));
    }

    @Test
    public void testMoveSouthBoundry(){
        Ship mineSw = new Ship("MINESWEEPER");
        mineSw.place('A', 10, false, true);
        mineSw.move(3);
        assertTrue(mineSw.isAtLocation(new Square(10,'B')));
        assertTrue(mineSw.isAtLocation(new Square(10,'A')));
    }

}
