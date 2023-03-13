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

    public Film(String name,
                String description,
                LocalDate releaseDate,
                int duration,
                List<Long> likesIds,
                List<Genre> genres,
                Mpa mpa) {
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
        this.likesIds = likesIds;
        this.genres = genres;
        this.mpa = mpa;
    }

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
