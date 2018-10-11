package cs361.battleships.models;

import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class Ship {

	@JsonProperty private List<Square> occupiedSquares;
	private boolean vertical;
	private String shipName;

	public Ship() {

        	occupiedSquares = new ArrayList<>();
        	vertical = false;
        	shipName = "";
	
	}
	
	public Ship(String kind, boolean isVertical, Square myPoint) {

		occupiedSquares = new ArrayList<>();

		if(myPoint.getColumn() < 'A' || myPoint.getColumn() > 'J'
				|| myPoint.getRow() < 1 || myPoint.getRow() > 10){
			throw new IllegalArgumentException("Your off the board");
		}

		vertical = isVertical;

		if(!(kind.equals("MINESWEEPER") || kind.equals("DESTROYER")
				|| kind.equals("BATTLESHIP"))) {
			throw new IllegalArgumentException("Invalid kind");
		}

		shipName = kind;

		//am I vertical?
		if(vertical) {
			//if i am vertical case, I will have a top point
			int myRow = myPoint.getRow();

			if(shipName.equals("MINESWEEPER")) {
				if(myPoint.getRow() > 9)
					throw new IllegalArgumentException("Off board");

				for(int i = 0; i < 2; i++) {
					occupiedSquares.add(new Square(myRow + i, myPoint.getColumn()));
				}
			} else if(shipName.equals("DESTROYER")) {
				if(myPoint.getRow() > 8)
					throw new IllegalArgumentException("Off board");

				occupiedSquares.add(new Square(myRow, myPoint.getColumn()));
				occupiedSquares.add(new Square(myRow + 1, myPoint.getColumn()));
				occupiedSquares.add(new Square(myRow + 2, myPoint.getColumn()));


			} else if(shipName.equals("BATTLESHIP")) {
				if(myPoint.getRow() > 7)
					throw new IllegalArgumentException("Off board");

				for(int i = 0; i < 4; i++) {
					occupiedSquares.add(new Square(myRow + i, myPoint.getColumn()));
				}
			}


		} else {
			//if i am not horizontal, I will have left point
			char myCol = myPoint.getColumn();

			if(shipName.equals("MINESWEEPER")) {
				if(myPoint.getColumn() > 'I')
					throw new IllegalArgumentException("Off board");

				for(int i = 0; i < 2; i++) {
					occupiedSquares.add(new Square(myPoint.getRow(), myCol));
					myCol++;
				}
			} else if(shipName.equals("DESTROYER")) {
				if(myPoint.getColumn() > 'H')
					throw new IllegalArgumentException("Off board");

				for(int i = 0; i < 3; i++) {
					occupiedSquares.add(new Square(myPoint.getRow(), myCol));
					myCol++;
				}
			} else if(shipName.equals("BATTLESHIP")) {
				if(myPoint.getColumn() > 'G')
					throw new IllegalArgumentException("Off board");

				for(int i = 0; i < 4; i++) {
					occupiedSquares.add(new Square(myPoint.getRow(), myCol));
					myCol++;
				}
			}
		}


		//how long am I, check kind use .equals()
	}

	public List<Square> getOccupiedSquares() {
		//TODO implement
		return occupiedSquares;
	}

}
