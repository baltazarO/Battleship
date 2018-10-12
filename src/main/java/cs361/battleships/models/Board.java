package cs361.battleships.models;

import java.util.ArrayList;
import java.util.List;

public class Board {

	private char [][] gameBoard;
	private List<Ship> fleet;
	/*
	DO NOT change the signature of this method. It is used by the grading scripts.
	 */
	public Board() {
		gameBoard = new char[10][10];
		fleet = new ArrayList<>();

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
				if (x > 9)
					return false;

				shipLength = 2;

			} else if (ship.getName().equals("DESTROYER")) {
				if (x > 8)
					return false;

				shipLength = 3;

			} else if (ship.getName().equals("BATTLESHIP")) {
				if (x > 7)
					return false;

				shipLength = 4;

			}

            int count = x;
            for (int i = 0; i < shipLength; i++){

                char start = gameBoard[count-1][y-65];
                if (start == 's'){
                    return false;
                }
                count++;
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

			char count = y;
			for (int i = 0; i < shipLength; i++){

			    char start = gameBoard[x-1][count-65];
			    if (start == 's'){
			        return false;
                }
			    count++;
            }


			for(int j = 0; j < shipLength; j++) {
				ship.setOccupiedSquares(x, y);

				int d = y - 65;
				gameBoard[x - 1][d] = 's';

				y++;
			}
		}

        fleet.add(ship);

		return true;
	}

	/*
	DO NOT change the signature of this method. It is used by the grading scripts.
	 */
	public Result attack(int x, char y) {


	    if(x > 10 || x < 1 || y < 'A' || y > 'J')
	        return new Result(AtackStatus.INVALID);


		char spotChar = gameBoard[x-1][y-65];
		if(spotChar == '#'){
		    gameBoard[x-1][y-65] = 'm';

            Result hap = new Result(AtackStatus.MISS);
            return hap;
        } else if(spotChar == 's') {
            gameBoard[x-1][y-65] = 'h';

		    return new Result(AtackStatus.HIT);
        } else
		    return new Result(AtackStatus.INVALID);

		char attackVal = gameBoard[x - 1][y - 65];

		if (attackVal == '#'){
			gameBoard[x][y] = 'm';
		}

		if (attackVal == 's'){
			gameBoard[x][y] = 'h';
		}

		else {
			return false;
		}
		return null;

	}

	public List<Ship> getShips() {
		return fleet;
	}

	public void setShips(List<Ship> ships) {
	    for(int k = 0; k < ships.size(); k++) {
            boolean isVertical = true;
            Ship s1 = ships.get(0);
            int r1 = s1.getOccupiedSquares().get(0).getRow();
            char c1 = s1.getOccupiedSquares().get(0).getColumn();

            int r2 = s1.getOccupiedSquares().get(1).getRow();
            char c2 = s1.getOccupiedSquares().get(1).getColumn();

            if (r1 == r2) {
                isVertical = false;
            } else if (c1 == c2) {
                isVertical = true;
            }

            placeShip(s1, r1, c1, isVertical);
        }

	}

	public List<Result> getAttacks() {
		//TODO implement
		return null;
	}

	public void setAttacks(List<Result> attacks) {
		//TODO implement
	}
}
