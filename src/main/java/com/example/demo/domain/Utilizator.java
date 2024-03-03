package com.example.demo.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Utilizator extends Entity<Long> {
    private String firstName;
    private String lastName;

    private String username;
    private String password;
    private ArrayList<Utilizator> friends;

    public Utilizator(String firstName, String lastName, String username, String password) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.password = password;
        friends = new ArrayList<>();
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public ArrayList<Utilizator> getFriends() {
        return friends;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void addFriend(Utilizator e) throws IllegalArgumentException{
        if(friends.contains(e))
            throw new IllegalArgumentException("Eroare! Utilizatorii sunt deja prieteni!");
        friends.add(e);
    }

    public void removeFriend(Utilizator e) throws IllegalArgumentException{
        if(!friends.contains(e))
            throw new IllegalArgumentException("Eroare! Utilizatorii nu sunt prieteni!");
        friends.remove(e);
    }

    @Override
    public String toString() {
        return "Utilizator{" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Utilizator)) return false;
        Utilizator that = (Utilizator) o;
        return getFirstName().equals(that.getFirstName()) &&
                getLastName().equals(that.getLastName()) &&
                getFriends().equals(that.getFriends());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getFirstName(), getLastName());
    }
    public int getNrFriends(){
        return friends.size();
    }
}
