package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.net.URI;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {

    private final Map<Long, Film> films = new ConcurrentHashMap<>();

    private final AtomicLong idGenerator = new AtomicLong();

    @PostMapping
    public ResponseEntity<Film> addFilm(@Valid @RequestBody Film film) {
        long id = idGenerator.incrementAndGet();
        film.setId(id);
        films.put(id, film);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(id)
                .toUri();

        log.info("POST /films → 201, id={}", id);
        return ResponseEntity.created(location).body(film);
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) {
        Long id = film.getId();
        if (id == null) {
            throw new NotFoundException("Film ID is required");
        }

        Film updated = films.computeIfPresent(id, (k, v) -> film);
        if (updated == null) {
            log.warn("PUT /films → 404, id={} not found", id);
            throw new NotFoundException("Film ID not found");
        }

        log.info("PUT /films → 200, id={}", id);
        return updated;
    }

    @GetMapping
    public List<Film> getAll() {
        return films.values().stream()
                .sorted(Comparator.comparingLong(Film::getId))
                .toList();
    }

    @GetMapping("/{id}")
    public Film getById(@PathVariable long id) {
        Film film = films.get(id);
        if (film == null) {
            log.warn("GET /films/{} → 404", id);
            throw new NotFoundException("Film ID not found");
        }
        return film;
    }
}