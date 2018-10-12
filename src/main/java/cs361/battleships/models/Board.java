package cs361.battleships.models;

import java.util.ArrayList;
import java.util.List;

public class Board {

	private char [][] gameBoard;
	private List<Ship> fleet;
	private List<Result> attacks;
	/*
	DO NOT change the signature of this method. It is used by the grading scripts.
	 */
	public Board() {
		gameBoard = new char[10][10];
		fleet = new ArrayList<>();
		attacks = new ArrayList<>();

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


	    if(x > 10 || x < 1 || y < 'A' || y > 'J') {
	        attacks.add(new Result(AtackStatus.INVALID));
            return new Result(AtackStatus.INVALID);
        }


		char spotChar = gameBoard[x-1][y-65];
		if(spotChar == '#'){
		    gameBoard[x-1][y-65] = 'm';


            Result hap = new Result(AtackStatus.MISS);
            attacks.add(hap);
            return hap;
        } else if(spotChar == 's') {
            gameBoard[x-1][y-65] = 'h';
            attacks.add(new Result(AtackStatus.HIT));

            //check sides
			boolean alive = false;
			if(x > 1){ //check above
				if(gameBoard[x-2][y-65] == 's'){
					alive = true;
				}
			}
			if(x < 10){ //check below
				if(gameBoard[x][y-65] == 's'){
					alive = true;
				}
			}
			if(y-65 > 0){ //check left
				if(gameBoard[x-1][y-66] == 's'){
					alive = true;
				}
			}
			if(y-65 < 9){ //check right
				if(gameBoard[x-1][y-64] == 's'){
					alive = true;
				}
			}
			if(!alive) {
				attacks.add(new Result(AtackStatus.SUNK));
			}
            return new Result(AtackStatus.HIT);
        } else if (spotChar == 'm') {
		    attacks.add(new Result(AtackStatus.MISS));
            return new Result(AtackStatus.MISS);
        } else if (spotChar == 'h') {
            attacks.add(new Result(AtackStatus.HIT));
            return new Result(AtackStatus.HIT);
        }
        return new Result(AtackStatus.INVALID);
	}

	public List<Ship> getShips() {
		return fleet;
	}

	public void setShips(List<Ship> ships) {
	    for(int k = 0; k < ships.size(); k++) {
            boolean isVertical = true;
            Ship s1 = ships.get(k);
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

	public List<Result> getAttacks() { return attacks; }

	public void setAttacks(List<Result> attacks) {
		for(int i = 0; i < attacks.size(); i++){
		    this.attacks.add(attacks.get(i));

        }
	}
}
