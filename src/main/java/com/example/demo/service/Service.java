package com.example.demo.service;


import com.example.demo.domain.Entity;
import com.example.demo.utils.events.UserTaskChangeEvent;

import java.util.Observer;
import java.util.Optional;

public interface Service<ID, E extends Entity<ID>> {

    Optional<E> add(E entity);

    Optional<E> delete(ID id);

    Optional<E> getEntityById(ID id);

    Iterable<E> getAll();


}
