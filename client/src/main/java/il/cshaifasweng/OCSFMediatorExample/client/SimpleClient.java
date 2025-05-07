package il.cshaifasweng.OCSFMediatorExample.client;

import org.greenrobot.eventbus.EventBus;

import il.cshaifasweng.OCSFMediatorExample.client.ocsf.AbstractClient;
import il.cshaifasweng.OCSFMediatorExample.entities.Warning;

public class SimpleClient extends AbstractClient {
	
	private static SimpleClient client = null;
	private int playerNum;

	private class Coords { // row, col pair
		public int row;
		public int col;
		public Coords(int row, int col) {
			this.row = row;
			this.col = col;
		}
	}
	private SimpleClient(String host, int port) {
		super(host, port);
	}

	@Override
	protected void handleMessageFromServer(Object msg) { // for handling server messages
		if (msg.getClass().equals(Warning.class)) {
			EventBus.getDefault().post(new WarningEvent((Warning) msg));
		}
		// add other server message options after making those classes public
		else if (msg.getClass().equals(String.class)) {
			String message = msg.toString();
			if (message.startsWith("draw")) {
				endGamedraw(); // end game with draw ui
			}
		}
		else if (msg.getClass().equals(Integer.class)) { // get player numbers from servers
			this.playerNum = (Integer) msg;
		}
	}
	
	public static SimpleClient getClient() {
		if (client == null) {
			client = new SimpleClient("localhost", 3000);
		}
		return client;
	}

	private Coords playTurn(int[][] Board) { // update your board and get player input for turn, return coords
		// show updated board ui
		// pick tile again and return its coordinates, do not update selected tile
		return new Coords(0,0);
	}

	private Coords playAgain(int[][] Board,int row,int col) {
		// tell to play again (because of invalid move) and return coords
		return new Coords(0,0);
	}

	private void updateBoard(int[][] Board) {
		// update board ui after own turn (after approval from server)
	}

	private void endGameWin(int[][] Board, int playerNum) {
		// end game in win ui
	}

	private void endGamedraw() {
		// end game in draw ui
	}

}
