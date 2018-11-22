package cs361.battleships.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Board {

	@JsonProperty private List<Ship> ships;
	@JsonProperty private List<Result> attacks;

	/*
	DO NOT change the signature of this method. It is used by the grading scripts.
	 */
	public Board() {
		ships = new ArrayList<>();
		attacks = new ArrayList<>();
	}

	/*
	DO NOT change the signature of this method. It is used by the grading scripts.
	 */
	public boolean placeShip(Ship ship, int x, char y, boolean isVertical, boolean captIsLeft) {
		if (ships.size() >= 3) {
			return false;
		}
		if (ships.stream().anyMatch(s -> s.getKind().equals(ship.getKind()))) {
			return false;
		}
		final var placedShip = new Ship(ship.getKind());
		placedShip.place(y, x, isVertical, captIsLeft);
		if (ships.stream().anyMatch(s -> s.overlaps(placedShip))) {
			return false;
		}
		if (placedShip.getOccupiedSquares().stream().anyMatch(s -> s.isOutOfBounds())) {
			return false;
		}
		ships.add(placedShip);
		return true;
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
		//go here if normal attacked square
		if (attacks.stream().anyMatch(r -> r.getLocation().equals(s)) && !s.isCaptains()) {
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
			sinkShip(hitShip);
			if (ships.stream().allMatch(ship -> ship.isSunk())) {
				attackResult.setResult(AtackStatus.SURRENDER);
			}
		}
		return attackResult;
	}

	public boolean move(int dir) {
		if(dir > 3){
			return false;
		}
		//Remove all attacks on ships, Its easier to just remove them all since we can get the locations from the ships
		removeAttacksOnShips();
		addAttacksOnShips(false);
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
		//remove any attacks in the new positions and add hits to ships and CQ
		removeAttacksOnShips();
		addAttacksOnShips(true);
		return true;
	}

	private void removeAttacksOnShips(){
		for(Ship ship : ships){
			if(!ship.isSunk()) {
				for (Square s : ship.getOccupiedSquares()) {
					attacks.removeIf(r -> r.getLocation().equals(s));
				}
			}
		}
	}

	private void addAttacksOnShips(boolean afterMove){
		for(Ship ship : ships) {
			if (!ship.isSunk()) {
				for (Square s : ship.getOccupiedSquares()) {
					if(s.isHit() && afterMove){
						Result hit = new Result(s);
						hit.setResult(AtackStatus.HIT);
						attacks.add(hit);
					}
					if(s.isCaptains() && afterMove){
						if(ship.getArmour() == 1 && !ship.getKind().equals("MINESWEEPER")){
							Result crit = new Result(s);
							crit.setResult(AtackStatus.CRITICAL);
							attacks.add(crit);
						}
					}
					if(!afterMove){
						Square missS = new Square(s.getRow(), s.getColumn());
						Result miss = new Result(missS);
						attacks.add(miss);
						miss.setResult(AtackStatus.MISS);
					}
				}
			}
		}
	}

	List<Ship> getShips() {
		return ships;
	}
	int getAttacksSize() { return attacks.size(); } //for the tests
}
