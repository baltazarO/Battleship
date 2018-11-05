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

	@JsonProperty private String kind;
	@JsonProperty private List<Square> occupiedSquares;
	@JsonProperty private int size;
	//captains quarters is referring to a specific occupied square, it acts like a pointer
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

	public Result attack(int x, char y) {
		var attackedLocation = new Square(x, y);
		var square = getOccupiedSquares().stream().filter(s -> s.equals(attackedLocation)).findFirst();

		if (!square.isPresent()) {
			//returns miss
			return new Result(attackedLocation);
		}
		var attackedSquare = square.get();
		if(attackedSquare.isCaptains() && armour > 0){
            Result critical = new Result(attackedLocation);
            critical.setShip(this);
            armour--;

		    if(armour == 1) {
		    	critical.setResult(AtackStatus.CRITICAL);
            }
            else {
                //armour is 0
				attackedSquare.hit();
				for(int i=0; i < getOccupiedSquares().size(); i++){
					if(!getOccupiedSquares().get(i).isHit()){
						occupiedSquares.get(i).hit();
					}
				}
                critical.setResult(AtackStatus.SUNK);
            }
		    return critical;
        }

		//when square is already hit
		if (attackedSquare.isHit()) {
			var result = new Result(attackedLocation);
			result.setResult(AtackStatus.INVALID);
			return result;
		}
		attackedSquare.hit();

		//default
		var result = new Result(attackedLocation);
		result.setShip(this);
		if (isSunk()) {
			result.setResult(AtackStatus.SUNK);
		} else {
			result.setResult(AtackStatus.HIT);
		}
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
}
