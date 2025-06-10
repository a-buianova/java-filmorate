package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;

@Data
public class User implements Serializable {

    private Integer id;

    @Email(message = "email must contain @")
    @NotBlank(message = "email must not be empty")
    private String email;

    @NotBlank(message = "login must not be empty")
    @Pattern(regexp = "^\\S+$", message = "login must not contain spaces")
    private String login;

    private String name;

    @PastOrPresent(message = "birthday must not be in the future")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthday;
}