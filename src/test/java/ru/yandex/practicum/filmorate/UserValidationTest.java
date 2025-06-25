package ru.yandex.practicum.filmorate;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class UserValidationTest {

    private static Validator validator;

    @BeforeAll
    static void setUp() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    @DisplayName("Пустой email не проходит валидацию")
    void nullEmailFails() {
        User user = validUser();
        user.setEmail(null);
        assertThat(validator.validate(user)).isNotEmpty();
    }

    @Test
    @DisplayName("Пустая строка в email — не проходит валидацию")
    void emptyEmailFails() {
        User user = validUser();
        user.setEmail("");
        assertThat(validator.validate(user)).isNotEmpty();
    }

    @Test
    @DisplayName("Некорректный email (без @) не проходит валидацию")
    void badEmailFails() {
        User user = validUser();
        user.setEmail("bad-email");
        assertThat(validator.validate(user)).isNotEmpty();
    }

    @Test
    @DisplayName("Пустой логин — не проходит валидацию")
    void blankLoginFails() {
        User user = validUser();
        user.setLogin(" ");
        assertThat(validator.validate(user)).isNotEmpty();
    }

    @Test
    @DisplayName("Логин с пробелами внутри — не проходит валидацию")
    void loginWithSpacesFails() {
        User user = validUser();
        user.setLogin("user name");
        assertThat(validator.validate(user)).isNotEmpty();
    }

    @Test
    @DisplayName("Дата рождения в будущем — не проходит валидацию")
    void futureBirthdayFails() {
        User user = validUser();
        user.setBirthday(LocalDate.now().plusDays(1));
        assertThat(validator.validate(user)).isNotEmpty();
    }

    @Test
    @DisplayName("Корректный пользователь проходит валидацию")
    void validUserOk() {
        User user = validUser();
        assertThat(validator.validate(user)).isEmpty();
    }

    private User validUser() {
        User user = new User();
        user.setEmail("user@mail.com");
        user.setLogin("username");
        user.setName("User Name");
        user.setBirthday(LocalDate.now());
        return user;
    }
}