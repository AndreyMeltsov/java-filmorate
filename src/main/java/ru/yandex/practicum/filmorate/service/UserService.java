package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.FriendshipStatus;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {
    private final UserStorage userStorage;

    public List<User> findAll() {
        return userStorage.findAll();
    }

    public User create(User user) {
        return userStorage.create(user);
    }

    public User update(User user) {
        return userStorage.update(user);
    }

    public User findUserById(Long id) {
        return userStorage.findUserById(id);
    }

    public void addFriend(Long userId, Long friendId) {
        userStorage.addFriend(userId, friendId);
    }

    public void removeFriend(Long userId, Long friendId) {
        userStorage.removeFriend(userId, friendId);
    }

    public List<User> findAllFriends(Long userId) {
        return userStorage.findAllFriends(userId);
    }

    public List<User> findCommonFriends(Long id, Long otherId) {
        Map<Long, FriendshipStatus> friendIds = userStorage.findUserById(id).getFriendsIdsAndStatus();
        Map<Long, FriendshipStatus> otherFriendIds = userStorage.findUserById(otherId).getFriendsIdsAndStatus();
        List<User> commonFriends = friendIds.keySet().stream()
                .filter(otherFriendIds::containsKey)
                .map(userStorage::findUserById)
                .collect(Collectors.toList());
        log.debug("Common friends quantity is: {}", commonFriends.size());
        return commonFriends;
    }
}
