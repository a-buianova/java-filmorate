package ru.yandex.practicum.filmorate.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import ru.yandex.practicum.filmorate.model.FilmReleaseDate;

import java.time.LocalDate;

public class FilmReleaseDateValidator
        implements ConstraintValidator<FilmReleaseDate, LocalDate> {

    private static final LocalDate MIN_DATE = LocalDate.of(1895, 12, 28);

    @Override
    public boolean isValid(LocalDate value, ConstraintValidatorContext ctx) {
        return value != null && !value.isBefore(MIN_DATE);
    }
}