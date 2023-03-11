package ru.yandex.practicum.filmorate.storage.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.GenreNotFoundException;
import ru.yandex.practicum.filmorate.exception.RatingNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@Component("filmDbStorage")
@Slf4j
@Primary
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;
    private final UserStorage userStorage;

    public FilmDbStorage(JdbcTemplate jdbcTemplate, UserStorage userStorage) {
        this.jdbcTemplate = jdbcTemplate;
        this.userStorage = userStorage;
    }

    @Override
    public List<Film> findAll() {
        List<Film> films = jdbcTemplate.query("SELECT * FROM films", (rs, rowNum) -> makeFilm(rs));
        log.debug("Films quantity is: {}", films.size());
        return films;
    }

    @Override
    public Film create(Film film) {
        String sqlForFilms = "insert into films(name, description, release_date, duration, rating_id) " +
                "values (?, ?, ?, ?, ?)";

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
            String sqlForGenreLine = "insert into genre_line(film_id, genre_id) values (?, ?)";

            for (Film.Genre genre : film.getGenres()) {
                jdbcTemplate.update(sqlForGenreLine, filmId, genre.getId());
            }
        }

        film.setId(Objects.requireNonNull(keyHolder.getKey()).longValue());

        log.debug("Film is added: {}", film);
        return film;
    }

    @Override
    public Film update(Film film) {
        findFilmById(film.getId());
        String sqlQuery = "merge into films key (id) values (?, ?, ?, ?, ?, ?)";

        jdbcTemplate.update(sqlQuery,
                film.getId(),
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().getId());

        if (film.getGenres() != null) {
            String sqlDeleteFromGenreLine = "delete from genre_line where film_id = ?";

            jdbcTemplate.update(sqlDeleteFromGenreLine, film.getId());

            String sqlForGenreLine = "insert into genre_line(film_id, genre_id) values (?, ?)";

            for (Film.Genre genre : film.getGenres()) {
                jdbcTemplate.update(sqlForGenreLine, film.getId(), genre.getId());
            }
        }
        film = findFilmById(film.getId());
        log.debug("Film is updated: {}", film);
        return film;
    }

    @Override
    public Film findFilmById(Long id) {
        String sqlQuery = "SELECT * FROM films WHERE id = ?";
        try {
            Film film = jdbcTemplate.queryForObject(sqlQuery, (rs, rowNum) -> makeFilm(rs, id), id);
            log.debug("Film is found in DB: {}", film);
            return film;
        } catch (DataAccessException e) {
            throw new FilmNotFoundException("Film with such id wasn't found in DB");
        }
    }

    @Override
    public List<Film.Genre> findAllGenres() {
        String sql = "SELECT * FROM genres ORDER BY id";
        List<Film.Genre> genres = jdbcTemplate.query(sql, (rs, rowNum) -> makeGenre(rs));
        log.debug("Genres quantity is: {}", genres.size());
        return genres;
    }

    @Override
    public Film.Genre findGenreById(Integer genreId) {
        String sql = "SELECT * FROM genres WHERE id = ?";
        try {
            Film.Genre genre = jdbcTemplate.queryForObject(sql, (rs, rowNum) -> makeGenre(rs), genreId);
            log.debug("Genre {} was found", genre);
            return genre;
        } catch (DataAccessException e) {
            throw new GenreNotFoundException("Genre with such id wasn't found");
        }
    }

    @Override
    public List<Film.Mpa> findAllRatings() {
        String sql = "SELECT * FROM ratings ORDER BY id";
        List<Film.Mpa> mpas = jdbcTemplate.query(sql, (rs, rowNum) -> makeMpa(rs));
        log.debug("MPA quantity is: {}", mpas.size());
        return mpas;
    }

    @Override
    public Film.Mpa findRatingById(Integer ratingId) {
        String sql = "SELECT * FROM ratings WHERE id = ?";
        try {
            Film.Mpa mpa = jdbcTemplate.queryForObject(sql, (rs, rowNum) -> makeMpa(rs), ratingId);
            log.debug("MPA {} was found", Objects.requireNonNull(mpa).getName());
            return mpa;
        } catch (DataAccessException e) {
            throw new RatingNotFoundException("Rating with such id wasn't found");
        }
    }

    @Override
    public void addLike(Long filmId, Long userId) {
        findFilmById(filmId);
        userStorage.findUserById(userId);
        String sqlQuery = "INSERT INTO likes (film_id, user_id) VALUES (?, ?)";
        jdbcTemplate.update(sqlQuery, filmId, userId);
        log.debug("User with id {} added a like to film with id {}", userId, filmId);
    }

    @Override
    public void removeLike(Long filmId, Long userId) {
        findFilmById(filmId);
        userStorage.findUserById(userId);
        String sqlQuery = "DELETE FROM likes WHERE film_id = ? AND user_id = ?";
        if (jdbcTemplate.update(sqlQuery, filmId, userId) > 0) {
            log.debug("User with id {} removed a like of film with id {}", userId, filmId);
        }
    }

    private List<Film.Genre> findGenresByFilmId(Long filmId) {
        String sql = "SELECT DISTINCT g.id, g.name FROM genre_line AS gl JOIN genres AS g ON gl.genre_id = g.id " +
                "WHERE film_id = ?";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeGenre(rs), filmId);
    }

    private List<Long> findLikesByFilmId(Long filmId) {
        String sql = "SELECT user_id FROM likes WHERE film_id = ?";
        return jdbcTemplate.query(sql, (rs, rowNum) -> rs.getLong("user_id"), filmId);
    }

    private Film makeFilm(ResultSet rs) throws SQLException {
        Long id = rs.getLong("id");
        String name = rs.getString("name");
        String description = rs.getString("description");
        LocalDate releaseDate = rs.getDate("release_date").toLocalDate();
        int duration = rs.getInt("duration");
        List<Long> likes = findLikesByFilmId(id);
        Film.Mpa mpa = findRatingById(rs.getInt("rating_id"));
        List<Film.Genre> genres = findGenresByFilmId(id);

        return new Film(id, name, description, releaseDate, duration, likes, genres, mpa);
    }

    private Film makeFilm(ResultSet rs, Long id) throws SQLException {
        String name = rs.getString("name");
        String description = rs.getString("description");
        LocalDate releaseDate = rs.getDate("release_date").toLocalDate();
        int duration = rs.getInt("duration");
        List<Long> likes = findLikesByFilmId(id);
        Film.Mpa mpa = findRatingById(rs.getInt("rating_id"));
        List<Film.Genre> genres = findGenresByFilmId(id);

        return new Film(id, name, description, releaseDate, duration, likes, genres, mpa);
    }

    private Film.Genre makeGenre(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String name = rs.getString("name");

        return new Film.Genre(id, name);
    }

    private Film.Mpa makeMpa(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String name = rs.getString("name");

        return new Film.Mpa(id, name);
    }
}
