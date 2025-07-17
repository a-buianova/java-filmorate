package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.RatingMpa;

import java.util.Collection;
import java.util.Optional;

public interface MpaStorage {
    Optional<RatingMpa> findById(int id);

    Collection<RatingMpa> findAll();
}