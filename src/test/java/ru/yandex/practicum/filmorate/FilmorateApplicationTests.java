package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.FriendshipStatus;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.GenreStorage;
import ru.yandex.practicum.filmorate.storage.MpaStorage;
import ru.yandex.practicum.filmorate.storage.impl.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.impl.UserDbStorage;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class FilmorateApplicationTests {
    private final UserDbStorage userStorage;
    private final FilmDbStorage filmStorage;
    private final GenreStorage genreStorage;
    private final MpaStorage mpaStorage;

    @Order(1)
    @Test
    void testCreateUser() {
        User user = new User(
                "meltsov",
                "",
                "mail@mail.ru",
                LocalDate.of(1984, 12, 15));
        userStorage.create(user);
        User actualUser = userStorage.findUserById(1L);

        assertThat(actualUser.getEmail()).isEqualTo("mail@mail.ru");
    }

    @Order(2)
    @Test
    void testUpdateUser() {
        User newUser = new User(
                "pupkin",
                "Vasiliy",
                "yandex@yandex.ru",
                LocalDate.of(1993, 12, 15));
        newUser.setId(1L);
        userStorage.update(newUser);
        User actualUser = userStorage.findUserById(1L);

        assertThat(actualUser.getLogin()).isEqualTo("pupkin");
        assertThat(actualUser.getName()).isEqualTo("Vasiliy");
        assertThat(actualUser.getBirthday()).isEqualTo(LocalDate.of(1993, 12, 15));
    }

    @Order(3)
    @Test
    void testFindUserById() {
        Optional<User> userOptional = Optional.of(userStorage.findUserById(1L));

        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(user ->
                        assertThat(user).hasFieldOrPropertyWithValue("id", 1L)
                );
    }

    @Order(4)
    @Test
    void testFindAllUsers() {
        List<User> users = userStorage.findAll();

        assertThat(users).hasSize(1);
    }

    @Order(5)
    @Test
    void testAddFriend() {
        User user = new User(
                "meltsov",
                "",
                "mail@mail.ru",
                LocalDate.of(1984, 12, 15));
        userStorage.create(user);
        userStorage.addFriend(1L, 2L);

        assertThat(userStorage.findUserById(1L).getFriendsIdsAndStatus())
                .containsKey(2L)
                .containsValue(FriendshipStatus.REQUESTED);
    }

    @Order(6)
    @Test
    void testFindAllFriends() {
        User user = new User(
                "gubkin",
                "Kolya",
                "google@gmail.com",
                LocalDate.of(1965, 12, 11));
        userStorage.create(user);
        userStorage.addFriend(1L, 3L);

        assertThat(userStorage.findAllFriends(1L))
                .hasSize(2)
                .contains(userStorage.findUserById(3L));
    }

    @Order(7)
    @Test
    void testRemoveFriend() {
        userStorage.removeFriend(1L, 3L);

        assertThat(userStorage.findAllFriends(1L))
                .hasSize(1);
    }

    @Order(8)
    @Test
    void testCreateFilm() {
        Film film = new Film(
                "Terminator",
                "Robot from the future tries to kill the head of rebels mother",
                LocalDate.of(1984, 5, 7),
                97,
                null,
                Arrays.asList(new Genre(4), new Genre(6)),
                new Mpa(4));
        filmStorage.create(film);
        Film actualFilm = filmStorage.findFilmById(1L);

        assertThat(actualFilm.getName()).isEqualTo("Terminator");
    }

    @Order(9)
    @Test
    void testUpdateFilm() {
        Film film = new Film(
                "Terminator 2",
                "Robot from the future tries to protect the head of rebels mother",
                LocalDate.of(1992, 10, 7),
                102,
                null,
                Arrays.asList(new Genre(4), new Genre(6)),
                new Mpa(4));
        film.setId(1L);
        filmStorage.update(film);
        Film actualFilm = filmStorage.findFilmById(1L);

        assertThat(actualFilm.getName()).isEqualTo("Terminator 2");
        assertThat(actualFilm.getDescription()).isEqualTo("Robot from the future tries to protect the head of rebels mother");
        assertThat(actualFilm.getReleaseDate()).isEqualTo(LocalDate.of(1992, 10, 7));
    }

    @Order(10)
    @Test
    void testFindFilmById() {
        Optional<Film> filmOptional = Optional.of(filmStorage.findFilmById(1L));

        assertThat(filmOptional)
                .isPresent()
                .hasValueSatisfying(film ->
                        assertThat(film).hasFieldOrPropertyWithValue("id", 1L)
                );
    }

    @Order(11)
    @Test
    void testFindAllFilms() {
        List<Film> films = filmStorage.findAll();

        assertThat(films).hasSize(1);
    }

    @Order(12)
    @Test
    void testFindAllGenres() {
        List<Genre> genres = genreStorage.findAllGenres();

        assertThat(genres).hasSize(6);
    }

    @Order(13)
    @Test
    void testFindGenreById() {
        Genre actualGenre = genreStorage.findGenreById(3);
        assertThat(actualGenre.getName()).isEqualTo("Мультфильм");
    }

    @Order(14)
    @Test
    void testFindAllRatings() {
        List<Mpa> ratings = mpaStorage.findAllRatings();

        assertThat(ratings).hasSize(5);
    }

    @Order(15)
    @Test
    void testFindRatingById() {
        Mpa actualRating = mpaStorage.findRatingById(3);
        assertThat(actualRating.getName()).isEqualTo("PG-13");
    }

    @Order(16)
    @Test
    void testAddLike() {
        filmStorage.addLike(1L, 1L);
        filmStorage.addLike(1L, 2L);

        assertThat(filmStorage.findFilmById(1L).getLikesIds())
                .hasSize(2)
                .contains(1L);
    }

    @Order(17)
    @Test
    void testRemoveLike() {
        filmStorage.removeLike(1L, 1L);

        assertThat(filmStorage.findFilmById(1L).getLikesIds())
                .hasSize(1)
                .doesNotContain(1L);
    }
}
