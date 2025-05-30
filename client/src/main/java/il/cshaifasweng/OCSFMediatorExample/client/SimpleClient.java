package il.cshaifasweng.OCSFMediatorExample.client;

import il.cshaifasweng.OCSFMediatorExample.entities.*;
import org.greenrobot.eventbus.EventBus;

import il.cshaifasweng.OCSFMediatorExample.client.ocsf.AbstractClient;
import org.greenrobot.eventbus.Subscribe;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class SimpleClient extends AbstractClient {
	
	private static SimpleClient client = null;

	public static int playerId;


	private SimpleClient(String host, int port) {
		super(host, port);
		EventBus.getDefault().register(this);
	}

	@Override
	protected void handleMessageFromServer(Object msg) { // for handling server messages
		if (msg.getClass().equals(Warning.class)) {
			EventBus.getDefault().post(new WarningEvent((Warning) msg));
		}
		else if (msg.getClass().equals(WinMessage.class)) {
			WinMessage winMessage = (WinMessage) msg;
			boolean myTurn = winMessage.playerNum != playerId;
//			EventBus.getDefault().post(new UpdateBoardEvent(winMessage.board, myTurn));
		}
		else if (msg.getClass().equals(DrawMessage.class)) {
			DrawMessage drawMessage = (DrawMessage) msg;
//			EventBus.getDefault().post(new UpdateBoardEvent(drawMessage.board, false));
		}
		else if (msg.getClass().equals(InvalidTileException.class)) { // game turn was denied
			InvalidTileException e = (InvalidTileException) msg;
			System.out.println(e.getMessage());
		} else if (msg.getClass().equals(ServerMessage.class)) { // game turn was accepted
			ServerMessage serverMessage = (ServerMessage) msg;
			int[][] board = serverMessage.board;
			boolean myTurn = serverMessage.playerNum != playerId;
			EventBus.getDefault().post(new UpdateBoardEvent(board, myTurn));
		}
		else if (msg.getClass().equals(Integer.class)) { // get player numbers from servers
			playerId = (Integer) msg;
		}
		else if (msg.getClass().equals(AllocMessage.class)) {
			AllocMessage allocMessage = (AllocMessage) msg;
			boolean mySign = allocMessage.playerSign == 'X';
			playerId = allocMessage.numAlloc;
			EventBus.getDefault().post(new AllocationEvent(mySign));
		}
		else if (msg.getClass().equals(StartMessage.class)) { // game started
			StartMessage startMessage = (StartMessage) msg;
			App.switchToGame();
			int startingPlayerNum = startMessage.startingPlayerNum;
			boolean myTurn = startingPlayerNum == playerId;
			EventBus.getDefault().post(new UpdateBoardEvent(startMessage.board, myTurn));
		}
	}

	// return client to connect with
	public static SimpleClient getClient() {
		if (client == null) {
			client = new SimpleClient("localhost", 3000);
		}
		return client;
	}

	// send to server a turn request

	@Subscribe
	public void onGameMove(GameMove gameMove) {
		int[] coords = gameMove.getCoords();
		int row = coords[0];
		int col = coords[1];
		ClientMessage message = new ClientMessage(row, col, playerId);
		try{
			this.sendToServer(message);
		}
		catch (IOException e1) {
			e1.printStackTrace();
		}
	}

}
