package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.*;

public interface GenreStorage {
    Optional<Genre> findById(int id);

    Collection<Genre> findAll();

    List<Genre> findGenresByFilmId(Long filmId);

    List<Genre> findAllByIds(Set<Integer> ids);

    Map<Long, List<Genre>> findGenresForFilmIds(List<Long> filmIds);

}