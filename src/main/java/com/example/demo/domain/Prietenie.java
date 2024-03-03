package com.example.demo.domain;

import java.time.LocalDateTime;

public class Prietenie extends Entity<Tuple<Long,Long>> {

    // private LocalDateTime date;
    private LocalDateTime friendsFrom;
    public Prietenie(LocalDateTime f_F) {friendsFrom = f_F;};

    public LocalDateTime getDate() {
        return friendsFrom;
    }

    public void setDate(LocalDateTime date) {
        this.friendsFrom = date;
    }

}