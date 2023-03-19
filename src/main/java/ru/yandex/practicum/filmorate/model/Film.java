package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import ru.yandex.practicum.filmorate.annotation.FutureFromCinemaBirthday;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode
public class Film {
    @Setter
    private Long id;

    @NotBlank
    private String name;

    @Size(max = 200)
    private String description;

    @FutureFromCinemaBirthday
    private LocalDate releaseDate;

    @Positive
    private int duration;

    private List<Long> likesIds;

    private List<Genre> genres;

    private Mpa mpa;

}
