package cs361.battleships.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static cs361.battleships.models.AtackStatus.*;

public class Game {

    @JsonProperty private Board playersBoard = new Board();
    @JsonProperty private Board opponentsBoard = new Board();

    /*
	DO NOT change the signature of this method. It is used by the grading scripts.
	 */
    public boolean placeShip(Ship ship, int x, char y, boolean isVertical, boolean captIsLeft) {
        boolean successful = playersBoard.placeShip(ship, x, y, isVertical, captIsLeft);
        if (!successful)
            return false;

        boolean opponentPlacedSuccessfully;
        do {
            // AI places random ships, so it might try and place overlapping ships
            // let it try until it gets it right
            opponentPlacedSuccessfully = opponentsBoard.placeShip(ship, randRow(), randCol(), randVertical(), randCaptIsLeft());
        } while (!opponentPlacedSuccessfully);

        return true;
    }

    public boolean placeSub(Submarine sub, int x, char y, boolean isVertical, boolean submerged) {
        boolean successful = playersBoard.placeSub(sub, x, y, isVertical, submerged);
        if (!successful)
            return false;

        boolean opponentPlacedSuccessfully;
        do {
            // AI places random ships, so it might try and place overlapping ships
            // let it try until it gets it right
            opponentPlacedSuccessfully = opponentsBoard.placeSub(sub, randRow(), randCol(), randVertical(), randCaptIsLeft());
        } while (!opponentPlacedSuccessfully);

        return true;
    }

    /*
	DO NOT change the signature of this method. It is used by the grading scripts.
	 */
    public boolean attack(int x, char  y) {
        Result playerAttack = opponentsBoard.attack(x, y);
        if (playerAttack.getResult() == INVALID) {
            return false;
        }

        Result opponentAttackResult;
        do {
            // AI does random attacks, so it might attack the same spot twice
            // let it try until it gets it right
            opponentAttackResult = playersBoard.attack(randRow(), randCol());
        } while(opponentAttackResult.getResult() == INVALID);

        //if the AI can move its fleet, attempt to move the fleet
        if(playersBoard.getShipsSunk() > 1 && opponentsBoard.getMoveFleetCalls() > 0){
            move(false, new Random().nextInt(4));
        }

        return true;
    }

    public boolean move(boolean isPlayersBoard, int dir) {
        var rand = new Random().nextInt(5);
        if(!isPlayersBoard && rand == 1) {
            return opponentsBoard.move(false, dir);
        }
        if(isPlayersBoard && opponentsBoard.getShipsSunk() > 1 && playersBoard.getMoveFleetCalls() > 0) {
            return playersBoard.move(true, dir);
        }
        else {
            return false;
        }
    }

    //for the tests
    public Square shipLocation(int index){
        return opponentsBoard.shipLocation(index);
    }

    private char randCol() {
        int random = new Random().nextInt(10);
        return (char) ('A' + random);
    }

    private int randRow() {
        return  new Random().nextInt(10) + 1;
    }

    private boolean randVertical() {
        return new Random().nextBoolean();
    }

    private boolean randCaptIsLeft() { return new Random().nextBoolean();}
}
