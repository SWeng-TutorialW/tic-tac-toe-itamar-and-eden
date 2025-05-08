package il.cshaifasweng.OCSFMediatorExample.entities;

import java.io.Serializable;

public class AllocMessage implements Serializable {
    public int numAlloc;
    public char playerSign;

    public AllocMessage(int numAlloc, char playerSign) {
        this.numAlloc = numAlloc;
        this.playerSign = playerSign;
    }
}
