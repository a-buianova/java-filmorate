package ru.yandex.practicum.filmorate.ValidationTest;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.RatingMpa;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class FilmValidationTest {

    private static Validator validator;

    @BeforeAll
    static void setUp() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    @DisplayName("Дата релиза раньше 1895-12-28 не проходит валидацию")
    void earlyReleaseDateFails() {
        Film film = validFilm();
        film.setReleaseDate(LocalDate.of(1895, 12, 27));
        assertThat(validator.validate(film)).isNotEmpty();
    }

    @Test
    @DisplayName("Дата релиза null — не проходит валидацию")
    void nullReleaseDateFails() {
        Film film = validFilm();
        film.setReleaseDate(null);
        assertThat(validator.validate(film)).isNotEmpty();
    }

    @Test
    @DisplayName("Название пустое — не проходит валидацию")
    void blankNameFails() {
        Film film = validFilm();
        film.setName(" ");
        assertThat(validator.validate(film)).isNotEmpty();
    }

    @Test
    @DisplayName("Название null — не проходит валидацию")
    void nullNameFails() {
        Film film = validFilm();
        film.setName(null);
        assertThat(validator.validate(film)).isNotEmpty();
    }

    @Test
    @DisplayName("Описание длиной 200 символов проходит валидацию")
    void description200Ok() {
        Film film = validFilm();
        film.setDescription("a".repeat(200));
        assertThat(validator.validate(film)).isEmpty();
    }

    @Test
    @DisplayName("Описание длиной 201 символ не проходит валидацию")
    void description201Fails() {
        Film film = validFilm();
        film.setDescription("a".repeat(201));
        assertThat(validator.validate(film)).isNotEmpty();
    }

    @Test
    @DisplayName("Продолжительность ≤ 0 — не проходит валидацию")
    void nonPositiveDurationFails() {
        Film film = validFilm();
        film.setDuration(0);
        assertThat(validator.validate(film)).isNotEmpty();
    }

    @Test
    @DisplayName("MPA null — не проходит валидацию")
    void nullMpaFails() {
        Film film = validFilm();
        film.setMpa(null);
        assertThat(validator.validate(film)).isNotEmpty();
    }

    @Test
    @DisplayName("Корректный фильм проходит валидацию")
    void validFilmOk() {
        Film film = validFilm();
        assertThat(validator.validate(film)).isEmpty();
    }

    private Film validFilm() {
        Film film = new Film();
        film.setName("Matrix");
        film.setDescription("Neo chooses pill");
        film.setReleaseDate(LocalDate.of(1999, 3, 31));
        film.setDuration(136);
        film.setMpa(new RatingMpa(1, "G"));
        return film;
    }

    @Test
    @DisplayName("Описание null — проходит валидацию (если допускается)")
    void nullDescriptionOk() {
        Film film = validFilm();
        film.setDescription(null);
        assertThat(validator.validate(film)).isEmpty();
    }

    @Test
    @DisplayName("Название из одного символа проходит валидацию")
    void nameOneCharOk() {
        Film film = validFilm();
        film.setName("A");
        assertThat(validator.validate(film)).isEmpty();
    }

    @Test
    @DisplayName("Продолжительность 1 — проходит валидацию")
    void durationOneOk() {
        Film film = validFilm();
        film.setDuration(1);
        assertThat(validator.validate(film)).isEmpty();
    }
}