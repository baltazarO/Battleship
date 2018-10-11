package cs361.battleships.models;

import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class Ship {

	@JsonProperty private List<Square> occupiedSquares;
	private String shipName;

	public Ship() {

        	occupiedSquares = new ArrayList<>();
        	shipName = "";
	
	}
	
	public Ship(String kind) {

		occupiedSquares = new ArrayList<>();

		if(!(kind.equals("MINESWEEPER") || kind.equals("DESTROYER")
				|| kind.equals("BATTLESHIP"))) {
			throw new IllegalArgumentException("Invalid kind");
		}

		shipName = kind;

	}

	public void setOccupiedSquares(int x, char y) {
		occupiedSquares.add(new Square(x, y));
	}

	public List<Square> getOccupiedSquares() {
		//TODO implement
		return occupiedSquares;
	}

	public String getName(){ return shipName; }
}
