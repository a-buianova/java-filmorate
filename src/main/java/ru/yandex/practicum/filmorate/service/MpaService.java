package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.RatingMpa;
import ru.yandex.practicum.filmorate.storage.MpaStorage;

import java.util.Collection;

@Service
@RequiredArgsConstructor
public class MpaService {

    private final MpaStorage mpaStorage;

    public Collection<RatingMpa> findAll() {
        return mpaStorage.findAll();
    }

    public RatingMpa findById(int id) {
        return mpaStorage.findById(id)
                .orElseThrow(() -> new NotFoundException("MPA с id " + id + " не найден."));
    }
}