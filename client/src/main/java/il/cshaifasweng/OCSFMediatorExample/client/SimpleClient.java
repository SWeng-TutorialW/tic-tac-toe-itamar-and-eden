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
	private char playerSign;
	private char otherSign;

	private SimpleClient(String host, int port) {
		super(host, port);
	}

	public static void main(String[] args) throws IOException {
		SimpleClient client = new SimpleClient("localhost", 3000);
		EventBus.getDefault().register(client);
		client.openConnection();
	}

	private void print_board(int[][] board) {
		for (int i = 0; i < board.length; i++) {
			for (int j = 0; j < board[i].length; j++) {
				if (board[i][j] == 0) {
					System.out.print("? ");
				}
				else if (board[i][j] == this.playerNum) {
					System.out.print(this.playerSign + " ");
				}
				else {
					System.out.print(this.otherSign + " ");
				}
			}
			System.out.println();
		}
	}

	@Override
	protected void handleMessageFromServer(Object msg) { // for handling server messages
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
		}
		else if (msg.getClass().equals(AllocMessage.class)) {
			AllocMessage allocMessage = (AllocMessage) msg;
			this.playerNum = allocMessage.numAlloc;
			this.playerSign = allocMessage.playerSign;
			this.otherSign = (this.playerSign == 'X' ? 'O' : 'X');
		}
		else if (msg.getClass().equals(StartMessage.class)) {
			StartMessage startMessage = (StartMessage) msg;
			int startingPlayerNum = startMessage.startingPlayerNum;
			if (startingPlayerNum == this.playerNum) {
				Coords tile = this.playTurn(startMessage.board);
				ClientMessage message = new ClientMessage(tile.row,tile.col,this.playerNum);
				try{
					this.sendToServer(message);
				}
				catch (IOException e1) {
					e1.printStackTrace();
				}
			}
			else {
				this.updateBoard(startMessage.board);
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
		this.print_board(Board);
		Scanner scanner = new Scanner(System.in);
		System.out.println("Your turn!");
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
		this.print_board(Board);
		System.out.println("Waiting for opponent's move...");
	}

	private void endGameWin(int[][] Board, int playerNum) {
		// end game in win ui
		this.print_board(Board);
		if (this.playerNum == playerNum) {
			System.out.println("You win!");
		}
		else {
			System.out.println("You lose!");
		}
	}

	private void endGamedraw(int[][] Board) {
		// end game in draw ui
		this.print_board(Board);
		System.out.println("Draw!");
	}

}
