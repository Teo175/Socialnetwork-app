package com.example.demo;

import com.example.demo.controller.AddController;
import com.example.demo.controller.EditController;
import com.example.demo.controller.LoginController;
import com.example.demo.repository.RepoDB.PrietenieRepoDB;
import com.example.demo.repository.RepoDB.UserRepoDB;
import com.example.demo.service.PrietenieService;
import com.example.demo.service.UtilizatorService;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class AddApplication extends Application {

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(AddApplication.class.getResource("add-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 600, 400);
        stage.setTitle("FRIENDS");
        stage.setScene(scene);
        stage.show();
    }


}

