package cs361.battleships.models;

public class Result {

	AtackStatus occured;

	public Result(){
		occured = null;
	}

	public Result(AtackStatus occured){
		this.occured = occured;
	}

	public AtackStatus getResult() {
		return occured;
	}

	public void setResult(AtackStatus result) {
		//TODO implement
	}

	public Ship getShip() {
		//TODO implement
		return null;
	}

	public void setShip(Ship ship) {
		//TODO implement
	}

	public Square getLocation() {
		//TODO implement
		return null;
	}

	public void setLocation(Square square) {
		//TODO implement
	}
}
