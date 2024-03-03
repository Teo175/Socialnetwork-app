package com.example.demo.repository.RepoDB;

import com.example.demo.domain.Message;
import com.example.demo.domain.Utilizator;
import com.example.demo.repository.RepoPage.Page;
import com.example.demo.repository.RepoPage.Pageable;
import com.example.demo.repository.Repository;


import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;


public class MessageRepoDB implements Repository<Long, Message> {

    private final String url;
    private final String user;
    private final String password;
    private Repository<Long, Utilizator> utilizatorRepository;

    public MessageRepoDB(String url, String user, String password, Repository<Long, Utilizator> utilizatorRepository) {
        this.url = url;
        this.user = user;
        this.password = password;
        this.utilizatorRepository = utilizatorRepository;
    }

    public Optional<Message> findOneWithoutReply(Long longID) {
        try (Connection connection = DriverManager.getConnection(url, user, password);
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM message WHERE id=?")) {
            statement.setLong(1, longID);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                Long from_id = resultSet.getLong("from_id");
                Long to_id = resultSet.getLong("to_id");
                LocalDateTime date = resultSet.getTimestamp("date").toLocalDateTime();
                String message = resultSet.getString("message");
                Message msg = new Message(utilizatorRepository.findOne(from_id).get(), Collections.singletonList(utilizatorRepository.findOne(to_id).get()), date, message);
                msg.setId(longID);
                return Optional.of(msg);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return Optional.empty();
    }

    @Override
    public Optional<Message> findOne(Long longID) {

        Message msg;
        if (findOneWithoutReply(longID).isPresent()) {
            msg = findOneWithoutReply(longID).get();
        } else return Optional.empty();

        try (Connection connection = DriverManager.getConnection(url, user, password);
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM message WHERE id=?")) {
            statement.setLong(1, longID);
            ResultSet resultSet = statement.executeQuery();
            long reply_id = resultSet.getLong("reply_id");
            if (!resultSet.wasNull()) {
                Message reply;
                if (findOneWithoutReply(reply_id).isPresent()) {
                    reply = findOneWithoutReply(reply_id).get();
                } else return Optional.empty();


                msg.setReply(findOneWithoutReply(reply_id).get());
                return Optional.of(msg);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return Optional.empty();
    }

    @Override
    public Iterable<Message> findAll() {
        List<Message> messages = new ArrayList<>();

        try (Connection connection = DriverManager.getConnection(url, user, password);
             PreparedStatement statement = connection.prepareStatement("select * from message");
             ResultSet resultSet = statement.executeQuery()
        ) {
            while (resultSet.next()) {
                Long id = resultSet.getLong("id");
                Long from_id = resultSet.getLong("from_id");
                Long to_id = resultSet.getLong("to_id");
                LocalDateTime date = resultSet.getTimestamp("data").toLocalDateTime();
                String message = resultSet.getString("message");

                Message msg = new Message(utilizatorRepository.findOne(from_id).get(), Collections.singletonList(utilizatorRepository.findOne(to_id).get()), date, message);
                msg.setId(id);
                messages.add(msg);
            }
            return messages;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<Message> save(Message entity) {
        try(Connection connection = DriverManager.getConnection(url,user,password);
            PreparedStatement statement  = connection.prepareStatement("INSERT INTO message(from_id,to_id,data, message, reply_id) VALUES (?,?,?,?,?)"))
        {
            statement.setLong(1,entity.getFrom().getId());
            statement.setLong(2,entity.getTo().get(0).getId());
            statement.setTimestamp(3, Timestamp.valueOf(entity.getDate()));
            statement.setString(4, entity.getMessage());
            if (entity.getReply() != null)
                statement.setLong(5, entity.getReply().getId());
            else statement.setNull(5, java.sql.Types.NULL);

            int affectedRows = statement.executeUpdate();
            return affectedRows!=0? Optional.empty():Optional.of(entity);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    @Override
    public Optional<Message> delete(Long longID) {
        try(Connection connection = DriverManager.getConnection(url,user,password);
            PreparedStatement statement  = connection.prepareStatement("DELETE FROM message WHERE ID = ?");)
        {
            Optional<Message> cv = findOne(longID);
            statement.setLong(1,longID);
            int affectedRows = statement.executeUpdate();
            return affectedRows==0? Optional.empty():cv;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<Message> update(Message entity) {
        try(Connection connection = DriverManager.getConnection(url,user,password);
            PreparedStatement statement  = connection.prepareStatement("UPDATE message SET from_id = ?, to_id = ?, data = ?, message = ?, reply_id = ? WHERE id = ?"))
        {
            statement.setLong(1,entity.getFrom().getId());
            statement.setLong(2,entity.getTo().get(0).getId());
            statement.setTimestamp(3, Timestamp.valueOf(entity.getDate()));
            statement.setString(4, entity.getMessage());
            statement.setLong(5,entity.getReply().getId());
            statement.setLong(6,entity.getId());
            int affectedRows = statement.executeUpdate();
            return affectedRows!=0? Optional.empty():Optional.of(entity);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Page<Message> getAllBetweenUsers(Pageable pageable, Long user1, Long user2){
        List<Message> messages = new ArrayList<>();

        try(Connection connection = DriverManager.getConnection(url, user, password);
            PreparedStatement statement = connection.prepareStatement(
                    "select * from message " +
                            "where (from_id = ? and to_id = ?) or (from_id = ? and to_id = ?) " +
                            "order by message.data" +
                            " LIMIT ? OFFSET ?");

            PreparedStatement countPreparedStatement = connection.prepareStatement
                    ("SELECT COUNT(*) as count " +
                            "FROM message " +
                            "WHERE (from_id = ? AND to_id = ?) OR (from_id = ? AND to_id = ?) ");
        ){
            statement.setInt(1, user1.intValue());
            statement.setInt(2, user2.intValue());
            statement.setInt(3, user2.intValue());
            statement.setInt(4, user1.intValue());
            statement.setInt(5, pageable.getSize_page());
            statement.setInt(6, pageable.getSize_page() * pageable.getNr_page());
            countPreparedStatement.setInt(1, user1.intValue());
            countPreparedStatement.setInt(2, user2.intValue());
            countPreparedStatement.setInt(3, user2.intValue());
            countPreparedStatement.setInt(4, user1.intValue());
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                Long id = resultSet.getLong("id");
                Long from_id = resultSet.getLong("from_id");
                Long to_id = resultSet.getLong("to_id");
                LocalDateTime date = resultSet.getTimestamp("data").toLocalDateTime();
                String message = resultSet.getString("message");

                Message msg = new Message(utilizatorRepository.findOne(from_id).get(), Collections.singletonList(utilizatorRepository.findOne(to_id).get()), date, message);
                msg.setId(id);
                messages.add(msg);
            }

            ResultSet countResultSet = countPreparedStatement.executeQuery();
            int totalCount = 0;
            if(countResultSet.next()) {
                totalCount = countResultSet.getInt("count");
            }
            return new Page<>(messages, totalCount);
        }
        catch(SQLException e){
            throw new RuntimeException(e);
        }
    }
}