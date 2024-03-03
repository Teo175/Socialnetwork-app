package com.example.demo.repository.RepoDB;

import com.example.demo.domain.Prietenie;
import com.example.demo.domain.Tuple;
import com.example.demo.domain.validators.PrietenieValidator;
import com.example.demo.domain.validators.Validator;
import com.example.demo.repository.Repository;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class PrietenieRepoDB implements Repository<Tuple<Long,Long>, Prietenie> {

    private String url;
    private String user;
    private String password;


    private Validator<Prietenie> validator;



    public PrietenieRepoDB(String url, String username, String password) {
        this.url = url;
        this.user = username;
        this.password = password;
        validator = new PrietenieValidator();
    }

    @Override
    public Optional<Prietenie> findOne(Tuple<Long, Long> longLongTuple) {
        try(Connection connection = DriverManager.getConnection(url, user, password);
            PreparedStatement statement = connection.prepareStatement("select * from prietenii where (id1 = ? and id2 = ?) or (id1 = ? and id2 =? )");

        ) {
            statement.setInt(1, Math.toIntExact(longLongTuple.getLeft()));
            statement.setInt(2,Math.toIntExact(longLongTuple.getRight()));

            statement.setInt(3, Math.toIntExact(longLongTuple.getRight()));
            statement.setInt(4,Math.toIntExact(longLongTuple.getLeft()));

            ResultSet resultSet = statement.executeQuery();
            if(resultSet.next()) {
                Long id1 = resultSet.getLong("id1");
                Long id2 =  resultSet.getLong("id2");
                LocalDateTime data = resultSet.getTimestamp(3).toLocalDateTime();
                Prietenie p = new Prietenie(data);
                Tuple<Long, Long> idTuple = new Tuple<>(id1, id2);
                p.setId(idTuple);
                return Optional.ofNullable(p);
            }
        } catch (SQLException e) {

            throw new RuntimeException(e);
        }

        return Optional.empty();
    }

    @Override
    public Iterable<Prietenie> findAll() {
        Set<Prietenie> friendships = new HashSet<>();

        try (Connection connection = DriverManager.getConnection(url, user, password);
             PreparedStatement statement = connection.prepareStatement("select * from prietenii");
             ResultSet resultSet = statement.executeQuery()
        ) {

            while (resultSet.next())
            {
                Long id1= resultSet.getLong("id1");
                Long id2= resultSet.getLong("id2");
                LocalDateTime data = resultSet.getTimestamp("friends_from").toLocalDateTime();
                Prietenie p = new Prietenie(data);
                Tuple<Long, Long> idTuple = new Tuple<>(id1, id2);
                p.setId(idTuple);

                friendships.add(p);

            }
            return friendships;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<Prietenie> save(Prietenie entity) {
        if(entity.getId() == null)
            throw new IllegalArgumentException("Eroare! ID-urile nu pot sa fie null!");
        validator.validate(entity);

        System.out.println("DATA:");
        System.out.println(entity.getDate());

        try(Connection connection = DriverManager.getConnection(url, user, password);
            PreparedStatement statement = connection.prepareStatement("insert into prietenii(id1, id2, friends_from) values(?,?,?)");)
        {
            statement.setInt(1,entity.getId().getLeft().intValue());
            statement.setInt(2,entity.getId().getRight().intValue());

            statement.setTimestamp(3, Timestamp.valueOf(entity.getDate()));
            int affectedRows = statement.executeUpdate();
            return affectedRows == 0 ? Optional.ofNullable(entity) : Optional.empty();
        }
        catch(SQLException e){
            throw new RuntimeException(e);
        }
    }

    //nu se face momentan
    @Override
    public Optional<Prietenie> delete(Tuple<Long, Long> longLongTuple) {
        if(longLongTuple == null)
            throw new IllegalArgumentException("Eroare! ID-ul nu poate sa fie null!");

        try(Connection connection = DriverManager.getConnection(url, user, password);
            PreparedStatement statement = connection.prepareStatement("delete from prietenii " +
                    "where (id1 = ? AND id2 = ?) OR (id1 = ? AND id2 = ?)");
        ){
            Optional<Prietenie> prietenie = findOne(longLongTuple);

            statement.setInt(1, longLongTuple.getLeft().intValue());
            statement.setInt(2, longLongTuple.getRight().intValue());
            statement.setInt(3, longLongTuple.getRight().intValue());
            statement.setInt(4, longLongTuple.getLeft().intValue());
            int affectedRows = statement.executeUpdate();
            return affectedRows == 0 ? Optional.empty() : Optional.ofNullable(prietenie.get());
        }
        catch(SQLException e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<Prietenie> update(Prietenie entity) {
        if(entity.getId() == null)
            throw new IllegalArgumentException("Eroare! ID-urile nu pot sa fie null!");
        validator.validate(entity);

        try(Connection connection = DriverManager.getConnection(url, user, password);
            PreparedStatement statement = connection.prepareStatement("update prietenii set friends_from = ? " +
                    "where (id1 = ? and id2 = ?) or (id1 = ? and id2 = ?)");
        ){
            statement.setTimestamp(1,Timestamp.valueOf(entity.getDate()));
            statement.setInt(2,entity.getId().getLeft().intValue());
            statement.setInt(3,entity.getId().getRight().intValue());
            statement.setInt(4,entity.getId().getRight().intValue());
            statement.setInt(5,entity.getId().getLeft().intValue());
            int affectedRows = statement.executeUpdate();
            return affectedRows == 0 ? Optional.ofNullable(entity) : Optional.empty();
        }
        catch(SQLException e){
            throw new RuntimeException(e);
        }
    }
}