package com.example.demo;

import com.example.demo.UI.UI;
import com.example.demo.domain.Utilizator;
import com.example.demo.repository.RepoDB.PrietenieRepoDB;
import com.example.demo.repository.RepoDB.RepoFriendRequest;
import com.example.demo.repository.RepoDB.UserRepoDB;
import com.example.demo.service.FriendRequestService;
import com.example.demo.service.PrietenieService;
import com.example.demo.service.UtilizatorService;

import java.sql.SQLOutput;
import java.util.stream.Collectors;

public class Main {

    public static void main(String[] args) {

        //UserRepoDB u_repo = new UserRepoDB("jdbc:postgresql://localhost:5432/socialnetwork","postgres","dinozaur123");
        // System.out.println(u_repo.findOne(1l));
        // System.out.println(u_repo.findAll());
        UI ui = UI.getInstance();
        ui.run();


    }

}