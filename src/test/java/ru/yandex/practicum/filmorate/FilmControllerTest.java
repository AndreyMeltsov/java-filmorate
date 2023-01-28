package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.Film;

import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FilmControllerTest {

    @Test
    void createTest() {
//        URI url = URI.create("http://localhost:8080/films/");
//        Film film = new Film(1, " ", "dfb", LocalDate.of(1250, 12, 20), 120);
//        String json = gson.toJson(task);
//        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
//        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();
//        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
//
//        assertEquals(response.body(), "Task is created/updated successfully!", "Wrong response body.");
//        assertEquals(response.statusCode(), 200, "Wrong status code.");
    }

    @Test
    void updateTest() {

    }
}
