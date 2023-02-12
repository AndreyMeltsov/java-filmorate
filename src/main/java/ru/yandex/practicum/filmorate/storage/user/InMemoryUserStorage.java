package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class InMemoryUserStorage implements UserStorage {
    private Long identifier = 0L;
    private final Map<Long, User> users = new HashMap<>();

    @Override
    public List<User> findAll() {
        log.debug("Users quantity is: {}", users.size());
        return new ArrayList<>(users.values());
    }

    @Override
    public User create(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        user.setId(generateId());
        users.put(user.getId(), user);
        log.debug("User is created: {}", user);
        return user;
    }

    @Override
    public User update(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        if (!users.containsKey(user.getId())) {
            throw new UserNotFoundException("No user with such id");
        }
        users.put(user.getId(), user);
        log.debug("User is updated: {}", user);
        return user;
    }

    @Override
    public User findUserById(Long id) {
        if (!users.containsKey(id)) {
            throw new UserNotFoundException("No user with such id");
        }
        log.debug("User {} was found", users.get(id));
        return users.get(id);
    }

    private Long generateId() {
        return ++identifier;
    }
}
