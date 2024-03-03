package com.example.demo.repository;

import com.example.demo.domain.Entity;
import com.example.demo.repository.RepoPage.Page;
import com.example.demo.repository.RepoPage.Pageable;

public interface PagingRepository<ID, E extends Entity<ID>> extends Repository<ID, E>{
    Page<E> findAll(Pageable pageable);
}

