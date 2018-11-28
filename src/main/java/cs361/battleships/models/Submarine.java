package cs361.battleships.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Submarine extends Ship{
    @JsonProperty private boolean isSubmerged;

    public Submarine(){
        super("BATTLESHIP");
        kind = "SUBMARINE";
        size = 5;
    }

    //since java methods are virtual
    public void place(char col, int row, boolean isVertical, boolean submerged){
        isSubmerged = submerged;
        if(isVertical) {
            for(int i = 0; i < size - 1; i++){
                occupiedSquares.add(new Square(row+i, col ));
                if(i == 0){
                    occupiedSquares.get(i).setCaptains(true);
                }
            }
            occupiedSquares.add(new Square(row + 1, (char) (col - 1)));
        }
        else{
            for(int i = 0; i < size - 1; i++){
                occupiedSquares.add(new Square(row, (char) (col + i)));
                if(i == 3){
                    occupiedSquares.get(i).setCaptains(true);
                }
            }
            occupiedSquares.add(new Square(row - 1, (char) (col + 2)));
        }
    }
    //overlaps as in at the same square and at the same DEPTH
    public boolean overlaps(Ship other){
        //if we had two submarines, you would need to change this function a bit
        if(isSubmerged)
            return false;

        //if on surface, treat as regular ship
        return super.overlaps(other);
    }


}
