package ru.yandex.practicum.filmorate.storage.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class InMemoryFilmStorage implements FilmStorage {
    private Long identifier = 0L;
    private final Map<Long, Film> films = new HashMap<>();

    @Override
    public List<Film> findAll() {
        log.debug("Films quantity is: {}", films.size());
        return new ArrayList<>(films.values());
    }

    @Override
    public Film create(Film film) {
        film.setId(generateId());
        films.put(film.getId(), film);
        log.debug("Film is added: {}", film);
        return film;
    }

    @Override
    public Film update(Film film) {
        if (!films.containsKey(film.getId())) {
            throw new FilmNotFoundException("No film with such id");
        }
        films.put(film.getId(), film);
        log.debug("Film is updated: {}", film);
        return film;
    }

    @Override
    public Film findFilmById(Long id) {
        if (!films.containsKey(id)) {
            throw new FilmNotFoundException("No film with such id");
        }
        log.debug("Film {} was found", films.get(id));
        return films.get(id);
    }

    @Override
    public void addLike(Long id, Long userId) { /* TODO document why this method is empty */ }

    @Override
    public void removeLike(Long filmId, Long userId) { /* TODO document why this method is empty */ }

    @Override
    public List<Film.Genre> findAllGenres() {
        return null;
    }

    @Override
    public Film.Genre findGenreById(Integer genreId) {
        return null;
    }

    @Override
    public List<Film.Mpa> findAllRatings() {
        return null;
    }

    @Override
    public Film.Mpa findRatingById(Integer ratingId) {
        return null;
    }


    private Long generateId() {
        return ++identifier;
    }
}
