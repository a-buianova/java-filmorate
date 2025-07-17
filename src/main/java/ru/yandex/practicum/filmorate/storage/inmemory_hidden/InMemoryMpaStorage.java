package ru.yandex.practicum.filmorate.storage.inmemory_hidden;

import ru.yandex.practicum.filmorate.model.RatingMpa;
import ru.yandex.practicum.filmorate.storage.MpaStorage;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;


public class InMemoryMpaStorage implements MpaStorage {

    private final Map<Integer, RatingMpa> ratings = new HashMap<>();

    public InMemoryMpaStorage() {
        ratings.put(1, new RatingMpa(1, "G"));
        ratings.put(2, new RatingMpa(2, "PG"));
        ratings.put(3, new RatingMpa(3, "PG-13"));
        ratings.put(4, new RatingMpa(4, "R"));
        ratings.put(5, new RatingMpa(5, "NC-17"));
    }

    @Override
    public Optional<RatingMpa> findById(int id) {
        return Optional.ofNullable(ratings.get(id));
    }

    @Override
    public Collection<RatingMpa> findAll() {
        return ratings.values();
    }
}