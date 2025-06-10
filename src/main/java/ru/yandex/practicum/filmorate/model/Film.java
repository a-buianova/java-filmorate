package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;

@Data
public class Film implements Serializable {

    private Long id;

    @NotBlank(message = "name must not be empty")
    private String name;

    @Size(max = 200, message = "description must be ≤ 200 symbols")
    private String description;

    @NotNull(message = "releaseDate is required")
    @JsonFormat(pattern = "yyyy-MM-dd")
    @FilmReleaseDate
    private LocalDate releaseDate;

    @Positive(message = "duration must be positive")
    private int duration;
}