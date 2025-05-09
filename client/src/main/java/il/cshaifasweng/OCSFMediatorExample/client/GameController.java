package il.cshaifasweng.OCSFMediatorExample.client;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.shape.Line;

public class GameController {
    public static boolean mySign; // X is true, O is false
    public static boolean myTurn = false;

    public static SimpleClient client;


    @FXML
    private static Button btn11, btn12, btn13, btn21, btn22, btn23, btn31, btn32, btn33;

    @FXML
    private Line win1, win2, win3, win4, win5, win6, win7, win8;

    @FXML
    private static Label turnText;

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
//        isPrimary = App.getPlayerId() == 1;
        win1.setManaged(false);
        win1.setVisible(false);
        win2.setManaged(false);
        win2.setVisible(false);
        win3.setManaged(false);
        win3.setVisible(false);
        win4.setManaged(false);
        win4.setVisible(false);
        win5.setManaged(false);
        win5.setVisible(false);
        win6.setManaged(false);
        win6.setVisible(false);
        win7.setManaged(false);
        win7.setVisible(false);
        win8.setManaged(false);
        win8.setVisible(false);
    }

    void buttonPressed(int btnNumber) {
        if (!myTurn) { return; }
        int btnIndex = btnNumber - 1;
        Button[] buttons = {btn11, btn12, btn13, btn21, btn22, btn23, btn31, btn32, btn33, btn33};
        Button pressedBtn = buttons[btnIndex];
        int[] coords = {(int)(btnIndex / 3), btnIndex % 3};
        GameController.playTurn(coords);
    }

    private static void playTurn(int[] coords) {
        client.playTurn(coords);
    }

    public static void endGame(int[][] board) {
        updateBoard(board);
    }

    public static void updateBoard(int[][] board) {
        System.out.println(myTurn);
        turnText.setText(myTurn ? "Your turn" : "Opponent's turn");

        System.out.println(board[0][0]);
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
}