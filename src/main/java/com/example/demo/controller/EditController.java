package com.example.demo.controller;

import com.example.demo.controller.alert.UserActionsAlert;
import com.example.demo.domain.Utilizator;
import com.example.demo.repository.RepoPage.Page;
import com.example.demo.repository.RepoPage.Pageable;
import com.example.demo.service.UtilizatorService;
import com.example.demo.utils.events.ChangeEventType;
import com.example.demo.utils.events.UserTaskChangeEvent;
import com.example.demo.utils.observer.Observer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import com.example.demo.utils.observer.Observer;
import com.example.demo.utils.observer.Observable;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class EditController implements Observer<UserTaskChangeEvent>  {
    private UtilizatorService service;
    ObservableList<Utilizator> model = FXCollections.observableArrayList();

    private int pageSize;
    private int currentUsersPage = 0;
    private int totalNumberOfUsers = 0;



    @FXML
    public TableView<Utilizator> tableView;
    @FXML
    public TableColumn<Utilizator, Long> tableColumnID;
    @FXML
    public TableColumn<Utilizator, String> tableColumnFN;
    @FXML
    public TableColumn<Utilizator, String> tableColumnLN;

    @FXML
    public TableColumn<Utilizator, String> tableColumnU;
    @FXML
    public TableColumn<Utilizator, String> tableColumnP;





    @FXML
    public TextField TextID;

    @FXML
    public TextField TextFN;

    @FXML
    public TextField TextLN;

    @FXML
    public TextField TextU;

    @FXML
    public TextField TextP;

    @FXML
    public Button ExitButton;
    @FXML
    public Button nextButton;
    @FXML
    public Button previousButton;

    public void setUserTaskService(int pg_size, UtilizatorService utilizatorService){

        this.service = utilizatorService;
        service.addObserver(this);
        pageSize=pg_size;
        initializeTableDataUser();

    }

    public void initializeTableDataUser(){
        /*Iterable<Utilizator> allUsers = service.getAll();
        List<Utilizator> allUsersList = StreamSupport.stream(allUsers.spliterator(), false).toList();
        model.setAll(allUsersList);*/

        Page<Utilizator> page = service.findAll(new Pageable(currentUsersPage, pageSize));

        int maxPage = (int) Math.ceil((double) page.getNr_elems() / pageSize ) - 1;
        if(currentUsersPage > maxPage) {
            currentUsersPage = maxPage;
            page = service.findAll(new Pageable(currentUsersPage, pageSize));
        }

        model.setAll(StreamSupport.stream(page.getElem().spliterator(),
                false).collect(Collectors.toList()));
        totalNumberOfUsers = page.getNr_elems();

        previousButton.setDisable(currentUsersPage == 0);
        nextButton.setDisable((currentUsersPage + 1) * pageSize >= totalNumberOfUsers);
    }
    public void update(UserTaskChangeEvent taskChangeEvent) {
        initializeTableDataUser();
    }

    @FXML
    public void initialize() {
        tableColumnID.setCellValueFactory(new PropertyValueFactory<Utilizator, Long>("id"));
        tableColumnFN.setCellValueFactory(new PropertyValueFactory<Utilizator, String>("firstName"));
        tableColumnLN.setCellValueFactory(new PropertyValueFactory<Utilizator, String>("lastName"));
        tableColumnU.setCellValueFactory(new PropertyValueFactory<Utilizator, String>("username"));
        tableColumnP.setCellValueFactory(new PropertyValueFactory<Utilizator, String>("password"));
        tableView.setItems(model);
    }

    public void handleAddUser(){

        String first_name = TextFN.getText();
        String last_name = TextLN.getText();
        String username = TextU.getText();
        String password = TextP.getText();
        try {
            Long id = Long.parseLong(TextID.getText());
            Utilizator user = new Utilizator(first_name, last_name, username, password);
            user.setId(id);
            try {
                Optional<Utilizator> addedUser = service.add(user);
                if (addedUser.isPresent())
                    UserActionsAlert.showMessage(null, Alert.AlertType.ERROR, "Error", "Eroare! Exista deja un utilizator cu ID-ul dat!");
                else{
                    update(new UserTaskChangeEvent(ChangeEventType.ADD, user));
                }
            }
            catch (Exception e){
                UserActionsAlert.showMessage(null, Alert.AlertType.ERROR, "Error", e.getMessage());
            }
        }
        catch (Exception e){
            UserActionsAlert.showMessage(null, Alert.AlertType.ERROR, "Error", "ID invalid! ID-ul trebuie sa fie un numar!");
        }

        TextID.clear();
        TextFN.clear();
        TextLN.clear();
        TextU.clear();
        TextP.clear();
    }

    public void handleDeleteUser(){

        try {
            Long id = Long.parseLong(TextID.getText());
            try {
                Optional<Utilizator> deletedUser = service.delete(id);
                if (deletedUser.isEmpty()) {
                    UserActionsAlert.showMessage(null, Alert.AlertType.ERROR, "Error", "Eroare! Nu exista utilizator cu ID-ul dat!");
                }
                    else update(new UserTaskChangeEvent(ChangeEventType.DELETE, deletedUser.get()));
            }
            catch (Exception e){
                UserActionsAlert.showMessage(null, Alert.AlertType.ERROR, "Error", e.getMessage());
            }
        }
        catch (Exception e){
            UserActionsAlert.showMessage(null, Alert.AlertType.ERROR, "Error", "ID invalid! ID-ul trebuie sa fie un numar!");
        }

        TextID.clear();
        TextFN.clear();
        TextLN.clear();
        TextU.clear();
        TextP.clear();
    }

    public void handleUpdateUser(){

        String first_name = TextFN.getText();
        String last_name = TextLN.getText();
        String username = TextU.getText();
        String password = TextP.getText();
        try {
            Long id = Long.parseLong(TextID.getText());
            Utilizator user = new Utilizator(first_name,last_name,username,password);
            user.setId(id);
            try {
                Optional<Utilizator> updatedUser = service.update(user);
                if (updatedUser.isEmpty())
                    UserActionsAlert.showMessage(null, Alert.AlertType.ERROR, "Error", "Eroare! Nu exista utilizatorul dat!");
                else update(new UserTaskChangeEvent(ChangeEventType.UPDATE, user));
            }
            catch (Exception e){
                UserActionsAlert.showMessage(null, Alert.AlertType.ERROR, "Error", e.getMessage());
            }
        }
        catch (Exception e){
            UserActionsAlert.showMessage(null, Alert.AlertType.ERROR, "Error", "ID invalid! ID-ul trebuie sa fie un numar!");
        }

        TextID.clear();
        TextFN.clear();
        TextLN.clear();
        TextU.clear();
        TextP.clear();
    }

    public void handleFindUser()  {
        try {
            Long id = Long.parseLong(TextID.getText());
            Optional<Utilizator> foundUser = service.getEntityById(id);
        if (foundUser.isEmpty()) {
            UserActionsAlert.showMessage(null, Alert.AlertType.ERROR, "Error", "Eroare! Nu exista utilizator cu ID-ul dat!");
            TextFN.clear();
            TextLN.clear();
            TextU.clear();
            TextP.clear();
        }
        else{
            TextFN.setText(foundUser.get().getFirstName());
            TextLN.setText(foundUser.get().getLastName());
            TextU.setText(foundUser.get().getUsername());
            TextP.setText(foundUser.get().getPassword());
        }
        }
        catch (Exception e){
        UserActionsAlert.showMessage(null, Alert.AlertType.ERROR, "Error", "ID invalid! ID-ul trebuie sa fie un numar!");
        TextFN.clear();
        TextLN.clear();
        TextU.clear();
        TextP.clear();
         }
    }

    public void handleSelectUser(){
        Utilizator utilizator = (Utilizator) tableView.getSelectionModel().getSelectedItem();

        TextID.setText(utilizator.getId().toString());
        TextFN.setText(utilizator.getFirstName());
        TextLN.setText(utilizator.getLastName());
        TextU.setText(utilizator.getUsername());
        TextP.setText(utilizator.getPassword());
    }

    public void handleExitButton(){
        Node src = ExitButton;
        Stage stage = (Stage) src.getScene().getWindow();
        stage.close();
    }

    public void handleNext(){
        currentUsersPage++;
        initializeTableDataUser();
    }

    public void handlePrevious(){
        currentUsersPage--;
        initializeTableDataUser();
    }

}