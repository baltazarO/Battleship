package cs361.battleships.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Board {

	@JsonProperty private List<Ship> ships;
	@JsonProperty private List<Result> attacks;
	@JsonProperty private int shipsSunk;
	@JsonProperty private int moveFleetCalls;

	/*
	DO NOT change the signature of this method. It is used by the grading scripts.
	 */
	public Board() {
		ships = new ArrayList<>();
		attacks = new ArrayList<>();
		shipsSunk = 0;
		moveFleetCalls = 2;
	}

	/*
	DO NOT change the signature of this method. It is used by the grading scripts.
	 */
	public boolean placeShip(Ship ship, int x, char y, boolean isVertical, boolean captIsLeft) {
		if (ships.size() >= 3) {
			return false;
		}
		//checks double placing a ship
		if (ships.stream().anyMatch(s -> s.getKind().equals(ship.getKind()))) {
			return false;
		}
		final var placedShip = new Ship(ship.getKind());
		placedShip.place(y, x, isVertical, captIsLeft);
		//if any ships are overlapping, fail
		if (ships.stream().anyMatch(s -> s.overlaps(placedShip))) {
			return false;
		}
		if (placedShip.getOccupiedSquares().stream().anyMatch(s -> s.isOutOfBounds())) {
			return false;
		}
		ships.add(placedShip);
		return true;
	}

	private boolean placeSubOnSurface(Submarine sub, int x, char y, boolean isVertical, boolean submerged){
		if (ships.size() >= 4) {
			return false;
		}
		//checks double placing a ship
		if (ships.stream().anyMatch(s -> s.getKind().equals(sub.getKind()))) {
			return false;
		}
		final var placedSub = new Submarine();
		placedSub.place(y, x, isVertical, submerged);
		//if any ships are overlapping, fail
		if (ships.stream().anyMatch(s -> s.overlaps(placedSub))) {
			return false;
		}
		if (placedSub.getOccupiedSquares().stream().anyMatch(s -> s.isOutOfBounds())) {
			return false;
		}
		ships.add(placedSub);
		return true;
	}
	public boolean placeSub(Submarine sub, int x, char y, boolean isVertical, boolean submerged){
		if(ships.size() >= 4)
			return false;
		//checks double placing a ship
		if (ships.stream().anyMatch(s -> s.getKind().equals("SUBMARINE"))) {
			return false;
		}
		if(!submerged)
			return placeSubOnSurface(sub, x, y, isVertical, submerged);
		//ships must exist to hide a submarine under
		if(ships.size() == 0)
			return false;
		//check if coordinates are under ship
		Square subHead = new Square(x, y);
		for(int i = 0; i < ships.size(); i++){
			for(int j = 0; j < ships.get(i).getOccupiedSquares().size(); j++){
				if(subHead.equals(ships.get(i).getOccupiedSquares().get(j))) {
					sub.place(y, x, isVertical, submerged);
					ships.add(sub);
					return true;
				}
			}
		}
		return false;
	}

	private boolean sIsCaptainsQ(Square s){
		for(int i = 0; i < ships.size(); i++){
			for(int j = 0; j < ships.get(i).getOccupiedSquares().size(); j++){
				if(s.equals(ships.get(i).getOccupiedSquares().get(j)) && ships.get(i).getOccupiedSquares().get(j).isCaptains()){
					return true;
				}
			}
		}
		return false;
	}

	/*
	DO NOT change the signature of this method. It is used by the grading scripts.
	 */
	public Result attack(int x, char y) {
		Square candidateSquare = new Square(x, y);
		//so I need to see if I am some ship's lucky spot if I am, mark atackResult with setCaptain
		if(sIsCaptainsQ(candidateSquare)) {
			candidateSquare.setCaptains(true);
		}

		Result attackResult = attack(candidateSquare); //private attack method called here
		if(attackResult.getResult() != AtackStatus.INVALID){attacks.add(attackResult);}
		return attackResult;
	}

	private void sinkShip(Ship ship) {
		for(int i=0; i < ship.getOccupiedSquares().size(); i++){
			Result sunkShip = new Result(ship.getOccupiedSquares().get(i));
			sunkShip.setResult(AtackStatus.SUNK);
			attacks.add(sunkShip);
		}
	}

	private Result attack(Square s) {
		/*
		var shipsAtSquare = ships.stream().filter(ship -> ship.isAtLocation(s)).collect(Collectors.toList());
		if(shipsAtSquare.size() == 2){
			//handle two ships, no CQ involved

		}*/
		//go here if normal attacked square
		if (attacks.stream().anyMatch(r -> r.getLocation().equals(s) && !r.getResult().equals(AtackStatus.INVALID)) && !s.isCaptains()) {
			var attackResult = new Result(s);
			attackResult.setResult(AtackStatus.INVALID);
			return attackResult;
		}
		var shipsAtLocation = ships.stream().filter(ship -> ship.isAtLocation(s)).collect(Collectors.toList());
		if (shipsAtLocation.size() == 0) {
			var attackResult = new Result(s);
			return attackResult;
		}
		var hitShip = shipsAtLocation.get(0);
		var attackResult = hitShip.attack(s.getRow(), s.getColumn());
		if (attackResult.getResult() == AtackStatus.SUNK) {
			shipsSunk++;
			sinkShip(hitShip);
			if (ships.stream().allMatch(ship -> ship.isSunk())) {
				attackResult.setResult(AtackStatus.SURRENDER);
			}
		}
		return attackResult;
	}

	public boolean move(boolean isPlayersBoard, int dir) {
		if(dir > 3 || dir < 0){
			return false;
		}
		//Invalid attacks wont prevent attacking the square again
		switchAttackResultsOnShips(AtackStatus.INVALID, AtackStatus.MISS); //convert invalid to miss
		if(isPlayersBoard){ removeHitsOnShips(); }
		//move all the ships that are not sunk, regardless of overlap
		ships.forEach((ship) -> {
			if(!ship.isSunk()){
				ship.move(dir); //boundries are handled in Ship.java
			}
		});
		//Fix any overlaps, may swap positions of ships depending on order in list
		var flag = true;
		while(flag) {
			flag = false;
			for(var i=0; i<ships.size(); i++){
				for(var j=i+1; j<ships.size(); j++){
					if(ships.get(i).overlaps(ships.get(j))){
						ships.get(j).move(dir + 2);
						flag = true;
					}
				}
			}
		}
		//remove any attacks in the new positions and add hits to ships and CQ for players board
		switchAttackResultsOnShips(AtackStatus.MISS, AtackStatus.INVALID);
		if(isPlayersBoard){ addHitsOnShips(); }
		moveFleetCalls--;
		return true;
	}

	private void switchAttackResultsOnShips(AtackStatus ifthis, AtackStatus assignthis){
		for(Ship ship : ships){
			if(!ship.isSunk()) {
				for (Square s : ship.getOccupiedSquares()) {
					for (Result r : attacks) {
						if (r.getLocation().equals(s) && r.getResult().equals(ifthis)){
							r.setResult(assignthis);
						}
					}
				}
			}
		}
	}

	private void removeHitsOnShips(){
		for(Ship ship : ships){
			if(!ship.isSunk()) {
				for (Square s : ship.getOccupiedSquares()) {
					attacks.removeIf(r -> r.getLocation().equals(s) && r.getResult().equals(AtackStatus.HIT) || r.getResult().equals(AtackStatus.CRITICAL));
				}
			}
		}
	}

	private void addHitsOnShips(){
		for(Ship ship : ships) {
			if (!ship.isSunk()) {
				for (Square s : ship.getOccupiedSquares()) {
					if(s.isHit()){
						Result hit = new Result(s);
						hit.setResult(AtackStatus.HIT);
						attacks.add(hit);
					}
					if(s.isCaptains()){
						if(ship.getArmour() == 1 && !ship.getKind().equals("MINESWEEPER")){
							Result crit = new Result(s);
							crit.setResult(AtackStatus.CRITICAL);
							attacks.add(crit);
						}
					}
				}
			}
		}
	}

	//for tests, used to sink opponents randomly placed ships
	public Square shipLocation(int index) {
		for( Square s : ships.get(index).getOccupiedSquares()) {
			if(s.isCaptains()){
				return s;
			}
		}
		return null;
	}

	List<Ship> getShips() {
		return ships;
	}
	List<Result> getAttacks() { return attacks; } //for the tests
	int getShipsSunk() { return shipsSunk; }
	int getMoveFleetCalls() { return moveFleetCalls; }
}
