package ru.yandex.practicum.filmorate;

import jakarta.validation.Validation;
import org.junit.jupiter.api.*;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class FilmValidationTest {

    @Test
    @DisplayName("releaseDate раньше 1895-12-28 не проходит Bean Validation")
    void earlyReleaseDateFails() {
        var film = new Film();
        film.setName("Test");
        film.setDescription("desc");
        film.setDuration(100);
        film.setReleaseDate(LocalDate.of(1800, 1, 1));

        var violations = Validation.buildDefaultValidatorFactory()
                .getValidator().validate(film);

        assertThat(violations).isNotEmpty();
    }

    @Test
    @DisplayName("duration ≤ 0 не проходит Bean Validation")
    void nonPositiveDurationFails() {
        var film = new Film();
        film.setName("Test");
        film.setDescription("desc");
        film.setDuration(0);
        film.setReleaseDate(LocalDate.of(2000, 1, 1));

        var violations = Validation.buildDefaultValidatorFactory()
                .getValidator().validate(film);

        assertThat(violations).isNotEmpty();
    }
}