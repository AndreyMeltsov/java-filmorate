package ru.yandex.practicum.filmorate.storage.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;
    private final GenreStorage genreStorage;

    @Override
    public List<Film> findAll() {
        String sql = "SELECT * FROM films f JOIN ratings r ON f.rating_id = r.id";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeFilm(rs));
    }

    @Override
    public Film create(Film film) {
        String sqlForFilms = "INSERT INTO films(name, description, release_date, duration, rating_id) " +
                "VALUES (?, ?, ?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlForFilms, new String[]{"id"});
            stmt.setString(1, film.getName());
            stmt.setString(2, film.getDescription());
            stmt.setDate(3, Date.valueOf(film.getReleaseDate()));
            stmt.setInt(4, film.getDuration());
            stmt.setInt(5, film.getMpa().getId());
            return stmt;
        }, keyHolder);

        Long filmId = Objects.requireNonNull(keyHolder.getKey()).longValue();

        if (film.getGenres() != null) {
            updateGenresByFilmId(film.getGenres(), filmId);
        }

        film.setId(Objects.requireNonNull(keyHolder.getKey()).longValue());
        return film;
    }

    @Override
    public Film update(Film film) {
        String sqlQuery = "MERGE INTO films KEY (id) VALUES (?, ?, ?, ?, ?, ?)";

        jdbcTemplate.update(sqlQuery,
                film.getId(),
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().getId());

        if (film.getGenres() != null) {
            String sqlDeleteFromGenreLine = "DELETE FROM genre_line WHERE film_id = ?";
            jdbcTemplate.update(sqlDeleteFromGenreLine, film.getId());

            updateGenresByFilmId(film.getGenres(), film.getId());
        }
        film = findFilmById(film.getId());
        return film;
    }

    @Override
    public Film findFilmById(Long id) {
        String sqlQuery = "SELECT * FROM films f JOIN ratings r ON f.rating_id = r.id WHERE f.id = ?";
        try {
            return jdbcTemplate.queryForObject(sqlQuery, (rs, rowNum) -> makeFilm(rs, id), id);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Film with such id wasn't found in DB");
        }
    }

    @Override
    public void addLike(Long filmId, Long userId) {
        String sqlQuery = "INSERT INTO likes (film_id, user_id) VALUES (?, ?)";
        jdbcTemplate.update(sqlQuery, filmId, userId);
    }

    @Override
    public Integer removeLike(Long filmId, Long userId) {
        String sqlQuery = "DELETE FROM likes WHERE film_id = ? AND user_id = ?";
        return jdbcTemplate.update(sqlQuery, filmId, userId);
    }

    private List<Long> findLikesByFilmId(Long filmId) {
        String sql = "SELECT user_id FROM likes WHERE film_id = ?";
        return jdbcTemplate.query(sql, (rs, rowNum) -> rs.getLong("user_id"), filmId);
    }

    private Film makeFilm(ResultSet rs) throws SQLException {
        return makeFilm(rs, rs.getLong("films.id"));
    }

    private Film makeFilm(ResultSet rs, Long id) throws SQLException {
        String name = rs.getString("films.name");
        String description = rs.getString("films.description");
        LocalDate releaseDate = rs.getDate("films.release_date").toLocalDate();
        int duration = rs.getInt("films.duration");
        List<Long> likes = findLikesByFilmId(id);
        Mpa mpa = new Mpa(rs.getInt("films.rating_id"), rs.getString("ratings.name"));
        List<Genre> genres = genreStorage.findGenresByFilmId(id);

        return new Film(id, name, description, releaseDate, duration, likes, genres, mpa);
    }

    private void updateGenresByFilmId(List<Genre> genres, Long filmId) {
        String sql = "INSERT INTO genre_line(film_id, genre_id) VALUES (?, ?)";

        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                Genre genre = genres.get(i);
                ps.setLong(1, filmId);
                ps.setInt(2, genre.getId());
            }

            @Override
            public int getBatchSize() {
                return genres.size();
            }
        });
    }
}
