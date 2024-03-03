package com.example.demo.repository.RepoDB;

import com.example.demo.domain.PasswordEncoder;
import com.example.demo.domain.Utilizator;
import com.example.demo.domain.validators.UtilizatorValidator;
import com.example.demo.domain.validators.Validator;
import com.example.demo.repository.PagingRepository;
import com.example.demo.repository.RepoPage.Page;
import com.example.demo.repository.RepoPage.Pageable;
import com.example.demo.repository.Repository;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import java.sql.*;
import java.util.*;

public class UserRepoDB implements PagingRepository<Long, Utilizator> {

    private String url;
    private String user;
    private String password;

    //private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();


    private Validator<Utilizator> validator;

    public UserRepoDB(String url, String user, String password) {
        this.url = url;
        this.user = user;
        this.password = password;
        validator = new UtilizatorValidator();
    }

    @Override
    public Optional<Utilizator> findOne(Long longID) {

        try(Connection connection = DriverManager.getConnection(url, user, password);
            PreparedStatement statement = connection.prepareStatement("select * from users " +
                    "where id = ?");

        ) {
            statement.setInt(1, Math.toIntExact(longID));
            ResultSet resultSet = statement.executeQuery();
            if(resultSet.next()) {
                String firstName = resultSet.getString("first_name");
                String lastName = resultSet.getString("last_name");
                String username = resultSet.getString("username");
                String password = resultSet.getString("password");
                Utilizator u = new Utilizator(firstName,lastName,username,password);
                u.setId(longID);
                return Optional.ofNullable(u);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return Optional.empty();

    }

    @Override
    public Iterable<Utilizator> findAll() {
        Set<Utilizator> users = new HashSet<>();

        try (Connection connection = DriverManager.getConnection(url, user, password);
             PreparedStatement statement = connection.prepareStatement("select * from users");
             ResultSet resultSet = statement.executeQuery()
        ) {

            while (resultSet.next())
            {
                Long id= resultSet.getLong("id");
                String firstName=resultSet.getString("first_name");
                String lastName=resultSet.getString("last_name");
                String username = resultSet.getString("username");
                String password = resultSet.getString("password");
                Utilizator user = new Utilizator(firstName,lastName,username,password);
                user.setId(id);
                users.add(user);

            }
            return users;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }



    @Override
    public Optional<Utilizator> save(Utilizator entity) {
        if(entity.getId() == null)
            throw new IllegalArgumentException("Eroare! Id-ul nu poate sa fie null!");

        validator.validate(entity);

        if(findOne(entity.getId()).isPresent())
            return Optional.of(entity);

        try(Connection connection = DriverManager.getConnection(url, user, password);
            PreparedStatement statement = connection.prepareStatement("insert into users(id, first_name, last_name,username,password) " +
                    "values(?,?,?,?,?)");
        ){
            statement.setInt(1, entity.getId().intValue());
            statement.setString(2, entity.getFirstName());
            statement.setString(3, entity.getLastName());
            statement.setString(4, entity.getUsername());
            statement.setString(5, PasswordEncoder.encodePassword(entity.getPassword()));

            int affectedRows = statement.executeUpdate();
            return affectedRows == 0 ? Optional.ofNullable(entity) : Optional.empty();
        }
        catch(SQLException e){
            throw new RuntimeException(e);
        }

    }

    @Override
    public Optional<Utilizator> delete(Long aLong) {
        if(aLong == null)
            throw new IllegalArgumentException("Eroare! Id-ul nu poate sa fie null!");
        Optional<Utilizator> entity = findOne(aLong);
        try(Connection connection = DriverManager.getConnection(url,user,password);
            PreparedStatement statement  = connection.prepareStatement("DELETE FROM users WHERE id = ?");)
        {
            statement.setInt(1, aLong.intValue());
            int affectedRows = statement.executeUpdate();
            return affectedRows==0? Optional.empty():Optional.ofNullable(entity.get());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<Utilizator> update(Utilizator entity) {
        try(Connection connection = DriverManager.getConnection(url, user, password);
            PreparedStatement statement = connection.prepareStatement("update users " +
                    "set first_name = ?,last_name = ?, username = ?, password = ? where id = ?");
        ){
            statement.setString(1, entity.getFirstName());
            statement.setString(2, entity.getLastName());
            statement.setString(3, entity.getUsername());
            statement.setString(4, PasswordEncoder.encodePassword(entity.getPassword()));
            statement.setInt(5, entity.getId().intValue());
            int affectedRows = statement.executeUpdate();
            return affectedRows==0? Optional.empty():Optional.ofNullable(entity);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public Iterable<Utilizator> getAllN(Long n)
    {

        Iterable<Utilizator> allUsers = (Iterable<Utilizator>) findAll();
        List<Utilizator> users =new LinkedList<>();
        //  entities.values().stream()
        //           .filter((user)->user.get)
        for (Utilizator allUser : allUsers) {
            if(getFriendsIds(allUser.getId()).size()>=n)
                users.add(allUser);
        }

        return users;
    }

    public List<Long> getFriendsIds(Long id){
        //toti prietenii unui om dupa id dat
        List<Long> idList = new ArrayList<>();

        try(Connection connection = DriverManager.getConnection(url, user, password);
            PreparedStatement statement = connection.prepareStatement(
                    "select users.id, prietenii.id1, prietenii.id2 " +
                            "from users " +
                            "INNER JOIN prietenii on (users.id = prietenii.id1 or users.id = prietenii.id2) " +
                            "where users.id = ?");
        ){
            statement.setInt(1,id.intValue());
            ResultSet resultSet = statement.executeQuery();

            while(resultSet.next()){
                Long id1 = resultSet.getLong("id1");
                Long id2 = resultSet.getLong("id2");

                if(id1 != id) idList.add(id1);
                else idList.add(id2);
            }
        }
        catch(SQLException e){
            throw new RuntimeException(e);
        }

        return idList;
    }

    public Page<Long> getFriendsIdsForFriendRequest(Long id, Pageable pageable){
        //toate cereirle de prietenie paginati unui om dupa id dat
        //toti prietenii paginati unui om dupa id dat
        List<Long> idList = new ArrayList<>();

        try(Connection connection = DriverManager.getConnection(url, user, password);
            PreparedStatement statement = connection.prepareStatement(
                    "select users.id, friend_request.id1, friend_request.id2 " +
                            "from users " +
                            "INNER JOIN friend_request on users.id = friend_request.id2 " +
                            "where (users.id = ? and friend_request.status = 'PENDING')" +
                            " LIMIT ? OFFSET ?");

            PreparedStatement countPreparedStatement = connection.prepareStatement
                    ("SELECT COUNT(*) AS count FROM prietenii ");
        ){
            statement.setInt(1,id.intValue());
            statement.setInt(2, pageable.getSize_page());
            statement.setInt(3, pageable.getSize_page() * pageable.getNr_page());
            ResultSet resultSet = statement.executeQuery();

            while(resultSet.next()){
                Long idBun = resultSet.getLong("id1");
                idList.add(idBun);
            }
            ResultSet countResultSet = countPreparedStatement.executeQuery();
            int totalCount = 0;
            if(countResultSet.next()) {
                totalCount = countResultSet.getInt("count");
            }
            return new Page<>(idList, totalCount);
        }
        catch(SQLException e){
            throw new RuntimeException(e);
        }

    }
    public List<Long> getFriendsIdsForFriendRequest(Long id){

        List<Long> idList = new ArrayList<>();

        try(Connection connection = DriverManager.getConnection(url, user, password);
            PreparedStatement statement = connection.prepareStatement(
                    "select users.id, friend_request.id1, friend_request.id2 " +
                            "from users " +
                            "INNER JOIN friend_request on users.id = friend_request.id2 " +
                            "where (users.id = ? and friend_request.status = 'PENDING')");
        ){
            statement.setInt(1,id.intValue());
            ResultSet resultSet = statement.executeQuery();

            while(resultSet.next()){
                Long idBun = resultSet.getLong("id1");
                idList.add(idBun);
            }
        }
        catch(SQLException e){
            throw new RuntimeException(e);
        }

        return idList;
    }




    public Optional<Utilizator> findFirstByName(String fn, String ln) {

        try(Connection connection = DriverManager.getConnection(url, user, password);
            PreparedStatement statement = connection.prepareStatement("select * from users " +
                    "where first_name = ? and last_name=? LIMIT 1");

        ){
            statement.setString(1, fn);
            statement.setString(2,ln);
            ResultSet resultSet = statement.executeQuery();

            if(resultSet.next()){
                Integer integer = resultSet.getInt("id");
                String username = resultSet.getString("username");
                String password = resultSet.getString("password");
                Long id = integer.longValue();
                Utilizator user = new Utilizator(fn,ln,username,password);
                user.setId(id);
                return Optional.ofNullable(user);
            }
        }
        catch(SQLException e){
            throw new RuntimeException(e);
        }

        return Optional.empty();
    }

    @Override
    public Page<Utilizator> findAll(Pageable pageable) {
        List<Utilizator> users = new ArrayList<>();
        try(Connection connection= DriverManager.getConnection(url, user, password);
            PreparedStatement pagePreparedStatement=connection.prepareStatement("SELECT * FROM users " +
                    "LIMIT ? OFFSET ?");

            PreparedStatement countPreparedStatement = connection.prepareStatement
                    ("SELECT COUNT(*) AS count FROM users ");

        ) {
            pagePreparedStatement.setInt(1, pageable.getSize_page());
            pagePreparedStatement.setInt(2, pageable.getSize_page() * pageable.getNr_page());
            try (ResultSet resultSet = pagePreparedStatement.executeQuery();
                 ResultSet countResultSet = countPreparedStatement.executeQuery(); ) {
                while (resultSet.next()) {
                    Long id= resultSet.getLong("id");
                    String firstName=resultSet.getString("first_name");
                    String lastName=resultSet.getString("last_name");
                    String username = resultSet.getString("username");
                    String password = resultSet.getString("password");
                    Utilizator user=new Utilizator(firstName,lastName,username,password);
                    user.setId(id);
                    users.add(user);
                }
                int totalCount = 0;
                if(countResultSet.next()) {
                    totalCount = countResultSet.getInt("count");
                }

                return new Page<>(users, totalCount);

            }
        }
        catch (SQLException e){
            throw new RuntimeException(e);
        }
    }


    public Page<Long> getFriendsIds(Long id, Pageable pageable){
        //toti prietenii paginati unui om dupa id dat
        List<Long> idList = new ArrayList<>();

        try(Connection connection = DriverManager.getConnection(url, user, password);
            PreparedStatement statement = connection.prepareStatement(
                    "select users.id, prietenii.id1, prietenii.id2 " +
                            "from users " +
                            "INNER JOIN prietenii on (users.id = prietenii.id1 or users.id = prietenii.id2) " +
                            "where users.id = ?" +
                        " LIMIT ? OFFSET ?");

            PreparedStatement countPreparedStatement = connection.prepareStatement
                    ("SELECT COUNT(*) AS count FROM prietenii ");
        ){
            statement.setInt(1,id.intValue());
            statement.setInt(2, pageable.getSize_page());
            statement.setInt(3, pageable.getSize_page() * pageable.getNr_page());
            ResultSet resultSet = statement.executeQuery();

            while(resultSet.next()){
                Long id1 = resultSet.getLong("id1");
                Long id2 = resultSet.getLong("id2");

                if(id1 != id) idList.add(id1);
                else idList.add(id2);
            }
            ResultSet countResultSet = countPreparedStatement.executeQuery();
            int totalCount = 0;
            if(countResultSet.next()) {
                totalCount = countResultSet.getInt("count");
            }
            return new Page<>(idList, totalCount);
        }
        catch(SQLException e){
            throw new RuntimeException(e);
        }

    }

    public Optional<Utilizator> findFirstByUsername(String username) {
        try(Connection connection = DriverManager.getConnection(url, user, password);
            PreparedStatement statement = connection.prepareStatement("select * from users " +
                    "where username = ? LIMIT 1");

        ){
            statement.setString(1, username);
            ResultSet resultSet = statement.executeQuery();

            if(resultSet.next()){
                Integer integer = resultSet.getInt("id");
                String fn = resultSet.getString("first_name");
                String ln = resultSet.getString("last_name");
                String password = resultSet.getString("password");
                Long id = integer.longValue();
                Utilizator user = new Utilizator(fn,ln,username,password);
                user.setId(id);
                return Optional.ofNullable(user);
            }
        }
        catch(SQLException e){
            throw new RuntimeException(e);
        }

        return Optional.empty();

    }

}
