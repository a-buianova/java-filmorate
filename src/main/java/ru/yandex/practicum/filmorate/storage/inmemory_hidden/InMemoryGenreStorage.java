package ru.yandex.practicum.filmorate.storage.inmemory_hidden;

import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

import java.util.*;


public class InMemoryGenreStorage implements GenreStorage {

    private final Map<Integer, Genre> genres = new HashMap<>();

    public InMemoryGenreStorage() {
        genres.put(1, new Genre(1, "Комедия"));
        genres.put(2, new Genre(2, "Драма"));
        genres.put(3, new Genre(3, "Мультфильм"));
        genres.put(4, new Genre(4, "Триллер"));
        genres.put(5, new Genre(5, "Документальный"));
        genres.put(6, new Genre(6, "Боевик"));
    }

    @Override
    public Optional<Genre> findById(int id) {
        return Optional.ofNullable(genres.get(id));
    }

    @Override
    public Collection<Genre> findAll() {
        return genres.values();
    }

    @Override
    public List<Genre> findGenresByFilmId(Long filmId) {
        return List.of();
    }

    @Override
    public List<Genre> findAllByIds(Set<Integer> ids) {
        return ids.stream()
                .map(genres::get)
                .filter(Objects::nonNull)
                .toList();
    }

    @Override
    public Map<Long, List<Genre>> findGenresForFilmIds(List<Long> filmIds) {
        Map<Long, List<Genre>> result = new HashMap<>();
        for (Long filmId : filmIds) {
            result.put(filmId, List.of());
        }
        return result;
    }
}