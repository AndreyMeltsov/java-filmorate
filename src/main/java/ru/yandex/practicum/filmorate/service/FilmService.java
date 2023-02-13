package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    public List<Film> findAll() {
        return filmStorage.findAll();
    }

    public Film create(Film film) {
        return filmStorage.create(film);
    }

    public Film update(Film film) {
        return filmStorage.update(film);
    }

    public Film findFilmById(Long id) {
        return filmStorage.findFilmById(id);
    }

    public Film addLike(Long id, Long userId) {
        Film film = filmStorage.findFilmById(id);
        User user = userStorage.findUserById(userId);
        film.getLikesIds().add(userId);
        log.debug("User {} added a like to film {}", user, film);
        return film;
    }

    public Film removeLike(Long id, Long userId) {
        Film film = filmStorage.findFilmById(id);
        User user = userStorage.findUserById(userId);
        film.getLikesIds().remove(userId);
        log.debug("User {} removed a like of film {}", user, film);
        return film;
    }

    public List<Film> getMostPopularFilms(Integer count) {
        List<Film> mostPopularFilms = filmStorage.findAll().stream()
                .sorted((x1, x2) -> x2.getLikesIds().size() - x1.getLikesIds().size())
                .limit(count)
                .collect(Collectors.toList());
        log.debug("The most popular films are: {}", mostPopularFilms);
        return mostPopularFilms;
    }
}
