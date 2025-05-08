package il.cshaifasweng.OCSFMediatorExample.entities;

import java.io.Serializable;

public class ServerMessage implements Serializable { // used to send server messages to clients
    public int[][] board;
    public int playerNum;

    public ServerMessage(int[][] board,int playerNum) {
        this.board = board;
        this.playerNum = playerNum;
    }
}
