package com.example.demo.service;

import com.example.demo.domain.Utilizator;
import com.example.demo.repository.RepoDB.UserRepoDB;
import com.example.demo.repository.RepoPage.Page;
import com.example.demo.repository.RepoPage.Pageable;
import com.example.demo.utils.events.UserTaskChangeEvent;
import com.example.demo.utils.observer.Observable;
import com.example.demo.utils.observer.Observer;
import javafx.scene.control.TextField;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class UtilizatorService implements Service<Long, Utilizator>, Observable<UserTaskChangeEvent> {

    private UserRepoDB repo;
    private List<Observer<UserTaskChangeEvent>> observers=new ArrayList<>();
    public UtilizatorService(UserRepoDB repo) {
        this.repo = repo;
    }

    @Override
    public Optional<Utilizator> add(Utilizator entity) {
        return repo.save(entity);
    }

    @Override
    public Optional<Utilizator> delete(Long id) {
        return repo.delete(id);
    }

    @Override
    public Iterable<Utilizator> getAll() {
        return repo.findAll();
    }



    @Override
    public Optional<Utilizator> getEntityById(Long id) {
        return repo.findOne(id);
    }


    private void DFS(Utilizator utilizator, Map<Utilizator, Integer> visited, int len) {
        visited.put(utilizator, len);
        List<Long> list = repo.getFriendsIds(utilizator.getId());
        list.forEach(userId -> {
            if (!visited.containsKey(repo.findOne(userId).get()) || visited.get(repo.findOne(userId).get()) == 0) {
                DFS(repo.findOne(userId).get(), visited, len);
            }
        });
    }


    public int nrComunitati() {

        Map<Utilizator, Integer> visited = new HashMap<>();
        AtomicInteger numar_comunitati = new AtomicInteger(0);

        getAll().forEach(user -> {
            if (!visited.containsKey(user) || visited.get(user) == 0) {
                numar_comunitati.incrementAndGet();
                DFS(user, visited, numar_comunitati.get());
            }
        });
        return numar_comunitati.get();
    }

    private int BFS(Utilizator utilizator, Map<Utilizator, Integer> visited) {
        int maxim = -1;
        Queue<Utilizator> coada = new LinkedList<>();
        coada.add(utilizator);
        visited.put(utilizator, 1);

        while (!coada.isEmpty()) {
            Utilizator nextUtilizator = coada.peek();
            coada.poll();
            for (Long userId : repo.getFriendsIds(nextUtilizator.getId())) {
                if (!visited.containsKey(repo.findOne(userId).get()) || visited.get(repo.findOne(userId).get()) == 0) {
                    int nxt_value = visited.get(nextUtilizator) + 1;

                    if (nxt_value > maxim) maxim = nxt_value;

                    visited.put(repo.findOne(userId).get(), nxt_value);
                    coada.add(repo.findOne(userId).get());
                }
            }
        }

        return maxim;
    }

    private void populeaza(Utilizator utilizator, List<Utilizator> list) {
        List<Long> friendsId = repo.getFriendsIds(utilizator.getId());
        friendsId.forEach(userId -> {
            if (!list.contains(repo.findOne(userId).get())) {
                list.add(repo.findOne(userId).get());
                populeaza(repo.findOne(userId).get(), list);
            }
        });
    }

    public List<Utilizator> comunitateaSociabila() {
        Map<Utilizator, Integer> visited = new HashMap<>();
        List<Utilizator> result = new ArrayList<>();
        int maxim = -1;

        for (Utilizator user : getAll())
            if (!visited.containsKey(user) || visited.get(user) == 0) {
                int lung = BFS(user, visited);
                if (lung > maxim) {
                    maxim = lung;
                    if (!result.isEmpty()) result.clear();
                    populeaza(user, result);
                }
            }

        return result;
    }

    public Optional<Utilizator> findUserByName(String fn,String ln)
    {
        return repo.findFirstByName(fn,ln);
    }

    public List<Long> get_friends(Long id)
    {
       return  repo.getFriendsIds(id);
    }

    public Page<Long> get_friends(Long id, Pageable pageable){
        return repo.getFriendsIds(id, pageable);
    }

    public Iterable<Utilizator> get_n(Long N) {
        return repo.getAllN(N);
    }


    public Optional<Utilizator> update(Utilizator u) {
        return repo.update(u);
    }


    @Override
    public void addObserver(Observer<UserTaskChangeEvent> e) {
        observers.add(e);
    }

    @Override
    public void removeObserver(Observer<UserTaskChangeEvent> e) {
        observers.remove(e);
    }

    @Override
    public void notifyObservers(UserTaskChangeEvent t) {
        observers.forEach(x->x.update(t));
    }

    public Page<Utilizator> findAll(Pageable pageable) { return repo.findAll(pageable);}

    public Optional<Utilizator> findUserByUsername(String username) {
        return repo.findFirstByUsername(username);
    }


}


