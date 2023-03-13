package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.GenreService;

import java.util.List;

@RequestMapping("/genres")
@RestController
@RequiredArgsConstructor
public class GenreController {
    private final GenreService genreService;

    @GetMapping
    public List<Genre> findAllGenres() {
        return genreService.findAllGenres();
    }

    @GetMapping("/{id}")
    public Genre findGenreById(@PathVariable Integer id) {
        return genreService.findGenreById(id);
    }
}