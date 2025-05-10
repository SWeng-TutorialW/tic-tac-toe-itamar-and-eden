package il.cshaifasweng.OCSFMediatorExample.client;

import il.cshaifasweng.OCSFMediatorExample.entities.Warning;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.shape.Line;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class GameController {
    public static boolean mySign; // X is true, O is false
    public static boolean myTurn = false;

    public static SimpleClient client;

    public GameController() {
        // Register this controller as an EventBus listener
        EventBus.getDefault().register(this);
    }

    @FXML
    private Button btn11, btn12, btn13, btn21, btn22, btn23, btn31, btn32, btn33;

    @FXML
    private Line win1, win2, win3, win4, win5, win6, win7, win8;

    @FXML
    private Label turnLabel;

    @FXML
    void btn11Pressed(ActionEvent event) {
        buttonPressed(1);
    }
    @FXML
    void btn12Pressed(ActionEvent event) {
        buttonPressed(2);
    }
    @FXML
    void btn13Pressed(ActionEvent event) {
        buttonPressed(3);
    }
    @FXML
    void btn21Pressed(ActionEvent event) {
        buttonPressed(4);
    }
    @FXML
    void btn22Pressed(ActionEvent event) {
        buttonPressed(5);
    }
    @FXML
    void btn23Pressed(ActionEvent event) {
        buttonPressed(6);
    }
    @FXML
    void btn31Pressed(ActionEvent event) {
        buttonPressed(7);
    }
    @FXML
    void btn32Pressed(ActionEvent event) {
        buttonPressed(8);
    }
    @FXML
    void btn33Pressed(ActionEvent event) { buttonPressed(9); }

    @FXML
    public void initialize() {
        Line[] winLines = {win1, win2, win3, win4, win5, win6, win7, win8};
        for (Line winLine : winLines) {
            winLine.setManaged(false);
            winLine.setVisible(false);
        }
        turnLabel.setText("Get ready");
    }

    void buttonPressed(int btnNumber) {
        if (!myTurn) { return; }
        int btnIndex = btnNumber - 1;
        Button[] buttons = {btn11, btn12, btn13, btn21, btn22, btn23, btn31, btn32, btn33, btn33};
        Button pressedBtn = buttons[btnIndex];
        int[] coords = {(int)(btnIndex / 3), btnIndex % 3};
        EventBus.getDefault().post(new GameMove(coords));
    }


    void updateBoard(int[][] board) {
        Button[] buttons = {btn11, btn12, btn13, btn21, btn22, btn23, btn31, btn32, btn33, btn33};
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                int value = board[i][j];
                Button button = buttons[i*3 + j];
                if (value == 0) {
                    button.setText("");
                } else {
                    boolean isX = (value == SimpleClient.playerId) == mySign;
                    button.setText(isX ? "X" : "O");
                }
            }
        }

    }


    @Subscribe
    public void onUpdateBoard(UpdateBoardEvent event) {
        Platform.runLater(() -> {
            int[][] board = event.getBoard();
            updateBoard(board);
            myTurn = event.getMyTurn();
            int status = checkBoard(board);
            String turnLabelText;
            if (status == 0) {
                turnLabelText = myTurn ? "Your turn" : "Opponent's turn";
            } else if (status == -1) {
                turnLabelText = "DRAW!";
                myTurn = false;
            } else {
                Line[] winLines = {win1, win2, win3, win4, win5, win6, win7, win8};
                winLines[status-1].setVisible(true);
                turnLabelText = myTurn ? "YOU LOSE!" : "YOU WIN!";
                myTurn = false;
            }
            turnLabel.setText(turnLabelText);
        });

    }

    private int checkBoard(int[][] board) { // 0 normal, -1 draw, else win
        // check if someone won
        for (int i = 0; i < 3; i++) {
            if (board[i][0] == board[i][1] && board[i][1] == board[i][2] && board[i][0] != 0) {
                return 1+i;
            }
        }

        for (int i = 0; i < 3; i++) {
            if (board[0][i] == board[1][i] && board[1][i] == board[2][i] && board[0][i] != 0) {
                return 4+i;
            }
        }

        if (board[0][0] == board[1][1] && board[1][1] == board[2][2] && board[0][0] != 0) {
            return 7;
        }

        if (board[0][2] == board[1][1] && board[1][1] == board[2][0] && board[0][2] != 0) {
            return 8;
        }

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (board[i][j] == 0) {
                    return 0;
                }
            }
        }

        return -1;
    }
}