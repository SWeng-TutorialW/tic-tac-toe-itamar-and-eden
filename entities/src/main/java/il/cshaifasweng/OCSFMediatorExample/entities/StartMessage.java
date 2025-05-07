package il.cshaifasweng.OCSFMediatorExample.entities;

import java.io.Serializable;

public class StartMessage implements Serializable {
    public int[][] board;
    public int playerNum;
    public StartMessage(int[][] board, int playerNum) {
        this.board = board;
        this.playerNum = playerNum;
    }
}
