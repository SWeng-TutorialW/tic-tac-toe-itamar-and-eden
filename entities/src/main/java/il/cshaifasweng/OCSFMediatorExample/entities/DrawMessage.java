package il.cshaifasweng.OCSFMediatorExample.entities;

public class DrawMessage { // message to send to winning client
    public int[][] board;

    public DrawMessage(int[][] board) {
        this.board = board;
    }
}
