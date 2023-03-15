package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {
    private final UserStorage userStorage;

    public List<User> findAll() {
        List<User> users = userStorage.findAll();
        log.debug("Users quantity is: {}", users.size());
        return users;
    }

    public User create(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        user = userStorage.create(user);
        log.debug("User is added: {}", user);
        return user;
    }

    public User update(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        user = userStorage.update(user);
        log.debug("User is updated: {}", user);
        return user;
    }

    public User findUserById(Long id) {
        User user = userStorage.findUserById(id);
        log.debug("User is found in DB: {}", user);
        return user;
    }

    public void addFriend(Long userId, Long friendId) {
        userStorage.addFriend(userId, friendId);
        log.debug("User with id {} is added as a friend for user with id {}", friendId, userId);
    }

    public void removeFriend(Long userId, Long friendId) {
        int rows = userStorage.removeFriend(userId, friendId);
        if (rows > 0) {
            log.debug("Users with id {} and id {} are no longer friends", userId, friendId);
        }
    }

    public List<User> findAllFriends(Long userId) {
        List<User> friends = userStorage.findAllFriends(userId);
                log.debug("Friends quantity is: {}", friends.size());
        return friends;
    }

    public List<User> findCommonFriends(Long id, Long otherId) {
        List<User> commonFriends = userStorage.findCommonFriends(id, otherId);
        log.debug("Common friends quantity is: {}", commonFriends.size());
        return commonFriends;
    }
}
