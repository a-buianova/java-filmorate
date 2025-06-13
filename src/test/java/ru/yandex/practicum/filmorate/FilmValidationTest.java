package ru.yandex.practicum.filmorate;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class FilmValidationTest {

    private static Validator validator;

    @BeforeAll
    static void setUp() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    @DisplayName("releaseDate раньше 1895-12-28 не проходит")
    void earlyReleaseDateFails() {
        Film f = validFilm();
        f.setReleaseDate(LocalDate.of(1895, 12, 27));

        assertThat(validator.validate(f)).isNotEmpty();
    }

    @Test
    @DisplayName("releaseDate null не проходит (@FilmReleaseDate)")
    void nullReleaseDateFails() {
        Film f = validFilm();
        f.setReleaseDate(null);

        assertThat(validator.validate(f)).isNotEmpty();
    }

    @Test
    @DisplayName("duration ≤ 0 не проходит")
    void nonPositiveDurationFails() {
        Film f = validFilm();
        f.setDuration(0);

        assertThat(validator.validate(f)).isNotEmpty();
    }

    @Test
    @DisplayName("description 200 символов проходит")
    void description200Ok() {
        Film f = validFilm();
        f.setDescription("a".repeat(200));

        assertThat(validator.validate(f)).isEmpty();
    }

    @Test
    @DisplayName("description 201 символ не проходит")
    void description201Fails() {
        Film f = validFilm();
        f.setDescription("a".repeat(201));

        assertThat(validator.validate(f)).isNotEmpty();
    }

    @Test
    @DisplayName("пустое name не проходит")
    void blankNameFails() {
        Film f = validFilm();
        f.setName(" ");

        assertThat(validator.validate(f)).isNotEmpty();
    }

    private Film validFilm() {
        Film f = new Film();
        f.setName("Matrix");
        f.setDescription("Neo chooses pill");
        f.setDuration(136);
        f.setReleaseDate(LocalDate.of(1999, 3, 31));
        return f;
    }
}