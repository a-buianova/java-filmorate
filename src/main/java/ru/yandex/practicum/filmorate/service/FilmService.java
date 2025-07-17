package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.RatingMpa;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FilmService {

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final MpaService mpaService;
    private final GenreService genreService;

    public Film add(Film film) {
        RatingMpa rating = mpaService.findById(film.getMpa().getId());
        film.setMpa(rating);
        if (film.getGenres() != null) {
            film.setGenres(film.getGenres().stream()
                    .map(g -> genreService.findById(g.getId()))
                    .distinct()
                    .collect(Collectors.toList()));
        }
        return filmStorage.add(film);
    }

    public Film update(Film film) {
        findById(film.getId()); // проверка, что фильм существует
        RatingMpa rating = mpaService.findById(film.getMpa().getId());
        film.setMpa(rating);
        if (film.getGenres() != null) {
            film.setGenres(film.getGenres().stream()
                    .distinct()
                    .map(g -> genreService.findById(g.getId()))
                    .collect(Collectors.toList()));
        }
        return filmStorage.update(film);
    }

    public Film findById(Long id) {
        return filmStorage.findById(id)
                .orElseThrow(() -> new NotFoundException("Фильм с id " + id + " не найден."));
    }

    public List<Film> findAll() {
        return filmStorage.findAll();
    }

    public void addLike(Long filmId, Long userId) {
        findById(filmId);
        userStorage.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден."));
        filmStorage.addLike(filmId, userId);
    }

    public void removeLike(Long filmId, Long userId) {
        findById(filmId);
        userStorage.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден."));
        filmStorage.removeLike(filmId, userId);
    }

    public List<Film> getPopular(int count) {
        return filmStorage.getPopular(count);
    }
}