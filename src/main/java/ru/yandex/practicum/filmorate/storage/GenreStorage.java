package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface GenreStorage {
    Optional<Genre> findById(int id);

    Collection<Genre> findAll();

    List<Genre> findGenresByFilmId(Long filmId);
}