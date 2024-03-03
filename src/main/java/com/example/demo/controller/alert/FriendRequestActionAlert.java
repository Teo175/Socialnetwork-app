package com.example.demo.controller.alert;

import javafx.scene.control.Alert;
import javafx.stage.Stage;

public class FriendRequestActionAlert {
    public static void showMessage(Stage owner, Alert.AlertType alertType, String title, String description){
        Alert message = new Alert(alertType);
        message.initOwner(owner);
        message.setTitle(title);
        message.setContentText(description);
        message.showAndWait();
    }

    public static void showErrorMessage(Stage owner, String text){
        Alert message=new Alert(Alert.AlertType.ERROR);
        message.initOwner(owner);
        message.setTitle("Error Message");
        message.setContentText(text);
        message.showAndWait();
    }
}
