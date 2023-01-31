package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequestMapping("/films")
@RestController
@Slf4j
public class FilmController {
    private int identifier = 0;
    private final Map<Integer, Film> films = new HashMap<>();

    @GetMapping
    public List<Film> findAll() {
        log.debug("Films quantity is: {}", films.size());
        return new ArrayList<>(films.values());
    }

    @PostMapping
    public Film create(@Valid @RequestBody Film film) {
        film.setId(generateId());
        log.debug("Film is added: {}", film);
        films.put(film.getId(), film);
        return film;
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film film) {
        if (!films.containsKey(film.getId())) {
            throw new ValidationException("No film with such id");
        }
        log.debug("Film is updated: {}", film);
        films.put(film.getId(), film);
        return film;
    }

    private int generateId() {
        return ++identifier;
    }
}
