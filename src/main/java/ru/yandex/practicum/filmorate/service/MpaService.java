package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.MpaStorage;

import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class MpaService {
    private final MpaStorage mpaStorage;

    public List<Mpa> findAllRatings() {
        List<Mpa> mpas = mpaStorage.findAllRatings();
        log.debug("MPA quantity is: {}", mpas.size());
        return mpas;
    }

    public Mpa findRatingById(Integer ratingId) {
        Mpa mpa = mpaStorage.findRatingById(ratingId);
        log.debug("MPA {} was found", Objects.requireNonNull(mpa).getName());
        return mpa;
    }
}
