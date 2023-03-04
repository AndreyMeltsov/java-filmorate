package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Past;
import javax.validation.constraints.Pattern;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Getter
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class User {

    @Setter
    private Long id;

    @NotBlank
    @Email
    private String email;

    @NotBlank
    @Pattern(regexp = "^\\w*$")
    private String login;

    @Setter
    private String name;

    @Past
    private LocalDate birthday;

    private final Map<Long, Boolean> friendsIdsAndStatus = new HashMap<>();
}
