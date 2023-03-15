package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class GenreService {
    private final GenreStorage genreStorage;

    public List<Genre> findAllGenres() {
        List<Genre> genres = genreStorage.findAllGenres();
        log.debug("Genres quantity is: {}", genres.size());
        return genres;
    }

    public Genre findGenreById(Integer genreId) {
        Genre genre = genreStorage.findGenreById(genreId);
        log.debug("Genre {} was found", genre);
        return genre;
    }
}
