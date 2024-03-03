package com.example.demo.controller;

import com.example.demo.AddApplication;
import com.example.demo.UsersApplication;
import com.example.demo.controller.alert.LoginActionAlert;
import com.example.demo.domain.PasswordEncoder;
import com.example.demo.domain.Utilizator;
import com.example.demo.service.FriendRequestService;
import com.example.demo.service.MessageService;
import com.example.demo.service.PrietenieService;
import com.example.demo.service.UtilizatorService;
import com.example.demo.utils.events.UserTaskChangeEvent;
import com.example.demo.utils.observer.Observer;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.xml.stream.events.StartElement;
import java.io.IOException;
import java.util.Optional;

public class LoginController {

    private FriendRequestService friendRequestService;
    private UtilizatorService utilizatorService;
    private PrietenieService prietenieService;

    private MessageService messageService;

    @FXML
    private TextField username;
    @FXML
    private TextField id_password;
    @FXML
    private TextField size_page_field;

   // private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @FXML
   private Button login;

   @FXML
   private Button cancel;


    public void setService(FriendRequestService friendRequestService, UtilizatorService utilizatorService, PrietenieService prietenieService,MessageService messageService){
        this.friendRequestService=friendRequestService;
        this.utilizatorService = utilizatorService;
        this.prietenieService = prietenieService;
        this.messageService = messageService;
    }


    public void handle_cancel(){
        Node src = cancel;
        Stage stage = (Stage) src.getScene().getWindow();

        stage.close();
    }

    public void handle_login() {
        String usrname = username.getText();
        String password = id_password.getText();
        String size_pg = size_page_field.getText();
        if (size_pg.isEmpty()) {
            LoginActionAlert.showMessage(null, Alert.AlertType.ERROR, "Error", "Eroare! Dimensiunea nu poate sa fie nula!");
            return;
        }
        if (password.isEmpty()) {
            LoginActionAlert.showMessage(null, Alert.AlertType.ERROR, "Error", "Eroare! Parola  nu poate sa fie nula!");
            return;
        }
        if(usrname.isEmpty())
        {
            LoginActionAlert.showMessage(null, Alert.AlertType.ERROR, "Error", "Eroare! Username nu poate sa fie nul!");
            return;
        }
        try
        {
            long size_long = Long.parseLong(size_pg);
            int size_int =(int)size_long;
            if(password.equals("admin") && usrname.equals("admin"))
            {
                Stage stage = new Stage();
                FXMLLoader fxmlLoader = new FXMLLoader(AddApplication.class.getResource("socialnetwork.fxml"));
                try {
                    Scene scene = new Scene(fxmlLoader.load(), 600, 400);
                    stage.setScene(scene);

                    EditController addController = fxmlLoader.getController();
                    addController.setUserTaskService(size_int,utilizatorService);
                    stage.show();
                }
                catch(IOException e){
                    LoginActionAlert.showMessage(null, Alert.AlertType.ERROR, "Error", e.getMessage());
                }
                id_password.clear();
                username.clear();
                size_page_field.clear();
            }
            else {
                Optional<Utilizator> utilizator = utilizatorService.findUserByUsername(usrname);
                if(utilizator.isEmpty()) {
                    LoginActionAlert.showMessage(null, Alert.AlertType.ERROR, "Error", "Eroare! Username-ul introdus nu exista!");
                    username.clear();
                    id_password.clear();
                    return;
                }
               if (PasswordEncoder.checkPassword(password, utilizator.get().getPassword()))
               {
                        Long id_user = utilizator.get().getId();
                        Stage stage = new Stage();

                   FXMLLoader fxmlLoader = new FXMLLoader(AddApplication.class.getResource("add-view.fxml"));
                   try {
                            Scene scene = new Scene(fxmlLoader.load(), 600, 400);
                            stage.setScene(scene);

                            AddController addController = fxmlLoader.getController();
                            addController.setService(size_int,id_user, friendRequestService, utilizatorService, prietenieService,messageService);
                            stage.show();
                        }
                 catch (IOException e) {
                           LoginActionAlert.showMessage(null, Alert.AlertType.ERROR, "Error", e.getMessage());
                   }
                        username.clear();
                        id_password.clear();
                        return;
                    } else {
                        LoginActionAlert.showMessage(null, Alert.AlertType.ERROR, "Error", "Nu am gasit nici un utilizator cu aceasta parola!");
                    }
                    username.clear();
                    id_password.clear();

            }
        }catch (NumberFormatException err) {
            LoginActionAlert.showMessage(null, Alert.AlertType.ERROR, "Error", "Eroare! Dimensiunea trebuie sa contina doar cifre!");
        }

    }
}