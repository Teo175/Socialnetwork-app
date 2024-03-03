package com.example.demo.controller;

import com.example.demo.controller.alert.FriendRequestActionAlert;
import com.example.demo.controller.alert.MessageActionAlert;
import com.example.demo.controller.alert.UserActionsAlert;
import com.example.demo.domain.*;
import com.example.demo.repository.RepoPage.Page;
import com.example.demo.repository.RepoPage.Pageable;
import com.example.demo.service.FriendRequestService;
import com.example.demo.service.MessageService;
import com.example.demo.service.PrietenieService;
import com.example.demo.service.UtilizatorService;
import com.example.demo.utils.events.*;
import com.example.demo.utils.observer.Observer;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import com.example.demo.utils.observer.Observer;
import com.example.demo.utils.observer.Observable;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class AddController implements Observer<UserTaskChangeEvent>  {
    private Long id_user;

    private Long from_id = 0l;
    private Long to_id = 0l;
    private int pageSize;
    private int currentFriendPage = 0;
    private int totalNumberOfFriends = 0;

    private int currentFriendRequestsPage = 0;
    private int totalNumberOfFriendRequests = 0;

    private int currentMessagePage = 0;
    private int totalNumberOfMessages = 0;


    private int currentLM = 0;
    private int totalNumberOfLM = 0;

    private FriendRequestService friend_request_service;
    private UtilizatorService utilizator_service;
    private PrietenieService prietenie_service;

    private MessageService message_service;
    ObservableList<Utilizator> model_friends = FXCollections.observableArrayList();

    ObservableList<Utilizator> model_requests = FXCollections.observableArrayList();
    private ObservableList<Message> model_message = FXCollections.observableArrayList();

    ObservableList<Utilizator> model_friend_list = FXCollections.observableArrayList();

    @FXML
    ListView<Message> listMessages;
    @FXML
    public TableView<Utilizator> table_friends;

    @FXML
    public TableView<Utilizator> table_friends_requests;

    @FXML
    public TableView<Utilizator> table_list_friends;

    @FXML
    public TableColumn<Utilizator, String> table_column_FN;
    @FXML
    public TableColumn<Utilizator, String> table_column_LN;
    @FXML
    public TableColumn<Utilizator, String> table_column_U;

    @FXML
    public TableColumn<Utilizator, String> table_column_FN2;
    @FXML
    public TableColumn<Utilizator, String> table_column_LN2;

    @FXML
    public TableColumn<Utilizator, String> table_column_U2;

    @FXML
    public TableColumn<Utilizator, String> table_column_FN3;
    @FXML
    public TableColumn<Utilizator, String> table_column_LN3;
    @FXML
    public TableColumn<Utilizator, String> table_column_U3;

    @FXML
    public TextField username;


    @FXML
    public TextField message;


    @FXML
    public TextField fn_account;

    @FXML TextField ln_account;

    @FXML
    public Button add;

    @FXML
    public Button send;

    @FXML
    public Button exit1;

    @FXML
    public Button exit2;

    @FXML
    public Button nextButtonFriends;

    @FXML
    public Button previousButtonFriends;

    @FXML
    public Button nextButtonFriendRequest;

    @FXML
    public Button previousButtonFriendRequest;

    @FXML
    public Button nextButtonMessage;

    @FXML
    public Button previousButtonMessage;

    @FXML
    public Button previousButtonLM;

    @FXML
    public Button nextButtonLM;



    public void setService(int sizepg,Long id, FriendRequestService friend_request_service, UtilizatorService utilizator_service, PrietenieService prietenie_service, MessageService message_service){
        this.id_user = id;
        this.friend_request_service = friend_request_service;
        this.utilizator_service = utilizator_service;
        this.prietenie_service = prietenie_service;
        this.message_service = message_service;
        pageSize=sizepg;
        prietenie_service.addObserver(this);
        friend_request_service.addObserver(this);
        message_service.addObserver(this);
        initializeTableFriends();
        initializeTableFriendRequests();
        initializeTableFriend_List();
        account();

    }

    public void update(UserTaskChangeEvent taskChangeEvent) {
        initializeTableFriends();
        initializeTableFriendRequests();
       // initializeTableFriend_List();
        handleSelect();
    }


    public void initializeTableFriends(){
        /*List<Long> friends = utilizator_service.get_friends(id_user);
        Iterable<Utilizator> allFriendsUser = friends.stream()
                .map(x -> {return utilizator_service.getEntityById(x).get();})
                .collect(Collectors.toList());

        List<Utilizator> allFriend = StreamSupport.stream(allFriendsUser.spliterator(), false).toList();
        model_friends.setAll(allFriend);*/

        Page<Long> page = utilizator_service.get_friends(id_user, new Pageable(currentFriendPage, pageSize));

        List<Utilizator> allFriendsUser = new ArrayList<>();
        page.getElem().forEach(x -> allFriendsUser.add(utilizator_service.getEntityById(x).get()));



        int maxPage = (int) Math.ceil((double) page.getNr_elems() / pageSize ) - 1;
        if(currentFriendPage > maxPage) {
            currentFriendPage = maxPage;
            page = utilizator_service.get_friends(id_user, new Pageable(currentFriendPage, pageSize));
        }

        model_friends.setAll(allFriendsUser);
        totalNumberOfFriends = page.getNr_elems();

        previousButtonFriends.setDisable(currentFriendPage == 0);
        nextButtonFriends.setDisable((currentFriendPage + 1) * pageSize >= totalNumberOfFriends);
    }

    public void initializeTableFriend_List(){
        /*List<Long> friends = utilizator_service.get_friends(id_user);
        Iterable<Utilizator> allFriendsUser = friends.stream()
                .map(x -> {return utilizator_service.getEntityById(x).get();})
                .collect(Collectors.toList());

        List<Utilizator> allFriend = StreamSupport.stream(allFriendsUser.spliterator(), false).toList();
        model_friends.setAll(allFriend);*/

        Page<Long> page = utilizator_service.get_friends(id_user, new Pageable(currentLM, pageSize));

        List<Utilizator> allFriendsUser = new ArrayList<>();
        page.getElem().forEach(x -> allFriendsUser.add(utilizator_service.getEntityById(x).get()));



        int maxPage = (int) Math.ceil((double) page.getNr_elems() / pageSize ) - 1;
        if(currentLM > maxPage) {
            currentLM = maxPage;
            page = utilizator_service.get_friends(id_user, new Pageable(currentLM, pageSize));
        }

        model_friend_list.setAll(allFriendsUser);
        totalNumberOfLM = page.getNr_elems();

        previousButtonLM.setDisable(currentLM == 0);
        nextButtonLM.setDisable((currentLM + 1) * pageSize >= totalNumberOfLM);
    }
    public void initializeTableFriendRequests(){
        /*
        List<Long> friendRequests = friend_request_service.getFriendRequestIds(id_user);
        Iterable<Utilizator> allFriendRequestsUser = friendRequests.stream()
                .map(x -> {return utilizator_service.getEntityById(x).get();})
                .collect(Collectors.toList());

        List<Utilizator> allFriendRequests = StreamSupport.stream(allFriendRequestsUser.spliterator(), false).toList();
        model_requests.setAll(allFriendRequests);
         */

        Page<Long> page = friend_request_service.getFriendRequestIds(id_user, new Pageable(currentFriendRequestsPage, pageSize));

        List<Utilizator> allRequests = new ArrayList<>();
        page.getElem().forEach(x -> allRequests.add(utilizator_service.getEntityById(x).get()));

        int maxPage = (int) Math.ceil((double) page.getNr_elems() / pageSize ) - 1;
        if(currentFriendRequestsPage > maxPage) {
            currentFriendRequestsPage = maxPage;
            page = friend_request_service.getFriendRequestIds(id_user, new Pageable(currentFriendRequestsPage, pageSize));
        }

        model_requests.setAll(allRequests);
        totalNumberOfFriendRequests = page.getNr_elems();

        previousButtonFriendRequest.setDisable(currentFriendRequestsPage == 0);
        nextButtonFriendRequest.setDisable((currentFriendRequestsPage+1)*pageSize >= totalNumberOfFriendRequests);
    }

    @FXML
    public void initialize() {
        table_column_FN.setCellValueFactory(new PropertyValueFactory<Utilizator, String>("firstName"));
        table_column_LN.setCellValueFactory(new PropertyValueFactory<Utilizator, String>("lastName"));
        table_column_U.setCellValueFactory(new PropertyValueFactory<Utilizator, String>("username"));
        table_friends.setItems(model_friends);

        table_column_FN2.setCellValueFactory(new PropertyValueFactory<Utilizator, String>("firstName"));
        table_column_LN2.setCellValueFactory(new PropertyValueFactory<Utilizator, String>("lastName"));
        table_column_U2.setCellValueFactory(new PropertyValueFactory<Utilizator, String>("username"));
        table_friends_requests.setItems(model_requests);

        table_column_FN3.setCellValueFactory(new PropertyValueFactory<Utilizator, String>("firstName"));
        table_column_LN3.setCellValueFactory(new PropertyValueFactory<Utilizator, String>("lastName"));
        table_column_U3.setCellValueFactory(new PropertyValueFactory<Utilizator, String>("username"));
        table_list_friends.setItems(model_friend_list);

        table_list_friends.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

    }
    public void account ()
    {
         Optional<Utilizator> u = utilizator_service.getEntityById(id_user);
         fn_account.setText(u.get().getFirstName());
         ln_account.setText(u.get().getLastName());
         fn_account.setEditable(false);
         ln_account.setEditable(false);
    }
    public void handleAccept(){
        Utilizator utilizator = table_friends_requests.getSelectionModel().getSelectedItem();
        Tuple<Long, Long> ids = new Tuple<>(utilizator.getId(), id_user);

        FriendRequest friendRequest = friend_request_service.getEntityById(ids).get();
        friendRequest.setStatus(FriendRequestStatus.APPROVED);
        friend_request_service.update(friendRequest);

        Prietenie prietenie = new Prietenie(LocalDateTime.now());
        prietenie.setId(ids);
        prietenie_service.add(prietenie);
        friend_request_service.notifyObservers(new FriendRequestTaskChangeEvent(ChangeEventType.ADD, friendRequest));
        update(new PrietenieTaskChangeEvent(ChangeEventType.ADD, prietenie));
    }

    public void handleDelete(){
        Utilizator utilizator = table_friends_requests.getSelectionModel().getSelectedItem();
        Tuple<Long, Long> ids = new Tuple<>(utilizator.getId(), id_user);

        FriendRequest friendRequest = friend_request_service.getEntityById(ids).get();
        friendRequest.setStatus(FriendRequestStatus.REJECTED);
        friend_request_service.update(friendRequest);
        friend_request_service.notifyObservers(new FriendRequestTaskChangeEvent(ChangeEventType.ADD, friendRequest));

        update(new FriendRequestTaskChangeEvent(ChangeEventType.DELETE, friendRequest));
    }

    public void handle_add_friend(){
        String usrname = username.getText();

        if(usrname == null )
        {
            UserActionsAlert.showMessage(null,Alert.AlertType.ERROR,"Error","Eroare! Trebuie sa completezi campul Username!");
            return;
        }
        Optional<Utilizator> utilizator = utilizator_service.findUserByUsername(usrname);
        if(utilizator.isEmpty()) {
            FriendRequestActionAlert.showMessage(null, Alert.AlertType.ERROR, "Error", "Eroare! Utilizatorul introdus nu exista!");
            return;
        }

        FriendRequestStatus friendRequestStatus = FriendRequestStatus.PENDING;
        FriendRequest friendRequest = new FriendRequest(friendRequestStatus);
        friendRequest.setId(new Tuple<>(id_user, utilizator.get().getId()));

        try {
            Optional<FriendRequest> sentRequest = friend_request_service.add(friendRequest);
            if(sentRequest.isPresent())
                FriendRequestActionAlert.showMessage(null, Alert.AlertType.ERROR, "Error", "Exista deja o cerere catre utilizatorul dat!");
            else {
                FriendRequestActionAlert.showMessage(null, Alert.AlertType.INFORMATION, "Succes!", "Cererea a fost trimisa cu succes!");
                friend_request_service.notifyObservers(new FriendRequestTaskChangeEvent(ChangeEventType.ADD, friendRequest));
                Platform.runLater(()->update(new FriendRequestTaskChangeEvent(ChangeEventType.ADD, friendRequest)));
            }
        }
        catch (Exception e){
            FriendRequestActionAlert.showMessage(null, Alert.AlertType.ERROR, "Error", e.getMessage());}

        username.clear();
    }

    private void loadListOfMessages(Long userIdFrom, Long userIdTo) {
        /*listMessages.getItems().clear();
        model_message.clear();
        for (Message msg : message_service.getMessagesBetweenTwoUsers(userIdFrom, userIdTo)) {
            model_message.add(msg);
        }

        listMessages.setItems(model_message);*/

        listMessages.getItems().clear();
        model_message.clear();

        Page<Message> page = message_service.getAllBetweenUsers(new Pageable(currentMessagePage, pageSize), userIdFrom, userIdTo);

        int maxPage = (int) Math.ceil((double) page.getNr_elems() / pageSize ) - 1;
        if(currentMessagePage > maxPage) {
            currentMessagePage = maxPage;
            page = message_service.getAllBetweenUsers(new Pageable(currentMessagePage, pageSize), userIdFrom, userIdTo);
        }

        page.getElem().forEach(x -> model_message.add(x));
        listMessages.setItems(model_message);
        totalNumberOfMessages = page.getNr_elems();

        previousButtonMessage.setDisable(currentMessagePage == 0);
        nextButtonMessage.setDisable((currentMessagePage + 1) * pageSize >= totalNumberOfMessages);
    }
    public void handleSend(){
        List<Utilizator> prieteni = table_list_friends.getSelectionModel().getSelectedItems();


        if(prieteni.isEmpty()){
            MessageActionAlert.showMessage(null, Alert.AlertType.ERROR, "Error", "Trebuie selectat un email!");
            return;
        }
        String text = message.getText();
        if(text.isEmpty()){
            MessageActionAlert.showMessage(null, Alert.AlertType.ERROR, "Error", "Mesajul nu poate sa fie gol!");
            return;
        }

        List<Utilizator> utilizatori = prieteni.stream()
                .map(x -> {return utilizator_service.findUserByName(x.getFirstName(),x.getLastName()).get();})
                .toList();
        utilizatori.forEach(x ->
        { message_service.addMessage(id_user, x.getId(), text); });


        if(utilizatori.size() == 1) {
                loadListOfMessages(id_user, utilizatori.get(0).getId());
                message_service.notifyObservers(new MessageTaskChangeEvent(ChangeEventType.ADD, null));
                Platform.runLater(() -> update(new MessageTaskChangeEvent(ChangeEventType.ADD, null)));

        }

        message.clear();
    }

    public void handleSelect(){
        List<Utilizator> utilizators = table_list_friends.getSelectionModel().getSelectedItems();
        if(utilizators.size() == 1) {
            Utilizator utilizator = utilizator_service.findUserByName(utilizators.get(0).getFirstName(),utilizators.get(0).getLastName()).get();
            from_id = id_user;
            to_id = utilizator.getId();
            loadListOfMessages(id_user, utilizator.getId());
        }
        else {
            listMessages.getItems().clear();
            model_message.clear();
        }
    }
    public void handle_exit1(){
        Node src = exit1;
        Stage stage = (Stage) src.getScene().getWindow();

        stage.close();
    }
    public void handle_exit2(){
        Node src = exit2;
        Stage stage = (Stage) src.getScene().getWindow();

        stage.close();
    }

    public void handleNextButtonFriends(){
        currentFriendPage++;
        initializeTableFriends();
    }

    public void handlePreviousButtonFriends(){
        currentFriendPage--;
        initializeTableFriends();
    }

    public void handleNextButtonFriendRequest(){
        currentFriendRequestsPage++;
        initializeTableFriendRequests();
    }

    public void handlePreviousButtonFriendRequest(){
        currentFriendRequestsPage--;
        initializeTableFriendRequests();
    }

    public void handleNextButtonMessage(){
        if(from_id != 0 && to_id != 0) {
            currentMessagePage++;
            loadListOfMessages(from_id, to_id);
        }
    }

    public void handlePreviousButtonMessage(){
        if(from_id != 0 && to_id != 0) {
            currentMessagePage--;
            loadListOfMessages(from_id, to_id);
        }
    }

    public void handleNextButtonLM(ActionEvent actionEvent) {
        currentLM++;
        initializeTableFriend_List();
    }

    public void handlePreviousButtonLM(ActionEvent actionEvent) {
        currentLM--;
        initializeTableFriend_List();
    }
}