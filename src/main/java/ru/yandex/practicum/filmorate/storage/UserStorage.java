package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserStorage {
    List<User> findAll();

    User create(User user);

    User update(User user);

    User findUserById(Long id);

    void addFriend(Long userId, Long friendId);

    Integer removeFriend(Long userId, Long friendId);

    List<User> findAllFriends(Long userId);

    List<User> findCommonFriends(Long id, Long otherId);
}
