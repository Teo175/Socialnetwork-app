package com.example.demo.domain;

import javafx.scene.control.TextField;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class Message extends Entity<Long> {
    private Utilizator from;
    private List<Utilizator> to;
    private LocalDateTime date;
    private String message;
    private Message reply;

    public Message(Utilizator from, List<Utilizator> to, LocalDateTime date, String message) {
        this.from = from;
        this.to = to;
        this.date = date;
        this.message = message;
        this.reply = null;
    }

    public Message(Utilizator from, List<Utilizator> to, String message) {
        this.from = from;
        this.to = to;
        this.message = message;
        this.date = LocalDateTime.now();
    }


    public Utilizator getFrom() {
        return from;
    }

    public void setFrom(Utilizator from) {
        this.from = from;
    }

    public List<Utilizator> getTo() {
        return to;
    }

    public void setTo(List<Utilizator> to) {
        this.to = to;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Message getReply() {
        return reply;
    }

    public void setReply(Message reply) {
        this.reply = reply;
    }

    @Override
    public String toString() {
        return from.getFirstName() +
                ": " + message + "\nDATE:" +
                date;
    }
}