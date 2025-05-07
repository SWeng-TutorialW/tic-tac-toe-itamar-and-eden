package il.cshaifasweng.OCSFMediatorExample.entities;

public class WinMessage { // message to send to winning client
    public int playerNum;
    public int[][] board;

    public WinMessage(int[][] board,int playerNum) {
        this.board = board;
        this.playerNum = playerNum;
    }
}
