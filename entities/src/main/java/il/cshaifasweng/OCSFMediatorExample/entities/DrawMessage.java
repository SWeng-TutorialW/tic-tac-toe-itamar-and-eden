package il.cshaifasweng.OCSFMediatorExample.entities;

import java.io.Serializable;

public class DrawMessage implements Serializable { // message to send to winning client
    public int[][] board;

    public DrawMessage(int[][] board) {
        this.board = board;
    }
}
