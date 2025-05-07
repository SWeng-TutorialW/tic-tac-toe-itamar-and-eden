package il.cshaifasweng.OCSFMediatorExample.client;

import il.cshaifasweng.OCSFMediatorExample.entities.*;
import org.greenrobot.eventbus.EventBus;

import il.cshaifasweng.OCSFMediatorExample.client.ocsf.AbstractClient;

import java.io.IOException;
import java.util.Arrays;
import java.util.Scanner;
public class SimpleClient extends AbstractClient {
	
	private static SimpleClient client = null;
	private int playerNum;

	private SimpleClient(String host, int port) {
		super(host, port);
	}

	public static void main(String[] args) throws IOException {
		SimpleClient client = new SimpleClient("localhost", 3000);
		EventBus.getDefault().register(client);;
		client.openConnection();
	}

	@Override
	protected void handleMessageFromServer(Object msg) { // for handling server messages
		System.out.println("Hello!");
		if (msg.getClass().equals(Warning.class)) {
			EventBus.getDefault().post(new WarningEvent((Warning) msg));
		}
		else if (msg.getClass().equals(WinMessage.class)) {
			WinMessage winMessage = (WinMessage) msg;
				this.endGameWin(winMessage.board,winMessage.playerNum);
		}
		else if (msg.getClass().equals(DrawMessage.class)) {
			DrawMessage drawMessage = (DrawMessage) msg;
				this.endGamedraw(drawMessage.board); // end game with draw ui
		}
		else if (msg.getClass().equals(InvalidTileException.class)) {
			InvalidTileException e = (InvalidTileException) msg;
			Coords tile = this.playAgain(e);
			ClientMessage message = new ClientMessage(tile.row,tile.col,this.playerNum);
			try{
				this.sendToServer(message);
			}
			catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		else if (msg.getClass().equals(ServerMessage.class)) {
			System.out.println("Server message!!");
			ServerMessage serverMessage = (ServerMessage) msg;
			int[][] board = serverMessage.board;
			int playerNum = serverMessage.playerNum;
			if (playerNum == this.playerNum) {
				this.updateBoard(board);
			}
			else {
				Coords tile = this.playTurn(board);
				ClientMessage message = new ClientMessage(tile.row,tile.col,this.playerNum);
				try{
					this.sendToServer(message);
				}
				catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}
		else if (msg.getClass().equals(Integer.class)) { // get player numbers from servers
			this.playerNum = (Integer) msg;
			System.out.println("Number!!!");
		}
		else if (msg.getClass().equals(StartMessage.class)) {
			System.out.println("Start!!!");
			StartMessage startMessage = (StartMessage) msg;
			this.playerNum = startMessage.playerNum;
			Coords tile = this.playTurn(startMessage.board);
			ClientMessage message = new ClientMessage(tile.row,tile.col,this.playerNum);
			try{
				this.sendToServer(message);
			}
			catch (IOException e1) {
				e1.printStackTrace();
			}
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
		System.out.println(Arrays.toString(Board));
		Scanner scanner = new Scanner(System.in);
		System.out.print("Your turn");
		System.out.print("Enter row: ");
		int row = scanner.nextInt();
		System.out.print("Enter col: ");
		int col = scanner.nextInt();
		// pick tile again and return its coordinates, do not update selected tile
		return new Coords(row,col);
	}

	private Coords playAgain(InvalidTileException e) {
		// tell to play again (because of invalid move) and return coords
		System.out.println("Play again, invalid move: " + e.toString());
		Scanner scanner = new Scanner(System.in);
		System.out.print("Enter row: ");
		int row = scanner.nextInt();
		System.out.print("Enter col: ");
		int col = scanner.nextInt();
		return new Coords(row,col);
	}

	private void updateBoard(int[][] Board) {
		// update board ui after own turn (after approval from server)
		System.out.println(Arrays.toString(Board));
	}

	private void endGameWin(int[][] Board, int playerNum) {
		// end game in win ui
		System.out.println(Arrays.toString(Board));
		if (this.playerNum == playerNum) {
			System.out.println("You win!");
		}
		else {
			System.out.println("You lost!");
		}
	}

	private void endGamedraw(int[][] Board) {
		// end game in draw ui
		System.out.println(Arrays.toString(Board));
		System.out.println("Draw!");
	}

}
