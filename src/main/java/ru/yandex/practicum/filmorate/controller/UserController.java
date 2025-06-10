package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.validator.UserValidator;

import java.net.URI;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {

    private final Map<Integer, User> users = new ConcurrentHashMap<>();

    private final AtomicInteger idGenerator = new AtomicInteger();

    @PostMapping
    public ResponseEntity<User> create(@Valid @RequestBody User user) {
        UserValidator.validate(user);

        int id = idGenerator.incrementAndGet();
        user.setId(id);
        users.put(id, user);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(id)
                .toUri();

        log.info("POST /users → 201, id={}", id);
        return ResponseEntity.created(location).body(user);
    }

    @PutMapping
    public User update(@Valid @RequestBody User user) {
        Integer id = user.getId();
        if (id == null) {
            throw new NotFoundException("User ID is required");
        }

        User updated = users.computeIfPresent(id, (k, v) -> {
            UserValidator.validate(user);
            return user;
        });

        if (updated == null) {
            log.warn("PUT /users → 404, id={} not found", id);
            throw new NotFoundException("User ID not found");
        }

        log.info("PUT /users → 200, id={}", id);
        return updated;
    }

    @GetMapping
    public List<User> getAll() {
        return users.values().stream()
                .sorted(Comparator.comparingInt(User::getId))
                .toList();
    }

    @GetMapping("/{id}")
    public User getById(@PathVariable int id) {
        User user = users.get(id);
        if (user == null) {
            log.warn("GET /users/{} → 404", id);
            throw new NotFoundException("User ID not found");
        }
        return user;
    }
}