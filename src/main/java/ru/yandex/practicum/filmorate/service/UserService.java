package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserService {
    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public void addFriend(Long userId, Long friendId) {
        User user = userStorage.findUserById(userId);
        User friend = userStorage.findUserById(friendId);
        Set<Long> userFriendsIds = user.getFriendIds();
        Set<Long> friendFriendsIds = friend.getFriendIds();
        userFriendsIds.add(friendId);
        friendFriendsIds.add(userId);
        log.debug("User {} added as a friend for user {}", friend, user);
    }

    public void removeFriend(Long userId, Long friendId) {
        User user = userStorage.findUserById(userId);
        User friend = userStorage.findUserById(friendId);
        Set<Long> userFriendsIds = user.getFriendIds();
        Set<Long> friendFriendsIds = friend.getFriendIds();
        userFriendsIds.remove(friendId);
        friendFriendsIds.remove(userId);
        log.debug("Users {} and {} are no longer friends", friend, user);
    }

    public List<User> findAllFriends(Long userId) {
        Set<Long> friendsIds = userStorage.findUserById(userId).getFriendIds();
        List<User> friends = friendsIds.stream()
                .map(userStorage::findUserById)
                .collect(Collectors.toList());
        log.debug("Friends quantity is: {}", friends.size());
        return friends;
    }

    public List<User> findCommonFriends(Long id, Long otherId) {
        Set<Long> friendIds = userStorage.findUserById(id).getFriendIds();
        Set<Long> otherFriendIds = userStorage.findUserById(otherId).getFriendIds();
        List<User> commonFriends = friendIds.stream()
                .filter(otherFriendIds::contains)
                .map(userStorage::findUserById)
                .collect(Collectors.toList());
        log.debug("Common friends quantity is: {}", commonFriends.size());
        return commonFriends;
    }
}
