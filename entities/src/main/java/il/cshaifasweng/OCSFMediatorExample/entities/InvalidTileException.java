package il.cshaifasweng.OCSFMediatorExample.entities;

public class InvalidTileException extends Exception { // exceptions for invalid moves
    public int row;
    public int col;
    public int playerNum;

    public InvalidTileException(String message,int playerNum,int row,int col) {
        super(message);
        this.row = row;
        this.col = col;
        this.playerNum = playerNum;
    }
}
