package ru.yandex.practicum.filmorate.storage.impl;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.FriendshipStatus;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static ru.yandex.practicum.filmorate.model.FriendshipStatus.REQUESTED;

@Component
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;

    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<User> findAll() {
        return jdbcTemplate.query("SELECT * FROM users", (rs, rowNum) -> makeUser(rs));
    }

    @Override
    public User create(User user) {
        String sqlQuery = "INSERT INTO users(email, login, name, birthday) VALUES (?, ?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"id"});
            stmt.setString(1, user.getEmail());
            stmt.setString(2, user.getLogin());
            stmt.setString(3, user.getName());
            stmt.setDate(4, Date.valueOf(user.getBirthday()));
            return stmt;
        }, keyHolder);

        user.setId(Objects.requireNonNull(keyHolder.getKey()).longValue());
        return user;
    }

    @Override
    public User update(User user) {
        findUserById(user.getId());
        String sqlQuery = "MERGE INTO users KEY (id) VALUES (?, ?, ?, ?, ?)";

        jdbcTemplate.update(sqlQuery, user.getId(), user.getEmail(), user.getLogin(), user.getName(), user.getBirthday());
        return user;
    }

    @Override
    public User findUserById(Long id) {
        String sqlQuery = "SELECT * FROM users WHERE id = ?";
        try {
            return jdbcTemplate.queryForObject(sqlQuery, (rs, rowNum) -> makeUser(rs, id), id);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("User with such id wasn't found in DB");
        }
    }

    @Override
    public void addFriend(Long userId, Long friendId) {
        findUserById(userId);
        findUserById(friendId);

        String sqlQuery = "INSERT INTO user_friends (user_id, friend_id, status) VALUES (?, ?, ?)";
        jdbcTemplate.update(sqlQuery, userId, friendId, REQUESTED.name());
    }

    @Override
    public Integer removeFriend(Long userId, Long friendId) {
        findUserById(userId);
        findUserById(friendId);

        String sqlQuery = "DELETE FROM user_friends WHERE user_id = ? AND friend_id = ?";
        return jdbcTemplate.update(sqlQuery, userId, friendId);
    }

    @Override
    public List<User> findAllFriends(Long userId) {
        String sqlQuery = "SELECT * FROM user_friends uf JOIN users u ON u.id = uf.friend_id WHERE user_id = ?";
        return jdbcTemplate.query(sqlQuery, (rs, rowNum) -> makeUser(rs), userId);
    }

    @Override
    public List<User> findCommonFriends(Long id, Long otherId) {
        String sqlQuery = "SELECT * FROM users WHERE id IN " +
                "(SELECT a.friend_id FROM user_friends AS a JOIN user_friends AS b ON a.friend_id = b.friend_id " +
                "WHERE a.user_id = ? AND b.user_id = ?)";
        return jdbcTemplate.query(sqlQuery, (rs, rowNum) -> makeUser(rs), id, otherId);
    }

    private Map<Long, FriendshipStatus> findAllFriendsAndStatuses(Long userId) {
        Map<Long, FriendshipStatus> friends = new HashMap<>();
        SqlRowSet friendRows = jdbcTemplate.queryForRowSet("SELECT * FROM user_friends WHERE user_id = ?", userId);
        while (friendRows.next()) {
            friends.put(friendRows.getLong("friend_id"),
                    FriendshipStatus.valueOf(friendRows.getString("status")));
        }
        return friends;
    }

    private User makeUser(ResultSet rs) throws SQLException {
        Long id = rs.getLong("id");
        String email = rs.getString("email");
        String login = rs.getString("login");
        String name = rs.getString("name");
        LocalDate birthday = rs.getDate("birthday").toLocalDate();
        Map<Long, FriendshipStatus> friendsIdsAndStatus = findAllFriendsAndStatuses(id);

        return new User(id, email, login, name, birthday, friendsIdsAndStatus);
    }

    private User makeUser(ResultSet rs, Long id) throws SQLException {
        String email = rs.getString("email");
        String login = rs.getString("login");
        String name = rs.getString("name");
        LocalDate birthday = rs.getDate("birthday").toLocalDate();
        Map<Long, FriendshipStatus> friendsIdsAndStatus = findAllFriendsAndStatuses(id);

        return new User(id, email, login, name, birthday, friendsIdsAndStatus);
    }
}
