package com.example.demo.repository.RepoDB;



import com.example.demo.domain.FriendRequest;
import com.example.demo.domain.FriendRequestStatus;
import com.example.demo.domain.Tuple;
import com.example.demo.domain.Utilizator;
import com.example.demo.domain.validators.FriendRequestValidator;
import com.example.demo.domain.validators.Validator;
import com.example.demo.repository.PagingRepository;
import com.example.demo.repository.RepoPage.Page;
import com.example.demo.repository.RepoPage.Pageable;
import com.example.demo.repository.Repository;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;

public class RepoFriendRequest implements PagingRepository<Tuple<Long,Long>, FriendRequest> {

    private final String url;
    private final String user;
    private final String password;

    private final Validator<FriendRequest> validator;

    public RepoFriendRequest(String url, String user, String password) {
        this.url = url;
        this.user = user;
        this.password = password;
        validator = new FriendRequestValidator();
    }

    @Override
    public Optional<FriendRequest> findOne(Tuple<Long, Long> longLongTuple) {
        try(Connection connection = DriverManager.getConnection(url, user, password);
            PreparedStatement statement = connection.prepareStatement("select * from friend_request " +
                    "where (id1 = ? and id2 = ?)")
        ){
            statement.setInt(1, Math.toIntExact(longLongTuple.getLeft()));
            statement.setInt(2, Math.toIntExact(longLongTuple.getRight()));

            ResultSet resultSet = statement.executeQuery();
            if(resultSet.next()){

                String status = resultSet.getString("status"); //extrag din coloana status din BD
                FriendRequestStatus friendRequestStatus;
                if(status.equals("PENDING")) friendRequestStatus = FriendRequestStatus.PENDING;
                else if(status.equals("APPROVED")) friendRequestStatus = FriendRequestStatus.APPROVED;
                else friendRequestStatus = FriendRequestStatus.REJECTED;

                FriendRequest friendRequest = new FriendRequest(friendRequestStatus);
                friendRequest.setId(new Tuple<Long, Long>(resultSet.getLong("id1"), resultSet.getLong("id2")));
                return Optional.of(friendRequest);
            }
        }
        catch(SQLException e){
            throw new RuntimeException(e);
        }
        return Optional.empty();
    }

    @Override
    public Iterable<FriendRequest> findAll() {
        return null;
    }

    @Override
    public Optional<FriendRequest> save(FriendRequest entity) {
        if(entity.getId() == null)
            throw new IllegalArgumentException("Eroare! ID-urile nu pot sa fie null!");
        validator.validate(entity);

        try(Connection connection = DriverManager.getConnection(url, user, password);
            PreparedStatement statement = connection.prepareStatement("insert into friend_request(id1, id2, status) " +
                    "values(?,?,?)");
        ){
            statement.setInt(1,entity.getId().getLeft().intValue());
            statement.setInt(2,entity.getId().getRight().intValue());
            statement.setString(3,entity.getStatus().toString());

            int affectedRows = statement.executeUpdate();
            return affectedRows == 0 ? Optional.of(entity) : Optional.empty();
        }
        catch(SQLException e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<FriendRequest> delete(Tuple<Long, Long> longLongTuple) {
        return Optional.empty();
    }

    @Override
    public Optional<FriendRequest> update(FriendRequest entity) {
        if(entity.getId() == null)
            throw new IllegalArgumentException("Eroare! ID-urile nu pot sa fie null!");
        validator.validate(entity);

        try(Connection connection = DriverManager.getConnection(url, user, password);
            PreparedStatement statement = connection.prepareStatement("update friend_request set status = ? " +
                    "where (id1 = ? and id2 = ?)");
        ){
            statement.setString(1, entity.getStatus().toString());
            statement.setInt(2,entity.getId().getLeft().intValue());
            statement.setInt(3,entity.getId().getRight().intValue());
            int affectedRows = statement.executeUpdate();
            return affectedRows == 0 ? Optional.ofNullable(entity) : Optional.empty();
        }
        catch(SQLException e){
            throw new RuntimeException(e);
        }
    }


    @Override
    public Page<FriendRequest> findAll(Pageable pageable) {
        List<FriendRequest> friend_requests = new ArrayList<>();
        try(Connection connection= DriverManager.getConnection(url, user, password);
            PreparedStatement pagePreparedStatement=connection.prepareStatement("SELECT * FROM friend_request " +
                    "LIMIT ? OFFSET ?");

            PreparedStatement countPreparedStatement = connection.prepareStatement
                    ("SELECT COUNT(*) as count " +
                            "FROM friend_request ");

        ) {
            pagePreparedStatement.setInt(1, pageable.getSize_page());
            pagePreparedStatement.setInt(2, pageable.getSize_page() * pageable.getNr_page());
            try (ResultSet resultSet = pagePreparedStatement.executeQuery();
                 ResultSet countResultSet = countPreparedStatement.executeQuery(); ) {
                while (resultSet.next()) {
                    Long id1 = resultSet.getLong("id1");
                    Long id2 =resultSet.getLong("id2");
                    String status=resultSet.getString("status");
                    FriendRequestStatus friendRequestStatus;
                    if(status.equals("PENDING")) friendRequestStatus = FriendRequestStatus.PENDING;
                    else if(status.equals("APPROVED")) friendRequestStatus = FriendRequestStatus.APPROVED;
                    else friendRequestStatus = FriendRequestStatus.REJECTED;

                    FriendRequest friendRequest = new FriendRequest(friendRequestStatus);
                    friendRequest.setId(new Tuple<Long, Long>(resultSet.getLong("id1"), resultSet.getLong("id2")));

                }
                int totalCount = 0;
                if(countResultSet.next()) {
                    totalCount = countResultSet.getInt("count");
                }

                return new Page<>(friend_requests, totalCount);

            }
        }
        catch (SQLException e){
            throw new RuntimeException(e);
        }
    }
}
