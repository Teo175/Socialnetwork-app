package com.example.demo.service;

import com.example.demo.domain.Prietenie;
import com.example.demo.domain.Tuple;
import com.example.demo.domain.Utilizator;
import com.example.demo.repository.RepoDB.PrietenieRepoDB;
import com.example.demo.repository.RepoDB.UserRepoDB;
import com.example.demo.utils.events.UserTaskChangeEvent;
import com.example.demo.utils.observer.Observable;
import com.example.demo.utils.observer.Observer;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class PrietenieService implements Service<Tuple<Long,Long>, Prietenie>, Observable<UserTaskChangeEvent> {
    PrietenieRepoDB repo;
    UserRepoDB repoUtilizatori;


    private List<Observer<UserTaskChangeEvent>> observers=new ArrayList<>();


    public PrietenieService(PrietenieRepoDB repo, UserRepoDB repoUtilizatori) {
        this.repo = repo;
        this.repoUtilizatori = repoUtilizatori;
        loadFriends();
    }

    private void loadFriends() {
        getAll().forEach(friendship -> {
            Optional<Utilizator> user1Optional = repoUtilizatori.findOne(friendship.getId().getLeft());
            Optional<Utilizator> user2Optional = repoUtilizatori.findOne(friendship.getId().getRight());

            user1Optional.ifPresent(user1 -> {
                user2Optional.ifPresent(user2 -> {
                    user1.addFriend(user2);
                    user2.addFriend(user1);
                });
            });
        });
    }


    @Override
    public Iterable<Prietenie> getAll() {
        return repo.findAll();
    }

    @Override
    public Optional<Prietenie> getEntityById(Tuple<Long, Long> longLongTuple) {

        return repo.findOne(longLongTuple);
    }

    @Override
    public Optional<Prietenie> add(Prietenie entity) {
        Long id1 = entity.getId().getLeft();
        Long id2 = entity.getId().getRight();

        Optional<Utilizator> user1Optional = repoUtilizatori.findOne(id1);
        Optional<Utilizator> user2Optional = repoUtilizatori.findOne(id2);

        if (user1Optional.isPresent() && user2Optional.isPresent()) {
            Utilizator user1 = user1Optional.get();
            Utilizator user2 = user2Optional.get();

            //user1.addFriend(user2);
            // user2.addFriend(user1);
            repoUtilizatori.update(user1);
            repoUtilizatori.update(user2);
            return repo.save(entity);
        } else {
            throw new IllegalArgumentException("Nu exista user cu id-ul " + (user1Optional.isPresent() ? id2 : id1));
        }
    }

    @Override
    public Optional<Prietenie> delete(Tuple<Long, Long> longLongTuple) {
        Long id1 = longLongTuple.getLeft();
        Long id2 = longLongTuple.getRight();
        Optional<Utilizator> u1 = repoUtilizatori.findOne(id1);
        Optional<Utilizator> u2 = repoUtilizatori.findOne(id2);

        if (u1.isEmpty() || u2.isEmpty()) {
            throw new IllegalArgumentException("User inexistent!");
        }

        Optional<Prietenie> deleted = repo.delete(longLongTuple);
        return deleted;
    }

    public List<String> friendsMadeInACertainMonth(Utilizator user, Integer luna) {
        List<Long> ids = repoUtilizatori.getFriendsIds(user.getId());

        List<String> result = ids.stream()
                .map(x -> repo.findOne(new Tuple<Long, Long>(x, user.getId())).get()) //transform din lista de Id-uri in lista de prietenii
                .map(x -> {
                    Utilizator utilizator;
                    if (x.getId().getLeft() == user.getId())
                        utilizator = repoUtilizatori.findOne(x.getId().getRight()).get();
                    else utilizator = repoUtilizatori.findOne(x.getId().getLeft()).get();

                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
                    return utilizator.getLastName() + " | " + utilizator.getFirstName() + " | "
                            + x.getDate().format(formatter);
                })//transform din lista de prietenii in lista de stringuri dupa formatul cerut
                .filter(str -> {
                    String[] parts = str.split("\\|");

                    String month = parts[2].trim().substring(3, 5);
                    return Integer.parseInt(month) == luna;
                })
                .collect(Collectors.toList());
        return result;
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
}



