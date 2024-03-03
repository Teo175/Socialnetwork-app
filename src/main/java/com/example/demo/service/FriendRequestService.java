package com.example.demo.service;

import com.example.demo.domain.FriendRequest;
import com.example.demo.domain.Prietenie;
import com.example.demo.domain.Tuple;
import com.example.demo.repository.RepoDB.PrietenieRepoDB;
import com.example.demo.repository.RepoDB.RepoFriendRequest;
import com.example.demo.repository.RepoDB.UserRepoDB;
import com.example.demo.repository.RepoPage.Page;
import com.example.demo.repository.RepoPage.Pageable;
import com.example.demo.utils.events.UserTaskChangeEvent;
import com.example.demo.utils.observer.Observable;
import com.example.demo.utils.observer.Observer;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class FriendRequestService implements Service<Tuple<Long,Long>, FriendRequest>, Observable<UserTaskChangeEvent> {
    private RepoFriendRequest repo;
    private UserRepoDB userRepoDB;
    private PrietenieRepoDB prietenieRepoDB;
    private List<Observer<UserTaskChangeEvent>> observers=new ArrayList<>();

    public FriendRequestService(RepoFriendRequest repo, UserRepoDB repoUtilizatori, PrietenieRepoDB prietenieRepoDB) {
        this.repo = repo;
        this.userRepoDB = repoUtilizatori;
        this.prietenieRepoDB = prietenieRepoDB;
    }


    @Override
    public Optional<FriendRequest> add(FriendRequest entity) {
        Optional<Prietenie> friendship = prietenieRepoDB.findOne(entity.getId());
        if(friendship.isPresent())
            throw new IllegalArgumentException("Eroare! Nu se poate trimite cererea, sunteti deja prieteni!");

        return repo.save(entity);
    }

    @Override
    public Optional<FriendRequest> delete(Tuple<Long, Long> longLongTuple) {
        return Optional.empty();
    }

    @Override
    public Optional<FriendRequest> getEntityById(Tuple<Long, Long> longLongTuple) {
        return repo.findOne(longLongTuple);
    }

    @Override
    public Iterable<FriendRequest> getAll() {
        return null;
    }

    public Optional<FriendRequest> update(FriendRequest entity) { return repo.update(entity); }

    public List<Long> getFriendRequestIds(Long id){ return userRepoDB.getFriendsIdsForFriendRequest(id); }

    public Page<Long> getFriendRequestIds(Long id, Pageable pageable){
        return userRepoDB.getFriendsIdsForFriendRequest(id, pageable);
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
