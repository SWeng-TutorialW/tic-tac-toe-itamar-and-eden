package il.cshaifasweng.OCSFMediatorExample.entities;

import java.io.Serializable;

public class ClientMessage implements Serializable { // used to get client tile and number
    public int row;
    public int col;
    public int playerNum;

    public ClientMessage(int row,int col,int playerNum) {
        this.row = row;
        this.col = col;
        this.playerNum = playerNum;
    }
}
