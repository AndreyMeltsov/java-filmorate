package ru.yandex.practicum.filmorate.annotation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;

public class FutureFromCinemaBirthdayValidator implements ConstraintValidator<FutureFromCinemaBirthday, LocalDate> {
    private static final LocalDate CINEMA_BIRTHDAY = LocalDate.of(1895, 12, 28);

    @Override
    public boolean isValid(LocalDate value, ConstraintValidatorContext context) {
        return value.isAfter(CINEMA_BIRTHDAY);
    }
}
