package com.example.demo.domain;

import java.io.Serializable;
import java.util.Objects;

public class Entity<ID> implements Serializable {
    //clasa generica-baza pentru alte clase, nu se defineste un tip exact de date
    //tip generic ID-pot avea mai multe tipuri (integer,string,...)

    // private static final long serialVersionUID = 7331115341259248461L;
    protected ID id;
    public ID getId() {
        return id;
    }
    public void setId(ID id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Entity)) return false;
        Entity<?> entity = (Entity<?>) o; //pun obiectul o intr-un obiect de tip Entity
        return getId().equals(entity.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }

    @Override
    public String toString() {
        return "Entity{" +
                "id=" + id +
                '}';
    }
}
