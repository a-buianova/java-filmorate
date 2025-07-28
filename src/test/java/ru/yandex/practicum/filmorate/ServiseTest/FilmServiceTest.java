package ru.yandex.practicum.filmorate.ServiseTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.RatingMpa;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.GenreStorage;
import ru.yandex.practicum.filmorate.storage.MpaStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.storage.inmemory_hidden.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.inmemory_hidden.InMemoryGenreStorage;
import ru.yandex.practicum.filmorate.storage.inmemory_hidden.InMemoryMpaStorage;
import ru.yandex.practicum.filmorate.storage.inmemory_hidden.InMemoryUserStorage;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class FilmServiceTest {

    private FilmService filmService;

    @BeforeEach
    void setUp() {
        FilmStorage filmStorage = new InMemoryFilmStorage();
        UserStorage userStorage = new InMemoryUserStorage();
        MpaStorage mpaStorage = new InMemoryMpaStorage();
        GenreStorage genreStorage = new InMemoryGenreStorage();

        filmService = new FilmService(filmStorage, userStorage, mpaStorage, genreStorage);
    }

    @Test
    @DisplayName("Добавление и получение фильма")
    void addAndGetFilm() {
        Film film = new Film();
        film.setName("Matrix");
        film.setDescription("Neo chooses pill");
        film.setReleaseDate(LocalDate.of(1999, 3, 31));
        film.setDuration(136);
        film.setMpa(new RatingMpa(1, null));

        Film created = filmService.add(film);
        Film found = filmService.findById(created.getId());

        assertThat(found).isNotNull();
        assertThat(found.getName()).isEqualTo("Matrix");
        assertThat(found.getMpa().getId()).isEqualTo(1);
    }

    @Test
    @DisplayName("Обновление фильма")
    void updateFilm() {
        Film film = new Film();
        film.setName("Matrix");
        film.setDescription("Neo chooses pill");
        film.setReleaseDate(LocalDate.of(1999, 3, 31));
        film.setDuration(136);
        film.setMpa(new RatingMpa(1, null));

        Film created = filmService.add(film);

        Film updated = new Film();
        updated.setId(created.getId());
        updated.setName("Updated Matrix");
        updated.setDescription("Updated desc");
        updated.setReleaseDate(LocalDate.of(2000, 1, 1));
        updated.setDuration(150);
        updated.setMpa(new RatingMpa(2, null));

        Film result = filmService.update(updated);

        assertThat(result.getName()).isEqualTo("Updated Matrix");
        assertThat(result.getMpa().getId()).isEqualTo(2);
    }

    @Test
    @DisplayName("Поиск фильма по ID — NotFoundException")
    void findById_shouldThrowIfNotFound() {
        assertThrows(NotFoundException.class, () -> filmService.findById(999L));
    }

    @Test
    @DisplayName("Получение всех фильмов")
    void getAllFilms() {
        assertThat(filmService.findAll()).isEmpty();

        Film film = new Film();
        film.setName("Matrix");
        film.setDescription("Neo chooses pill");
        film.setReleaseDate(LocalDate.of(1999, 3, 31));
        film.setDuration(136);
        film.setMpa(new RatingMpa(1, null));

        filmService.add(film);

        List<Film> all = filmService.findAll();
        assertThat(all).hasSize(1);
    }

    @Test
    @DisplayName("Популярные фильмы по количеству лайков")
    void getPopularFilms() {
        Film film1 = new Film();
        film1.setName("Film 1");
        film1.setDescription("Desc 1");
        film1.setReleaseDate(LocalDate.of(2001, 1, 1));
        film1.setDuration(100);
        film1.setMpa(new RatingMpa(1, null));

        Film film2 = new Film();
        film2.setName("Film 2");
        film2.setDescription("Desc 2");
        film2.setReleaseDate(LocalDate.of(2002, 2, 2));
        film2.setDuration(120);
        film2.setMpa(new RatingMpa(2, null));

        Film f1 = filmService.add(film1);
        Film f2 = filmService.add(film2);

        f1.addLike(1L);
        f1.addLike(2L);
        f2.addLike(1L);

        List<Film> popular = filmService.getPopular(10);
        assertThat(popular.get(0).getId()).isEqualTo(f1.getId());
    }
}