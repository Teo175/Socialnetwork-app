package com.example.demo.repository.RepoInMemory;

import com.example.demo.domain.Entity;
import com.example.demo.domain.validators.Validator;
import com.example.demo.repository.Repository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;

public class InMemoryRepository<ID, E extends Entity<ID>> implements Repository<ID,E> {
    private Validator<E> validator;
    Map<ID, E> entities;

    public InMemoryRepository(Validator<E> validator) {
        this.validator = validator;
        entities = new HashMap<ID, E>();
    }

    @Override
    public Optional<E> findOne(ID id) {
        if (id == null)
            throw new IllegalArgumentException("Error! Id must be not null!\n");
        // return entities.get(id);
        E entity = entities.get(id);
        return Optional.ofNullable(entity); //daca am null returnez optional.empty altfel se pune valoarea
    }

    @Override
    public Iterable<E> findAll() {
        return entities.values();
    }

    @Override
    public Optional<E> save(E entity) {


        if (entity == null)
            throw new IllegalArgumentException("Error! Entity must be not null!");
        validator.validate(entity);
        if (entities.get(entity.getId()) != null) {
            return Optional.of(entity);//daca  exista intorc identitatea
        } else {
            entities.put(entity.getId(), entity);
            return Optional.empty();//daca nu exista returnez empy="am luat-o"
        }
    }

    @Override
    public Optional<E> delete(ID id) {
        if (id == null)
            throw new IllegalArgumentException("Invalid ID!");

        // Define a predicate to check if the entity with the given ID exists
        Predicate<ID> entityExists = entityId -> entities.containsKey(entityId);

        if (entityExists.test(id)) {
            E entity = entities.get(id);
            entities.remove(id);
            return Optional.of(entity);
        }

        return Optional.empty();
    }

    @Override
    public Optional<E> update(E entity) {

        if (entity == null)
            throw new IllegalArgumentException("entity must be not null!");
        validator.validate(entity);

        entities.put(entity.getId(), entity);


        ID entityId = entity.getId();
        E existingEntity = entities.get(entityId);

        if (existingEntity != null) {
            // Entity with the given ID exists; update it
            entities.put(entityId, entity);
            return Optional.empty();
        }
        return Optional.of(entity);
    }

}
