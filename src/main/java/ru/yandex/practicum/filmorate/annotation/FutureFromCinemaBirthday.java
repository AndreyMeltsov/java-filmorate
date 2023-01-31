package ru.yandex.practicum.filmorate.annotation;

import javax.validation.Constraint;
import javax.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({ FIELD })
@Retention(RUNTIME)
@Constraint(validatedBy = FutureFromCinemaBirthdayValidator.class)
@Documented
public @interface FutureFromCinemaBirthday {
    String message() default "{Release date can't be earlier than 28 December 1895}";

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };
}
