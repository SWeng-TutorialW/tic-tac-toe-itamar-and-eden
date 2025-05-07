package il.cshaifasweng.OCSFMediatorExample.server;

import il.cshaifasweng.OCSFMediatorExample.server.ocsf.AbstractServer;
import il.cshaifasweng.OCSFMediatorExample.server.ocsf.ConnectionToClient;

import java.io.IOException;
import java.util.ArrayList;

import il.cshaifasweng.OCSFMediatorExample.entities.Warning;
import il.cshaifasweng.OCSFMediatorExample.server.ocsf.SubscribedClient;

public class SimpleServer extends AbstractServer {
	private static ArrayList<SubscribedClient> SubscribersList = new ArrayList<>();
	private static int[][] board = new int[3][3];
	private int playerCnt = 0;

	private class InvalidTileException extends Exception { // exceptions for invalid moves
		public int row;
		public int col;
		public int playerNum;

		public InvalidTileException(String message,int playerNum,int row,int col) {
			super(message);
			this.row = row;
			this.col = col;
			this.playerNum = playerNum;
		}
	}
	public SimpleServer(int port) {
		super(port);
	}

	private class ClientMessage { // used to get client tile and number
		public int row;
		public int col;
		public int playerNum;

		public ClientMessage(int row,int col,int playerNum) {
			this.row = row;
			this.col = col;
			this.playerNum = playerNum;
		}
	}

	private class WinMessage { // message to send to winning client
		public int playerNum;

		public WinMessage(int playerNum) {
			this.playerNum = playerNum;
		}
	}
	
	private class ServerMessage { // used to send server messages to clients
		public int[][] board;
		public int playerNum;

		public ServerMessage(int[][] board,int playerNum) {
			this.board = board;
			this.playerNum = playerNum;
		}
	}

	@Override
	protected void handleMessageFromClient(Object msg, ConnectionToClient client) { // handle messages from clients
		if (msg.getClass().equals(String.class)) {
			String msgString = msg.toString();
			if (msgString.startsWith("#warning")) {
				Warning warning = new Warning("Warning from server!");
				try {
					client.sendToClient(warning);
					System.out.format("Sent warning to client %s\n", client.getInetAddress().getHostAddress());
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			else if(msgString.startsWith("add client")){
				SubscribedClient connection = new SubscribedClient(client);
				SubscribersList.add(connection);
				try {
					if (this.playerCnt == 0) {
						client.sendToClient(1);
						this.playerCnt++;
					}
					else {
						client.sendToClient(2);
					}
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}
			else if(msgString.startsWith("remove client")){
				if(!SubscribersList.isEmpty()){
					for(SubscribedClient subscribedClient: SubscribersList){
						if(subscribedClient.getClient().equals(client)){
							SubscribersList.remove(subscribedClient);
							playerCnt--;
							break;
						}
					}
				}
			}
		}
		else if(msg.getClass().equals(ClientMessage.class)) {
			ClientMessage message = (ClientMessage) msg;
			int row = message.row;
			int col = message.col;
			int playerNum = message.playerNum;
			try {
				int turnResult = playTurn(row, col, playerNum);
				if (turnResult == 0) { // if draw let both clients know
					String serverMessage = "draw";
					sendToAllClients(serverMessage);
				}
				else if (turnResult == 1) { // if win let both clients know the winner
					WinMessage serverMessage = new WinMessage(playerNum);
					sendToAllClients(serverMessage);
				}
				else { // let client know move was accepted and second client that he can play
					ServerMessage serverMessage = new ServerMessage(board, playerNum);
					sendToAllClients(serverMessage);
				}
			}
			catch (InvalidTileException e) { // if move was illegal, let playing client know the mistake and play again
				try {
					client.sendToClient(e);
				}
				catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}
	}
	public void sendToAllClients(String message) {
		try {
			for (SubscribedClient subscribedClient : SubscribersList) {
				subscribedClient.getClient().sendToClient(message);
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	private boolean IsWin(int playerNum) { // check if move causes the playing client to win
		for (int i = 0; i < 3; i++) {
			if (board[i][0] == playerNum && board[i][1] == playerNum && board[i][2] == playerNum) {
				return true;
			}
		}

		for (int i = 0; i < 3; i++) {
			if (board[0][i] == playerNum && board[1][i] == playerNum && board[2][i] == playerNum) {
				return true;
			}
		}

		if (board[0][0] == playerNum && board[1][1] == playerNum && board[2][2] == playerNum) {
			return true;
		}

		if (board[0][2] == playerNum && board[1][1] == playerNum && board[2][0] == playerNum) {
			return true;
		}

		return false;
	}

	private boolean isFull() { // check if board is full - thus game ends in draw
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				if (board[i][j] == 0) {
					return false;
				}
			}
		}

		return true;
	}

	private int playTurn(int row,int col,int playerNum) throws InvalidTileException { // play current client turn
		if (row < 0 || row >= board.length) {
			throw new InvalidTileException("Player " + playerNum + " tried to place out of bounds: (row = " + row + ").",playerNum, row, col);
		}
		if (col < 0 || col >= board.length) {
			throw new InvalidTileException("Player " + playerNum + " tried to place out of bounds: (col = " + col + ").",playerNum, row, col);
		}
		if (board[row][col] != 0) // if tile isn't empty, ask to replace
		{
			throw new InvalidTileException("Player " + playerNum + " tried to place on occupied tile: " + row + "," + col + ".",playerNum,row,col);
		}
		board[row][col] = playerNum;
		boolean isWin = IsWin(playerNum);
		if (isWin) { // if client won, return 1, if caused a draw, return 0, if game continues, return -1
			return 1;
		}
		boolean isFull = isFull();
		if (isFull) {
			return 0;
		}
		return -1;
	}

}
