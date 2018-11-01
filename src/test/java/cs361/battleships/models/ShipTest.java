package cs361.battleships.models;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class ShipTest {

    @Test
    public void testPlaceMinesweeperHorizontaly() {
        Ship minesweeper = new Ship("MINESWEEPER");
        minesweeper.place('A', 1, false);
        List<Square> occupiedSquares = minesweeper.getOccupiedSquares();
        ArrayList<Object> expected = new ArrayList<>();
        expected.add(new Square(1, 'A'));
        expected.add(new Square(1, 'B'));
        assertEquals(expected, occupiedSquares);
    }

    @Test
    public void testPlaceMinesweeperVertically() {
        Ship minesweeper = new Ship("MINESWEEPER");
        minesweeper.place('A', 1, true);
        List<Square> occupiedSquares = minesweeper.getOccupiedSquares();
        ArrayList<Object> expected = new ArrayList<>();
        expected.add(new Square(1, 'A'));
        expected.add(new Square(2, 'A'));
        assertEquals(expected, occupiedSquares);
    }

    @Test
    public void testPlaceDestroyerHorizontaly() {
        Ship minesweeper = new Ship("DESTROYER");
        minesweeper.place('A', 1, false);
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
        minesweeper.place('A', 1, true);
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
        minesweeper.place('A', 1, false);
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
        minesweeper.place('A', 1, true);
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
        minesweeper1.place('A', 1, true);

        Ship minesweeper2 = new Ship("MINESWEEPER");
        minesweeper2.place('A', 1, true);

        assertTrue(minesweeper1.overlaps(minesweeper2));
    }

    @Test
    public void testShipsDontOverlap() {
        Ship minesweeper1 = new Ship("MINESWEEPER");
        minesweeper1.place('A', 1, true);

        Ship minesweeper2 = new Ship("MINESWEEPER");
        minesweeper2.place('C', 2, true);

        assertFalse(minesweeper1.overlaps(minesweeper2));
    }

    @Test
    public void testIsAtLocation() {
        Ship minesweeper = new Ship("BATTLESHIP");
        minesweeper.place('A', 1, true);

        assertTrue(minesweeper.isAtLocation(new Square(1, 'A')));
        assertTrue(minesweeper.isAtLocation(new Square(2, 'A')));
    }

    @Test
    public void testHit() {
        Ship minesweeper = new Ship("BATTLESHIP");
        minesweeper.place('A', 1, true);

        Result result = minesweeper.attack(1, 'A');
        assertEquals(AtackStatus.HIT, result.getResult());
        assertEquals(minesweeper, result.getShip());
        assertEquals(new Square(1, 'A'), result.getLocation());
    }

    @Test
    public void testSink() {
        Ship minesweeper = new Ship("MINESWEEPER");
        minesweeper.place('A', 1, true);

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
        minesweeper.place('C', 5, false);
        destroyer.place('C', 5, false);
        assertTrue(minesweeper.overlaps(destroyer));
    }

    @Test
    public void testAttackSameSquareTwice() {
        Ship minesweeper = new Ship("MINESWEEPER");
        minesweeper.place('A', 1, true);
        var result = minesweeper.attack(1, 'A');
        assertEquals(AtackStatus.HIT, result.getResult());
        result = minesweeper.attack(1, 'A');
        assertEquals(AtackStatus.INVALID, result.getResult());
    }

    @Test
    public void testEquals() {
        Ship minesweeper1 = new Ship("MINESWEEPER");
        minesweeper1.place('A', 1, true);
        Ship minesweeper2 = new Ship("MINESWEEPER");
        minesweeper2.place('A', 1, true);
        assertTrue(minesweeper1.equals(minesweeper2));
        assertEquals(minesweeper1.hashCode(), minesweeper2.hashCode());
    }

    @Test
    public void testCaptainsQuartersLocation() {

        Ship mineSw = new Ship("MINESWEEPER");
        mineSw.place('A', 1, false);
        Ship destr = new Ship("DESTROYER");
        destr.place('H', 5, true);
        Ship battleS = new Ship("BATTLESHIP");
        battleS.place('C', 4, true);

        assertEquals('B', mineSw.getCaptainQuarters().getColumn());
        assertEquals(1, mineSw.getCaptainQuarters().getRow());
        assertNotEquals('A', mineSw.getCaptainQuarters().getColumn());

        assertEquals('H', destr.getCaptainQuarters().getColumn());
        assertEquals(6, destr.getCaptainQuarters().getRow());
        assertNotEquals(7, destr.getCaptainQuarters().getRow());

        assertEquals('C', battleS.getCaptainQuarters().getColumn());
        assertEquals(6, battleS.getCaptainQuarters().getRow());
        assertNotEquals(5, battleS.getCaptainQuarters().getRow());
    }

    @Test
    public void testCaptainsQuartersSunk() {

        Ship mineSw = new Ship("MINESWEEPER");
        mineSw.place('A', 1, false);
        Ship destr = new Ship("DESTROYER");
        destr.place('H', 5, true);
        Ship battleS = new Ship("BATTLESHIP");
        battleS.place('C', 4, true);

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
        mineSw.place('A', 1, false);
        Ship destr = new Ship("DESTROYER");
        destr.place('H', 5, true);

        mineSw.attack(1, 'B');
        assertEquals(AtackStatus.INVALID, mineSw.attack(1, 'B').getResult());

        destr.attack(6, 'H');
        destr.attack(6, 'H');
        assertEquals(AtackStatus.INVALID, destr.attack(6, 'H').getResult());
    }

}
