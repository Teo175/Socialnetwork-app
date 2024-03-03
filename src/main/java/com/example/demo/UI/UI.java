package com.example.demo.UI;

import com.example.demo.domain.Prietenie;
import com.example.demo.domain.Tuple;
import com.example.demo.domain.Utilizator;
import com.example.demo.repository.RepoDB.PrietenieRepoDB;
import com.example.demo.repository.RepoDB.UserRepoDB;
import com.example.demo.service.PrietenieService;
import com.example.demo.service.UtilizatorService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.Scanner;

public class UI implements UI_Interface{

    UserRepoDB userFileRepo = new UserRepoDB("jdbc:postgresql://localhost:5432/socialnetwork","postgres","dinozaur123");
    PrietenieRepoDB prietenieFileRepository = new PrietenieRepoDB("jdbc:postgresql://localhost:5432/socialnetwork","postgres","dinozaur123");
    //UtilizatorFileRepository userFileRepo = new UtilizatorFileRepository("C:\\Users\\peter\\AN2-SEM1\\MAP\\lab\\lab6_gradle\\src\\main\\java\\src\\repository\\RepoFile\\Users.txt", new UtilizatorValidator());
    //PrietenieFileRepository prietenieFileRepository = new PrietenieFileRepository("C:\\Users\\peter\\AN2-SEM1\\MAP\\lab\\lab6_gradle\\src\\main\\java\\src\\repository\\RepoFile\\Prietenii.txt", new PrietenieValidator());
    UtilizatorService utilizatorService = new UtilizatorService(userFileRepo);
    PrietenieService prietenieService = new PrietenieService(prietenieFileRepository, userFileRepo);

    public static UI instance;
    public static UI getInstance(){
        if(instance == null) instance = new UI();
        return instance;
    }
    private UI(){}

    @Override
    public void run(){
        while(true){
            meniu();
            Scanner in = new Scanner(System.in);

            int option = in.nextInt();
            if(option == 1) addUser();
            else if(option == 2) deleteUser();
            else if(option == 3) printAllUsers();
            else if(option == 4) createFriendship();
            else if(option == 5) deleteFriendship();
            else if(option == 6) printAllPrietenii();
            else if(option == 7) System.out.println("In retea sunt " + utilizatorService.nrComunitati() + " comunitati distincte!");
            else if(option == 8) System.out.println("Cea mai sociabila retea este formata din: " + utilizatorService.comunitateaSociabila());
            else if (option == 9) user_n();
            else if (option == 10) friends_from();
            else if (option == 0){
                break;
            }
        }
    }

    private void meniu(){
        System.out.println("##      MENIU      ##");
        System.out.println("1 -> Adauga Utilizator");
        System.out.println("2 -> Sterge Utilizator");
        System.out.println("3 -> Afiseaza Utilizatori\n");
        System.out.println("4 -> Creeaza prietenie");
        System.out.println("5 -> Sterge prietenie");
        System.out.println("6 -> Afiseaza prietenii\n");
        System.out.println("7 -> Numar comunitati");
        System.out.println("8 -> Cea mai sociabila comunitate");
        System.out.println("9 -> Userii cu cel putin n prieteni\n");
        System.out.println("10 -> Prietenii dintr-o anumita luna\n");
        System.out.println("0 -> Exit");
    }

    private void user_n()
    {
        System.out.println("N: ");
        Scanner in = new Scanner(System.in);
        Long N = in.nextLong();

        Iterable<Utilizator> allUsers = utilizatorService.get_n(N);
        System.out.println("Userii cu cel putin N prieteni:\n");
        allUsers.forEach(System.out::println);

    }

    private void addUser() {
        Scanner in = new Scanner(System.in);
        System.out.println("ID: ");
        Long id = in.nextLong();
        System.out.println("Prenume: ");
        String fName = in.next();
        System.out.println("Nume: ");
        String lName = in.next();
        System.out.println("Username: ");
        String UName = in.next();
        System.out.println("Password: ");
        String Password = in.next();
        Utilizator user = new Utilizator(fName, lName,UName,Password);
        user.setId(id);

        try {

            Optional<Utilizator> addedUser = utilizatorService.add(user);

            if (addedUser.isEmpty()) {
                System.out.println("Utilizatorul " + fName + " " + lName + " a fost salvat cu succes!");
            } else {
                System.out.println("Exista deja un utilizator cu ID-ul " + id);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private void deleteUser() {
        Scanner in = new Scanner(System.in);
        System.out.println("ID: ");
        Long id = in.nextLong();
        try {

            Optional<Utilizator> deletedUser = utilizatorService.delete(id);

            if (deletedUser.isPresent()) {
                System.out.println("Utilizatorul " + deletedUser.get().getFirstName() + " " + deletedUser.get().getLastName() + " a fost sters!");
            } else {
                System.out.println("Nu exista niciun utilizatorul cu ID-ul " + id);
            }

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private void printAllUsers(){
        Iterable<Utilizator> allUsers = utilizatorService.getAll();
        allUsers.forEach(x-> System.out.println("Utilizator: " + x.getFirstName() + " "+ x.getLastName()));
    }

    private void printAllPrietenii(){
        Iterable<Prietenie> allPrieteni = prietenieService.getAll();
        allPrieteni.forEach(x ->
                System.out.println(
                        utilizatorService.getEntityById(x.getId().getLeft()).get().getFirstName()  + " " +
                                utilizatorService.getEntityById(x.getId().getLeft()).get().getLastName()  +
                                " este prieten cu " +
                                utilizatorService.getEntityById(x.getId().getRight()).get().getFirstName() + " " +
                                utilizatorService.getEntityById(x.getId().getRight()).get().getLastName() + " imprietenit la data de: " +
                                x.getDate()
                )
        );
    }

    private void createFriendship(){
        Scanner in = new Scanner(System.in);
        System.out.println("ID user 1: ");
        Long id1 = in.nextLong();
        System.out.println("ID user 2: ");
        Long id2 = in.nextLong();
        in.nextLine();
        System.out.println("Data: yyyy-MM-dd HH:mm:ss");
        String date = in.nextLine();
        // System.out.println(date);
        try {
            DateTimeFormatter f = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime date_time = LocalDateTime.parse(date,f);
            Prietenie newFriendship = new Prietenie(date_time);
            newFriendship.setId(new Tuple(id1, id2));
            Optional<Prietenie> prietenieAdd = prietenieService.add(newFriendship);

            if (prietenieAdd.isPresent()) {
                System.out.println("Prietenie exista deja sau nu s-a putut crea.");
            } else {
                System.out.println("Prietenie creata cu succes!");
            }
        }
        catch (Exception e) { System.out.println(e.getMessage()); }
    }

    private void deleteFriendship(){
        Scanner in = new Scanner(System.in);
        System.out.println("ID utilizator 1: ");
        Long id1 = in.nextLong();
        System.out.println("ID utilizator 2:");
        Long id2 = in.nextLong();
        try{
            Optional<Prietenie> deletedPrietenie = prietenieService.delete(new Tuple<>(id1, id2));

            if(deletedPrietenie.isPresent())
                System.out.println("Prietenia a fost stearsa cu succes!");
            else
            System.out.println("Eroare! Utilizatorii nu sunt prieteni");


        }
        catch(Exception e){
            System.out.println(e.getMessage());
        }
    }

    private void friends_from(){
        Scanner in = new Scanner(System.in);
        System.out.println("ID utilizatorului: ");
        Long id = in.nextLong();
        System.out.println("Numarul lunii: ");
        Integer luna = in.nextInt();

        if(luna < 0 || luna > 12){
            System.out.println("Numarul lunii este invalid!");
            return;
        }

        try{
            Optional<Utilizator> utilizator = utilizatorService.getEntityById(id);
            if(utilizator.isPresent()) {
                System.out.println("Utilizatorii cu care s-a imprietenit in luna data sunt: ");
                prietenieService.friendsMadeInACertainMonth(utilizator.get(), luna).forEach(System.out::println);
            }
            else System.out.println("Eroare! Utilizatorul nu exista!");
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
        }
    }

}
