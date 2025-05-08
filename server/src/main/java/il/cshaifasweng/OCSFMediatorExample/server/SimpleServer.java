package il.cshaifasweng.OCSFMediatorExample.server;

import il.cshaifasweng.OCSFMediatorExample.entities.*;
import il.cshaifasweng.OCSFMediatorExample.server.ocsf.AbstractServer;
import il.cshaifasweng.OCSFMediatorExample.server.ocsf.ConnectionToClient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import il.cshaifasweng.OCSFMediatorExample.server.ocsf.SubscribedClient;

public class SimpleServer extends AbstractServer {
	private static ArrayList<SubscribedClient> SubscribersList = new ArrayList<>();
	private static int[][] board = new int[3][3];
	private int playerCnt = 0;
	private char player1sign;
	private char player2sign;

	public SimpleServer(int port) {
		super(port);
		Random rand = new Random();
		if (rand.nextBoolean()) {
			player1sign = 'X';
			player2sign = 'O';
		}
		else {
			player1sign = 'O';
			player2sign = 'X';
		}
	}

	public static void main(String[] args) throws IOException {
		SimpleServer server = new SimpleServer(3000);
		server.listen();
	}

	private static int[][] copy_board() {
		int[][] copy = new int[3][3];
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				copy[i][j] = SimpleServer.board[i][j];
			}
		}
		return copy;
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
						AllocMessage allocMessage = new AllocMessage(1,player1sign);
						client.sendToClient(allocMessage);
						this.playerCnt++;
					}
					else if (this.playerCnt == 1) { // start game if two players are in
						this.playerCnt++;
						AllocMessage allocMessage = new AllocMessage(2,player2sign);
						client.sendToClient(allocMessage);
						int startingPlayerNum = (player1sign == 'X' ? 1 : 2);
						StartMessage startMessage = new StartMessage(SimpleServer.copy_board(),startingPlayerNum);
						this.sendToAllClients(startMessage);
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
				int turnResult = this.playTurn(row, col, playerNum);
				if (turnResult == 0) { // if draw let both clients know
					DrawMessage serverMessage = new DrawMessage(SimpleServer.copy_board());
					this.sendToAllClients(serverMessage);
				}
				else if (turnResult == 1) { // if win let both clients know the winner
					WinMessage serverMessage = new WinMessage(SimpleServer.copy_board(),playerNum);
					this.sendToAllClients(serverMessage);
				}
				else { // let client know move was accepted and second client that he can play
					ServerMessage serverMessage = new ServerMessage(SimpleServer.copy_board(), playerNum);
					this.sendToAllClients(serverMessage);
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
	public void sendToAllClients(Object message) {
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
			if (SimpleServer.board[i][0] == playerNum && SimpleServer.board[i][1] == playerNum && SimpleServer.board[i][2] == playerNum) {
				return true;
			}
		}

		for (int i = 0; i < 3; i++) {
			if (SimpleServer.board[0][i] == playerNum && SimpleServer.board[1][i] == playerNum && SimpleServer.board[2][i] == playerNum) {
				return true;
			}
		}

		if (SimpleServer.board[0][0] == playerNum && SimpleServer.board[1][1] == playerNum && SimpleServer.board[2][2] == playerNum) {
			return true;
		}

		if (SimpleServer.board[0][2] == playerNum && SimpleServer.board[1][1] == playerNum && SimpleServer.board[2][0] == playerNum) {
			return true;
		}

		return false;
	}

	private boolean isFull() { // check if board is full - thus game ends in draw
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				if (SimpleServer.board[i][j] == 0) {
					return false;
				}
			}
		}

		return true;
	}

	private int playTurn(int row,int col,int playerNum) throws InvalidTileException { // play current client turn
		if (row < 0 || row >= SimpleServer.board.length) {
			throw new InvalidTileException("Player " + playerNum + " tried to place out of bounds: (row = " + row + ").",playerNum, row, col);
		}
		if (col < 0 || col >= SimpleServer.board.length) {
			throw new InvalidTileException("Player " + playerNum + " tried to place out of bounds: (col = " + col + ").",playerNum, row, col);
		}
		if (SimpleServer.board[row][col] != 0) // if tile isn't empty, ask to replace
		{
			throw new InvalidTileException("Player " + playerNum + " tried to place on occupied tile: " + row + "," + col + ".",playerNum,row,col);
		}
		SimpleServer.board[row][col] = playerNum;
		boolean isWin = this.IsWin(playerNum);
		if (isWin) { // if client won, return 1, if caused a draw, return 0, if game continues, return -1
			return 1;
		}
		boolean isFull = this.isFull();
		if (isFull) {
			return 0;
		}
		return -1;
	}

}
