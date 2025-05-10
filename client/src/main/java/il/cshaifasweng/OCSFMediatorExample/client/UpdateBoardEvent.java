package il.cshaifasweng.OCSFMediatorExample.client;

public class UpdateBoardEvent {
	private int[][] board;
	private boolean myTurn;

	public int[][] getBoard() {
		return board;
	}
	public boolean getMyTurn() {
		return myTurn;
	}

	public UpdateBoardEvent(int[][] board, boolean myTurn) {
		this.board = board;
		this.myTurn = myTurn;
	}
}
