package ru.yandex.practicum.filmorate.model;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import ru.yandex.practicum.filmorate.validator.FilmReleaseDateValidator;

import java.lang.annotation.*;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = FilmReleaseDateValidator.class)
public @interface FilmReleaseDate {
    String message() default "releaseDate must be ≥ 1895-12-28";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}