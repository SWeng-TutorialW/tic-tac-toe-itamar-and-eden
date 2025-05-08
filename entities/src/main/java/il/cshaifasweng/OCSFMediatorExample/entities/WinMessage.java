package il.cshaifasweng.OCSFMediatorExample.entities;

import java.io.Serializable;

public class WinMessage implements Serializable { // message to send to winning client
    public int playerNum;
    public int[][] board;

    public WinMessage(int[][] board,int playerNum) {
        this.board = board;
        this.playerNum = playerNum;
    }
}
