package cs361.battleships.models;

import java.util.ArrayList;
import java.util.List;

public class Board {

	private Square [][] gameBoard;
	/*
	DO NOT change the signature of this method. It is used by the grading scripts.
	 */
	public Board() {
		gameBoard = new Square[10][10];

		for(int i = 0; i < 10; i++){
			char col = 'A';
			for(int j = 0; j < 10; j++){
				gameBoard[i][j].setRow(i + 1);
				gameBoard[i][j].setColumn(col);
				col++;
			}
		}
	}

	/*
	DO NOT change the signature of this method. It is used by the grading scripts.
	 */
	public boolean placeShip(Ship ship, int x, char y, boolean isVertical) {
		// TODO Implement
		return false;
	}

	/*
	DO NOT change the signature of this method. It is used by the grading scripts.
	 */
	public Result attack(int x, char y) {
		//TODO Implement
		return null;
	}

	public List<Ship> getShips() {
		//TODO implement
		return null;
	}

	public void setShips(List<Ship> ships) {
		//TODO implement
	}

	public List<Result> getAttacks() {
		//TODO implement
		return null;
	}

	public void setAttacks(List<Result> attacks) {
		//TODO implement
	}
}
