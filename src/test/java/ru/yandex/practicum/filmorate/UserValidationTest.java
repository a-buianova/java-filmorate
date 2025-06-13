package ru.yandex.practicum.filmorate;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.validator.UserValidator;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class UserValidationTest {

    private static Validator beanValidator;

    @BeforeAll
    static void init() {
        beanValidator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    void nullEmailFails() {
        User u = validUser();
        u.setEmail(null);

        assertThat(beanValidator.validate(u)).isNotEmpty();
    }

    @Test
    void badEmailFails() {
        User u = validUser();
        u.setEmail("bad-email");

        assertThat(beanValidator.validate(u)).isNotEmpty();
    }

    @Test
    void loginWithSpaceFails() {
        User u = validUser();
        u.setLogin("bad login");

        assertThatThrownBy(() -> UserValidator.validate(u))
                .isInstanceOf(ValidationException.class);
    }

    @Test
    void trimLoginOk() {
        User u = validUser();
        u.setLogin("  login  ");

        UserValidator.validate(u);

        assertThat(u.getLogin()).isEqualTo("login");
    }

    @Test
    void futureBirthdayFails() {
        User u = validUser();
        u.setBirthday(LocalDate.now().plusDays(1));

        assertThatThrownBy(() -> UserValidator.validate(u))
                .isInstanceOf(ValidationException.class);
    }

    @Test
    void emptyNameReplacedByLogin() {
        User u = validUser();
        u.setName("");

        UserValidator.validate(u);

        assertThat(u.getName()).isEqualTo(u.getLogin());
    }

    private User validUser() {
        User u = new User();
        u.setEmail("a@b.c");
        u.setLogin("login");
        u.setName("name");
        u.setBirthday(LocalDate.now());
        return u;
    }
}