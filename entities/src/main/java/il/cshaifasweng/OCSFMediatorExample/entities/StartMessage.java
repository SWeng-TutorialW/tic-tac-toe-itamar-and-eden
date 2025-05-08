package il.cshaifasweng.OCSFMediatorExample.entities;

import java.io.Serializable;

public class StartMessage implements Serializable {
    public int[][] board;
    public int startingPlayerNum;

    public StartMessage(int[][] board, int startingPlayerNum) {
        this.board = board;
        this.startingPlayerNum = startingPlayerNum;
    }
}
