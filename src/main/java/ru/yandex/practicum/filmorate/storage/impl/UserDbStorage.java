package ru.yandex.practicum.filmorate.storage.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
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
import java.util.stream.Collectors;

import static ru.yandex.practicum.filmorate.model.FriendshipStatus.REQUESTED;

@Component("userDbStorage")
@Primary
@Slf4j
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;

    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<User> findAll() {
        List<User> users = jdbcTemplate.query("SELECT * FROM users", (rs, rowNum) -> makeUser(rs));
        log.debug("Users quantity is: {}", users.size());
        return users;
    }

    @Override
    public User create(User user) {
        String sqlQuery = "insert into users(email, login, name, birthday) values (?, ?, ?, ?)";
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }

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

        log.debug("User is added: {}", user);
        return user;
    }

    @Override
    public User update(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        findUserById(user.getId());
        String sqlQuery = "merge into users key (id) values (?, ?, ?, ?, ?)";

        jdbcTemplate.update(sqlQuery, user.getId(), user.getEmail(), user.getLogin(), user.getName(), user.getBirthday());
        log.debug("User is updated: {}", user);
        return user;
    }

    @Override
    public User findUserById(Long id) {
        String sqlQuery = "SELECT * FROM users WHERE id = ?";
        try {
            User user = jdbcTemplate.queryForObject(sqlQuery, (rs, rowNum) -> makeUser(rs, id), id);
            log.debug("User is found in DB: {}", user);
            return user;
        } catch (DataAccessException e) {
            throw new UserNotFoundException("User with such id wasn't found in DB");
        }
    }

    @Override
    public void addFriend(Long userId, Long friendId) {
        findUserById(userId);
        findUserById(friendId);

        String sqlQuery = "INSERT INTO user_friends (user_id, friend_id, status) VALUES (?, ?, ?)";

        jdbcTemplate.update(sqlQuery, userId, friendId, REQUESTED.name());
        log.debug("User with id {} is added as a friend for user with id {}", friendId, userId);
    }

    @Override
    public void removeFriend(Long userId, Long friendId) {
        findUserById(userId);
        findUserById(friendId);

        String sqlQuery = "DELETE FROM user_friends WHERE user_id = ? AND friend_id = ?";

        if (jdbcTemplate.update(sqlQuery, userId, friendId) > 0) {
            log.debug("Users with id {} and id {} are no longer friends", userId, friendId);
        }
    }

    @Override
    public List<User> findAllFriends(Long userId) {
        List<User> friends = findAllFriendsAndStatuses(userId).keySet().stream().map(this::findUserById).collect(Collectors.toList());
        log.debug("Friends quantity is: {}", friends.size());
        return friends;
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
