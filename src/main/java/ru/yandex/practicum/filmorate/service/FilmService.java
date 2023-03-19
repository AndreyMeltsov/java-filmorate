package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserService userService;

    public List<Film> findAll() {
        List<Film> films = filmStorage.findAll();
        log.debug("Films quantity is: {}", films.size());
        return films;
    }

    public Film create(Film film) {
        film = filmStorage.create(film);
        log.debug("Film is added: {}", film);
        return film;
    }

    public Film update(Film film) {
        findFilmById(film.getId());
        film = filmStorage.update(film);
        log.debug("Film is updated: {}", film);
        return film;
    }

    public Film findFilmById(Long id) {
        Film film = filmStorage.findFilmById(id);
        log.debug("Film is found in DB: {}", film);
        return film;
    }

    public Film addLike(Long id, Long userId) {
        findFilmById(id);
        userService.findUserById(userId);
        filmStorage.addLike(id, userId);
        log.debug("User with id {} added a like to film with id {}", userId, id);
        return filmStorage.findFilmById(id);
    }

    public Film removeLike(Long id, Long userId) {
        findFilmById(id);
        userService.findUserById(userId);
        int rows = filmStorage.removeLike(id, userId);
        if (rows > 0) {
            log.debug("User with id {} removed a like of film with id {}", userId, id);
        }
        return filmStorage.findFilmById(id);
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
