package il.cshaifasweng.OCSFMediatorExample.client;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import java.io.IOException;


public class FirstController {

    @FXML
    void initialize() {
        if (App.gameRunning) {
            Platform.runLater(() -> {
                try {
                    App.switchToGame();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }

    }


}
