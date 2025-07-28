package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.RatingMpa;
import ru.yandex.practicum.filmorate.service.MpaService;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/mpa")
@RequiredArgsConstructor
public class MpaController {

    private final MpaService mpaService;

    @GetMapping
    public List<RatingMpa> findAll() {
        return new ArrayList<>(mpaService.findAll());
    }

    @GetMapping("/{id}")
    public RatingMpa findById(@PathVariable Integer id) {
        return mpaService.findById(id);
    }
}