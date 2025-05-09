package il.cshaifasweng.OCSFMediatorExample.client;

import il.cshaifasweng.OCSFMediatorExample.entities.*;
import org.greenrobot.eventbus.EventBus;

import il.cshaifasweng.OCSFMediatorExample.client.ocsf.AbstractClient;

import java.io.IOException;
import java.util.Arrays;
import java.util.Scanner;
public class SimpleClient extends AbstractClient {
	
	private static SimpleClient client = null;

	public static int playerId;


	private SimpleClient(String host, int port) {
		super(host, port);
	}

	@Override
	protected void handleMessageFromServer(Object msg) { // for handling server messages
		if (msg.getClass().equals(Warning.class)) {
			EventBus.getDefault().post(new WarningEvent((Warning) msg));
		}
		else if (msg.getClass().equals(WinMessage.class)) {
			WinMessage winMessage = (WinMessage) msg;
			GameController.endGame(winMessage.board);
		}
		else if (msg.getClass().equals(DrawMessage.class)) {
			DrawMessage drawMessage = (DrawMessage) msg;
			GameController.endGame(drawMessage.board);
		}
		else if (msg.getClass().equals(InvalidTileException.class)) { // game turn was denied
			InvalidTileException e = (InvalidTileException) msg;
			System.out.println("Move was invalid");
		} else if (msg.getClass().equals(ServerMessage.class)) { // game turn was accepted
			ServerMessage serverMessage = (ServerMessage) msg;
			int[][] board = serverMessage.board;
			int playerNum = serverMessage.playerNum;
			GameController.myTurn = playerNum != playerId;
			GameController.updateBoard(board);
		}
		else if (msg.getClass().equals(Integer.class)) { // get player numbers from servers
			playerId = (Integer) msg;
		}
		else if (msg.getClass().equals(AllocMessage.class)) {
			AllocMessage allocMessage = (AllocMessage) msg;
			playerId = allocMessage.numAlloc;
			GameController.mySign = allocMessage.playerSign == 'X';
		}
		else if (msg.getClass().equals(StartMessage.class)) { // game started
			StartMessage startMessage = (StartMessage) msg;
			System.out.println("Game started!");
			App.switchToGame();
			int startingPlayerNum = startMessage.startingPlayerNum;
			GameController.myTurn = startingPlayerNum == playerId;
			System.out.println("Player " + GameController.myTurn);
			GameController.updateBoard(startMessage.board);
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
	public void playTurn(int[] coords) {
		int row = coords[0];
		int col = coords[1];
		System.out.println("Playing turn " + row + ", " + col);
		ClientMessage message = new ClientMessage(row, col, playerId);
		try{
			this.sendToServer(message);
		}
		catch (IOException e1) {
			e1.printStackTrace();
		}
	}

}
