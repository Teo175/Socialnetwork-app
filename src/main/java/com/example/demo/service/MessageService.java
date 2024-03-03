package com.example.demo.service;

import com.example.demo.domain.Message;
import com.example.demo.domain.Utilizator;
import com.example.demo.repository.RepoDB.MessageRepoDB;
import com.example.demo.repository.RepoDB.UserRepoDB;
import com.example.demo.repository.RepoPage.Page;
import com.example.demo.repository.RepoPage.Pageable;
import com.example.demo.utils.events.UserTaskChangeEvent;
import com.example.demo.utils.observer.Observable;
import com.example.demo.utils.observer.Observer;

import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class MessageService implements Service<Long, Message>, Observable<UserTaskChangeEvent> {

    MessageRepoDB repo;
    UserRepoDB repoUtilizatori;

    private List<Observer<UserTaskChangeEvent>> observers=new ArrayList<>();

    public MessageService(MessageRepoDB repo , UserRepoDB repoUtilizatori) {
        this.repo = repo;
        this.repoUtilizatori = repoUtilizatori;
    }

    public ArrayList<Message> getMessagesBetweenTwoUsers(Long userId1, Long userId2) {
        if (repoUtilizatori.findOne(userId1).isEmpty() || repoUtilizatori.findOne(userId2).isEmpty())
            return new ArrayList<>();

        Collection<Message> messages = (Collection<Message>) repo.findAll();
        return messages.stream()
                .filter(x -> (x.getFrom().getId().equals(userId1) && x.getTo().get(0).getId().equals(userId2)) ||
                        (x.getFrom().getId().equals(userId2) && x.getTo().get(0).getId().equals(userId1)))
                .sorted(Comparator.comparing(Message::getDate))
                .collect(Collectors.toCollection(ArrayList::new));
    }


    public boolean addMessage(Long fromUserId, Long toUserId, String message) {
        try {
            Utilizator from = repoUtilizatori.findOne(fromUserId).orElse(null);
            Utilizator to = repoUtilizatori.findOne(toUserId).orElse(null);

            if (from == null || to == null)
                throw new Exception("Utilizatorul nu există");
            if (Objects.equals(message, ""))
                throw new Exception("Messageul este gol");

            Message msg = new Message(from, Collections.singletonList(to), message);
            repo.save(msg);

            List<Message> messagesTwoUsers = getMessagesBetweenTwoUsers(toUserId, fromUserId);
            if (messagesTwoUsers.size() > 1) {
                Message secondToLastMessage = messagesTwoUsers.get(messagesTwoUsers.size() - 2);
                Message lastMessage = messagesTwoUsers.get(messagesTwoUsers.size() - 1);
                lastMessage.setReply(secondToLastMessage);
                repo.update(lastMessage);
               // secondToLastMessage.setReply(lastMessage);
               // repo.update(secondToLastMessage);
            }

            return true;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return false;
        }
    }



    @Override
    public Optional<Message> add(Message entity) {
        if (addMessage(entity.getFrom().getId(), entity.getTo().get(0).getId(), entity.getMessage())) {
            return Optional.of(entity);
        } else {
            return Optional.empty();
        }
    }

    @Override
    public Optional<Message> delete(Long id) {
        return repo.delete(id);
    }

    @Override
    public Optional<Message> getEntityById(Long aLong) {
        return repo.findOne(aLong);
    }

    @Override
    public Iterable<Message> getAll() {
        return repo.findAll();
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

    public Page<Message> getAllBetweenUsers(Pageable pageable, Long user1, Long user2){ return repo.getAllBetweenUsers(pageable, user1, user2); }
}