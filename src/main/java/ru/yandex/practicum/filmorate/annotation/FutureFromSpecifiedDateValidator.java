package ru.yandex.practicum.filmorate.annotation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;

public class FutureFromSpecifiedDateValidator implements ConstraintValidator<FutureFromSpecifiedDate, LocalDate> {

    @Override
    public boolean isValid(LocalDate value, ConstraintValidatorContext context) {
        return value.isAfter(LocalDate.of(1895, 12, 28));
    }
}
