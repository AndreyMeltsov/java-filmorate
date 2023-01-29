package ru.yandex.practicum.filmorate;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.User;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UserControllerTest {
    Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
            .create();
    HttpClient client = HttpClient.newHttpClient();
    URI url = URI.create("http://localhost:8080/users");

    @Test
    void createTest() throws IOException, InterruptedException {
        FilmorateApplication.main(null);
        User user = new User(0, "mail@mail.ru", "meltsov", null,
                LocalDate.of(1984, 12, 15));
        String json = gson.toJson(user);
        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .header("Content-Type", "application/json")
                .POST(body)
                .build();
        HttpResponse<String> expectedResponse = client.send(request, HttpResponse.BodyHandlers.ofString());

        User actualUser = new User(1, "mail@mail.ru", "meltsov", "meltsov",
                LocalDate.of(1984, 12, 15));
        String actualResponse = gson.toJson(actualUser);

        assertEquals(expectedResponse.body(), actualResponse, "Wrong response body.");
        assertEquals(expectedResponse.statusCode(), 200, "Wrong status code.");
    }

    @Test
    void updateTest() throws IOException, InterruptedException {
        FilmorateApplication.main(null);
        User user = new User(1, "mail@yandex.ru", "meltsov", null,
                LocalDate.of(2000, 12, 15));
        String json = gson.toJson(user);
        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .header("Content-Type", "application/json")
                .PUT(body)
                .build();
        HttpResponse<String> expectedResponse = client.send(request, HttpResponse.BodyHandlers.ofString());

        User actualUser = new User(1, "mail@yandex.ru", "meltsov", "meltsov",
                LocalDate.of(2000, 12, 15));
        String actualResponse = gson.toJson(actualUser);

        assertEquals(expectedResponse.body(), actualResponse, "Wrong response body.");
        assertEquals(expectedResponse.statusCode(), 200, "Wrong status code.");
    }

    static class LocalDateAdapter extends TypeAdapter<LocalDate> {
        private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        @Override
        public void write(final JsonWriter jsonWriter, final LocalDate localDate) throws IOException {
            jsonWriter.value(localDate != null ? localDate.format(formatter) : null);
        }

        @Override
        public LocalDate read(final JsonReader jsonReader) throws IOException {
            return LocalDate.parse(jsonReader.nextString(), formatter);
        }
    }

}
