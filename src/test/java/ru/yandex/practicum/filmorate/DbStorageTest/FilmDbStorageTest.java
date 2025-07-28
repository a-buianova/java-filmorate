package ru.yandex.practicum.filmorate.DbStorageTest;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.RatingMpa;
import ru.yandex.practicum.filmorate.storage.DbStorage.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.DbStorage.GenreDbStorage;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@Import({FilmDbStorage.class, GenreDbStorage.class})
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class FilmDbStorageTest {

    private final FilmDbStorage filmStorage;

    @Test
    @DisplayName("Создание и получение фильма по ID")
    void addAndGetFilm() {
        Film film = makeFilm("The Matrix", "Sci-fi classic", 136);
        Film saved = filmStorage.add(film);

        assertThat(saved.getId()).isNotNull();
        assertThat(filmStorage.findById(saved.getId()))
                .isPresent()
                .hasValueSatisfying(f ->
                        assertThat(f)
                                .hasFieldOrPropertyWithValue("name", "The Matrix")
                                .hasFieldOrPropertyWithValue("description", "Sci-fi classic")
                                .hasFieldOrPropertyWithValue("duration", 136)
                                .extracting(Film::getMpa)
                                .extracting(RatingMpa::getId)
                                .isEqualTo(1)
                );
    }

    @Test
    @DisplayName("Обновление фильма")
    void updateFilm() {
        Film film = filmStorage.add(makeFilm("Old Title", "Old desc", 90));
        film.setName("New Title");
        film.setDescription("New desc");
        film.setDuration(100);
        film.setMpa(new RatingMpa(2, "PG"));

        Film updated = filmStorage.update(film);

        assertThat(updated.getName()).isEqualTo("New Title");
        assertThat(updated.getMpa().getId()).isEqualTo(2);
    }

    @Test
    @DisplayName("Получение всех фильмов")
    void getAllFilms() {
        filmStorage.add(makeFilm("Film 1", "Desc 1", 120));
        filmStorage.add(makeFilm("Film 2", "Desc 2", 130));

        List<Film> films = filmStorage.findAll();

        assertThat(films).hasSizeGreaterThanOrEqualTo(2);
    }

    private Film makeFilm(String name, String desc, int duration) {
        return new Film(
                null,
                name,
                desc,
                LocalDate.of(2000, 1, 1),
                duration,
                new RatingMpa(1, "G"),
                null,
                null
        );
    }
}