package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.validator.UserValidator;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.*;

class UserValidationTest {

    @Test
    void loginWithSpaceFails() {
        var u = new User();
        u.setEmail("a@b.c");
        u.setLogin("bad login");
        u.setBirthday(LocalDate.now());

        assertThatThrownBy(() -> UserValidator.validate(u))
                .isInstanceOf(ValidationException.class);
    }

    @Test
    void futureBirthdayFails() {
        var u = new User();
        u.setEmail("a@b.c");
        u.setLogin("login");
        u.setBirthday(LocalDate.now().plusDays(1));

        assertThatThrownBy(() -> UserValidator.validate(u))
                .isInstanceOf(ValidationException.class);
    }

    @Test
    void emptyNameReplacedByLogin() {
        var u = new User();
        u.setEmail("a@b.c");
        u.setLogin("login");
        u.setBirthday(LocalDate.now());

        UserValidator.validate(u);

        assertThat(u.getName()).isEqualTo("login");
    }
}
