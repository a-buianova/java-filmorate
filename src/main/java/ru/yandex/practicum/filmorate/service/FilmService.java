package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.RatingMpa;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.GenreStorage;
import ru.yandex.practicum.filmorate.storage.MpaStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FilmService {

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final MpaStorage mpaStorage;
    private final GenreStorage genreStorage;

    public Film add(Film film) {
        RatingMpa rating = mpaStorage.findById(film.getMpa().getId())
                .orElseThrow(() -> new NotFoundException("MPA с id " + film.getMpa().getId() + " не найден."));
        film.setMpa(rating);

        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            Set<Integer> requestedIds = film.getGenres().stream()
                    .map(Genre::getId)
                    .collect(Collectors.toSet());

            List<Genre> foundGenres = genreStorage.findAllByIds(requestedIds);
            Set<Integer> foundIds = foundGenres.stream()
                    .map(Genre::getId)
                    .collect(Collectors.toSet());

            Set<Integer> missingIds = new HashSet<>(requestedIds);
            missingIds.removeAll(foundIds);

            if (!missingIds.isEmpty()) {
                throw new NotFoundException("Жанры с id " + missingIds + " не найдены.");
            }

            film.setGenres(foundGenres);
        }

        return filmStorage.add(film);
    }

    public Film update(Film update) {
        Film film = findById(update.getId());

        if (update.getName() != null && !update.getName().isBlank()) {
            film.setName(update.getName());
        }
        if (update.getDescription() != null) {
            film.setDescription(update.getDescription());
        }
        if (update.getReleaseDate() != null) {
            film.setReleaseDate(update.getReleaseDate());
        }
        if (update.getDuration() > 0) {
            film.setDuration(update.getDuration());
        }
        if (update.getMpa() != null) {
            RatingMpa rating = mpaStorage.findById(update.getMpa().getId())
                    .orElseThrow(() -> new NotFoundException("MPA с id " + update.getMpa().getId() + " не найден."));
            film.setMpa(rating);
        }
        if (update.getGenres() != null) {
            Set<Integer> ids = update.getGenres().stream()
                    .map(g -> g.getId())
                    .collect(Collectors.toSet());
            film.setGenres(genreStorage.findAllByIds(ids));
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