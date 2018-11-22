package cs361.battleships.models;

import net.sourceforge.htmlunit.corejs.javascript.Undefined;
import org.junit.Test;
import static org.junit.Assert.*;

public class GameTest {

    @Test
    public void testPlace(){
        Game game = new Game();
        assertFalse(game.placeShip(new Ship("MINESWEEPER"), 11, 'J',false,false));
        assertTrue(game.placeShip(new Ship("BATTLESHIP"), 1, 'B', true, false));
        assertFalse(game.placeShip(new Ship("MINESWEEPER"), 6, 'J',false,false));
        assertFalse(game.placeShip(new Ship("MINESWEEPER"),10,'E',true,false));
        assertFalse(game.placeShip(new Ship("MINESWEEPER"),4,'A',false,false));
        assertFalse(game.placeShip(new Ship("BATTLESHIP"),4,'G',false,false));
        assertTrue(game.placeShip(new Ship("MINESWEEPER"), 5, 'D', false, false));
        assertTrue(game.placeShip(new Ship("DESTROYER"), 9, 'A', false, false));
        assertFalse(game.placeShip(new Ship("MINESWEEPER"),2,'G',false,false));
    }

    @Test
    public void testAttack(){
        Game game = new Game();
        assertTrue(game.placeShip(new Ship("BATTLESHIP"), 1, 'B', true, false));
        assertTrue(game.placeShip(new Ship("MINESWEEPER"), 5, 'D', false, false));
        assertTrue(game.placeShip(new Ship("DESTROYER"), 9, 'A', false, false));

        assertTrue(game.attack(1,'A'));
        assertFalse(game.attack(1,'A'));
    }

    @Test
    public void testMove(){
        Game game = new Game();
        assertTrue(game.placeShip(new Ship("BATTLESHIP"), 1, 'B', true, false));
        assertTrue(game.placeShip(new Ship("MINESWEEPER"), 5, 'D', false, false));
        assertTrue(game.placeShip(new Ship("DESTROYER"), 9, 'A', false, false));

        assertTrue(game.move(2));
        assertFalse(game.move(4));
    }
}
