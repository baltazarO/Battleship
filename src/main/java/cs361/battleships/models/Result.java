package cs361.battleships.models;

public class Result {

	private AtackStatus occured;
	private Ship myShip;
	private Square myLocation;

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
		occured = result;
	}

	public Ship getShip() {
		return myShip;
	}

	public void setShip(Ship ship) {
		myShip = ship;
	}

	public Square getLocation() {
		return myLocation;
	}

	public void setLocation(Square square) {
		myLocation = square;
	}
}
