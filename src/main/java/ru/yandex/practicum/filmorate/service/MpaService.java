package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.MpaStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MpaService {
    private final MpaStorage mpaStorage;

    public List<Mpa> findAllRatings() {
        return mpaStorage.findAllRatings();
    }

    public Mpa findRatingById(Integer ratingId) {
        return mpaStorage.findRatingById(ratingId);
    }
}
