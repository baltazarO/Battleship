package cs361.battleships.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Sets;
import com.mchange.v1.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class Ship {

	@JsonProperty protected String kind;
	@JsonProperty protected List<Square> occupiedSquares;
	@JsonProperty protected int size;
	@JsonProperty private int armour;

	public Ship() {
		occupiedSquares = new ArrayList<>();
	}
	
	public Ship(String kind) {
		this();
		this.kind = kind;
		switch(kind) {
			case "MINESWEEPER":
				size = 2;
				armour = 1;
				break;
			case "DESTROYER":
				size = 3;
				armour = 2;
				break;
			case "BATTLESHIP":
				size = 4;
				armour = 2;
				break;
		}

	}

	public List<Square> getOccupiedSquares() {
		return occupiedSquares;
	}

	public void place(char col, int row, boolean isVertical, boolean captIsLeft) {
		var cq = 1;
		if(captIsLeft && size == 2){ cq = 0; }
		if(!captIsLeft && size == 4){ cq = 2; }
		for (int i=0; i<size; i++) {
			if (isVertical) {
				occupiedSquares.add(new Square(row+i, col));
				if(i == cq){
					occupiedSquares.get(i).setCaptains(true);
				}
			} else {
				occupiedSquares.add(new Square(row, (char) (col + i)));
				if(i == cq){
					occupiedSquares.get(i).setCaptains(true);
				}
			}
		}
	}

	public boolean overlaps(Ship other) {
		Set<Square> thisSquares = Set.copyOf(getOccupiedSquares());
		Set<Square> otherSquares = Set.copyOf(other.getOccupiedSquares());
		Sets.SetView<Square> intersection = Sets.intersection(thisSquares, otherSquares);
		return intersection.size() != 0;
	}

	public boolean isAtLocation(Square location) {
		return getOccupiedSquares().stream().anyMatch(s -> s.equals(location));
	}

	public String getKind() {
		return kind;
	}

	public AtackStatus attackCaptain() {
		armour--;
		if(armour == 1) {
			return AtackStatus.CRITICAL;
		}
		else { //armour is 0
			getOccupiedSquares().stream().forEach(s -> s.hit());
			return AtackStatus.SUNK;
		}
	}

	public Result attack(int x, char y) {
		var attackedLocation = new Square(x, y);
		var square = getOccupiedSquares().stream().filter(s -> s.equals(attackedLocation)).findFirst();
		var attackedSquare = square.get();
		var result = new Result(attackedLocation);
		result.setShip(this);

		if(attackedSquare.isCaptains() && armour > 0){
            result.setResult(attackCaptain());
            return result;
        }

		//when square is already hit
		if (attackedSquare.isHit()) {
			result.setResult(AtackStatus.INVALID);
			return result;
		}
		attackedSquare.hit();
		//No need to check if is sunk because ships can only sink with CQ as last hit
		result.setResult(AtackStatus.HIT);
		return result;
	}

	@JsonIgnore
	public boolean isSunk() {
		return getOccupiedSquares().stream().allMatch(s -> s.isHit());
	}

	@Override
	public boolean equals(Object other) {
		if (!(other instanceof Ship)) {
			return false;
		}
		var otherShip = (Ship) other;

		return this.kind.equals(otherShip.kind)
				&& this.size == otherShip.size
				&& this.occupiedSquares.equals(otherShip.occupiedSquares);
	}

	@Override
	public int hashCode() {
		return 33 * kind.hashCode() + 23 * size + 17 * occupiedSquares.hashCode();
	}

	@Override
	public String toString() {
		return kind + occupiedSquares.toString();
	}


	public int getArmour(){return armour;}

	public int getSize(){ return size; }

	public void move(int dir) {
		if(dir == 0 || dir == 4) { //west
			mover(false, -1);
		}
		else if(dir == 1 || dir == 5) { //north
			mover(true, -1);
		}
		else if(dir == 2) { //east
			mover(false, 1);
		}
		else if(dir == 3) { //south
			mover(true, 1);
		}
	}

	private void mover(boolean row, int mod) {
		if(row) {
			var flag = true; //used to check out of bounds
			for(Square s : occupiedSquares) {
				var checker = new Square(s.getRow() + mod,s.getColumn());
				if(checker.isOutOfBounds()) {
					flag = false;
				}
			}
			if(flag) { //actually moves here
				for(Square s : occupiedSquares) {
					s.setRow(s.getRow() + mod);
				}
			}
		}
		else { //if column
			var flag = true;
			for(Square s : occupiedSquares) {
				var y = s.getColumn() + mod;
				var checker = new Square(s.getRow(),(char) y);
				if(checker.isOutOfBounds()) {
					flag = false;
				}
			}
			if(flag) {
				for(Square s : occupiedSquares) {
					var y = s.getColumn() + mod;
					s.setColumn((char) y);
				}
			}
		}
	}
}
