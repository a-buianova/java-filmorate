package ru.yandex.practicum.filmorate.validator;

import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

public final class UserValidator {

    private static final String MSG_EMPTY_LOGIN  = "login must not be empty";
    private static final String MSG_SPACE_LOGIN  = "login must not contain spaces";
    private static final String MSG_FUTURE_BIRTH = "birthday must not be in the future";

    private UserValidator() {
    }

    public static void validate(User user) {

        if (user.getLogin() != null) {
            user.setLogin(user.getLogin().trim());
        }

        if (user.getLogin() == null || user.getLogin().isBlank()) {
            throw new ValidationException(MSG_EMPTY_LOGIN);
        }
        if (user.getLogin().contains(" ")) {
            throw new ValidationException(MSG_SPACE_LOGIN);
        }

        if (user.getBirthday() != null &&
                user.getBirthday().isAfter(LocalDate.now())) {
            throw new ValidationException(MSG_FUTURE_BIRTH);
        }

        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
    }
}