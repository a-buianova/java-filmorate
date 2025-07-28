package ru.yandex.practicum.filmorate.ValidationTest;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Set;

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
    @DisplayName("Login с пробелами — ошибка валидации")
    void loginWithSpacesFails() {
        User user = new User();
        user.setEmail("user@example.com");
        user.setLogin("invalid login"); // пробел
        user.setName("Имя");
        user.setBirthday(LocalDate.of(2000, 1, 1));

        Set<ConstraintViolation<User>> violations = validator.validate(user);

        assertThat(violations)
                .anyMatch(v -> v.getPropertyPath().toString().equals("login")
                        && v.getMessage().contains("не должен содержать пробелов"));
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

    @Test
    @DisplayName("Login: null → не проходит")
    void nullLoginFails() {
        User user = validUser();
        user.setLogin(null);
        assertThat(validator.validate(user)).isNotEmpty();
    }

    @Test
    @DisplayName("Birthday: null → проходит (опционально)")
    void nullBirthdayIsValid() {
        User user = validUser();
        user.setBirthday(null);
        assertThat(validator.validate(user)).isEmpty();
    }

    @Test
    @DisplayName("Name: пустая строка → проходит (необязательное поле)")
    void emptyNameIsValid() {
        User user = validUser();
        user.setName("");
        assertThat(validator.validate(user)).isEmpty();
    }

    @Test
    @DisplayName("Name: null → проходит (автозаполнение в сервисе)")
    void nullNameIsValid() {
        User user = validUser();
        user.setName(null);
        assertThat(validator.validate(user)).isEmpty();
    }
}