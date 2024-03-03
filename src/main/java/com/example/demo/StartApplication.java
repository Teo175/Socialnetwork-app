package com.example.demo;

import com.example.demo.controller.EditController;
import com.example.demo.controller.LoginController;
import com.example.demo.domain.Utilizator;
import com.example.demo.repository.RepoDB.MessageRepoDB;
import com.example.demo.repository.RepoDB.PrietenieRepoDB;
import com.example.demo.repository.RepoDB.RepoFriendRequest;
import com.example.demo.repository.RepoDB.UserRepoDB;
import com.example.demo.service.FriendRequestService;
import com.example.demo.service.MessageService;
import com.example.demo.service.PrietenieService;
import com.example.demo.service.UtilizatorService;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;

public class StartApplication extends Application {



    @Override
    public void start(Stage stage) throws IOException {
       UserRepoDB userRepoDB = new UserRepoDB("jdbc:postgresql://localhost:5432/socialnetwork","postgres","dinozaur123");
       UtilizatorService utilizatorService = new UtilizatorService(userRepoDB);
       PrietenieRepoDB prieteniiRepoDB = new PrietenieRepoDB("jdbc:postgresql://localhost:5432/socialnetwork","postgres","dinozaur123");
        PrietenieService prietenieService = new PrietenieService(prieteniiRepoDB,userRepoDB);
        RepoFriendRequest friendrequesrepo = new RepoFriendRequest("jdbc:postgresql://localhost:5432/socialnetwork","postgres","dinozaur123");
        FriendRequestService friendRequestService = new FriendRequestService(friendrequesrepo,userRepoDB,prieteniiRepoDB);
        MessageRepoDB msg = new MessageRepoDB("jdbc:postgresql://localhost:5432/socialnetwork","postgres","dinozaur123",userRepoDB);
        MessageService msg_srv = new MessageService(msg,userRepoDB);

        FXMLLoader fxmlLoader = new FXMLLoader(StartApplication.class.getResource("login-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 600, 400);
        stage.setTitle("LOGIN");
        stage.setScene(scene);
        LoginController controller = fxmlLoader.getController();
        controller.setService( friendRequestService,utilizatorService,prietenieService,msg_srv);
        stage.show();

    }

    public static void main(String[] args) {
        launch();
    }
}