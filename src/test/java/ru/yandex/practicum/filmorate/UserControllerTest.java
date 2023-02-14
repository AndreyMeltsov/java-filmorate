package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UserControllerTest {
    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void whenCreateAndUpdateUserWithNullName_thenNameBecomesSameAsLogin() {
        User user = new User(0L,"mail@mail.ru", "meltsov", null, LocalDate.of(1984, 12, 15));

        User actualUser = restTemplate.postForObject("/users", user, User.class);

        assertThat(actualUser.getName(), is("meltsov"));

        User updatedUser = new User(actualUser.getId(), "mail@yandex.ru", "meltsov", "",
                LocalDate.of(2000, 12, 15));
        HttpEntity<User> entity = new HttpEntity<>(updatedUser);

        ResponseEntity<User> response = restTemplate.exchange("/users", HttpMethod.PUT, entity, User.class);

        assertThat(response.getBody().getEmail(), is("mail@yandex.ru"));
        assertThat(response.getBody().getName(), is("meltsov"));
    }
}
