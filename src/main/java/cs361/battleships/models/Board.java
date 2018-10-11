package cs361.battleships.models;

import java.util.ArrayList;
import java.util.List;

public class Board {

	private char [][] gameBoard;
	/*
	DO NOT change the signature of this method. It is used by the grading scripts.
	 */
	public Board() {
		gameBoard = new char[10][10];

		for(int i = 0; i < 10; i++){
			char col = 'A';
			for(int j = 0; j < 10; j++){
				gameBoard[i][j] = '#';
			}
		}
	}

	/*
	DO NOT change the signature of this method. It is used by the grading scripts.
	 */
	public boolean placeShip(Ship ship, int x, char y, boolean isVertical) {

		if(y < 'A' || y > 'J' || x < 1 || x > 10){
			return false;
		}

		int shipLength = 0;

		if(isVertical) {
			if (ship.getName().equals("MINESWEEPER")) {
				if (y > 'I')
					return false;

				shipLength = 2;

			} else if (ship.getName().equals("DESTROYER")) {
				if (y > 'H')
					return false;

				shipLength = 3;

			} else if (ship.getName().equals("BATTLESHIP")) {
				if (y > 'G')
					return false;

				shipLength = 4;

			}

			for (int j = 0; j < shipLength; j++) {
				ship.setOccupiedSquares(x, y);

				int d = y - 65;
				gameBoard[x - 1][d] = 's';
				x++;
			}
		} else {
			if(ship.getName().equals("MINESWEEPER")) {
				if(y > 'I')
					return false;

				shipLength = 2;

			} else if(ship.getName().equals("DESTROYER")) {
				if(y > 'H')
					return false;

				shipLength = 3;

			} else if(ship.getName().equals("BATTLESHIP")) {
				if (y > 'G')
					return false;

				shipLength = 4;

			}

			for(int j = 0; j < shipLength; j++) {
				ship.setOccupiedSquares(x, y);

				int d = y - 65;
				gameBoard[x - 1][d] = 's';

				y++;
			}
		}

		return true;
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
