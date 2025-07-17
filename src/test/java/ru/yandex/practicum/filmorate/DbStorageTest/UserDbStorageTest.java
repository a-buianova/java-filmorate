package ru.yandex.practicum.filmorate.DbStorageTest;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.DbStorage.UserDbStorage;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@Import(UserDbStorage.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserDbStorageTest {

    private final UserDbStorage userStorage;

    @Test
    @DisplayName("Добавление и поиск пользователя по id")
    void addAndFindUserById() {
        User user = createUser("user1@mail.com", "login1");
        userStorage.add(user);

        Optional<User> found = userStorage.findById(user.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getEmail()).isEqualTo("user1@mail.com");
    }

    @Test
    @DisplayName("Обновление пользователя")
    void updateUser() {
        User user = createUser("user2@mail.com", "login2");
        userStorage.add(user);

        user.setName("Updated Name");
        user.setEmail("updated@mail.com");
        userStorage.update(user);

        Optional<User> found = userStorage.findById(user.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getEmail()).isEqualTo("updated@mail.com");
        assertThat(found.get().getName()).isEqualTo("Updated Name");
    }

    @Test
    @DisplayName("Получение всех пользователей")
    void getAllUsers() {
        userStorage.add(createUser("a@mail.com", "a"));
        userStorage.add(createUser("b@mail.com", "b"));

        List<User> users = userStorage.findAll();
        assertThat(users.size()).isGreaterThanOrEqualTo(2);
    }

    @Test
    @DisplayName("Добавление и удаление друга (односторонняя дружба)")
    void addAndRemoveFriend() {
        User user1 = userStorage.add(createUser("user3@mail.com", "login3"));
        User user2 = userStorage.add(createUser("user4@mail.com", "login4"));

        userStorage.addFriend(user1.getId(), user2.getId());
        List<User> friends = userStorage.getFriends(user1.getId());

        assertThat(friends).extracting(User::getId).contains(user2.getId());

        userStorage.removeFriend(user1.getId(), user2.getId());
        List<User> updated = userStorage.getFriends(user1.getId());
        assertThat(updated).doesNotContain(user2);
    }

    @Test
    @DisplayName("Получение общих друзей")
    void getCommonFriends() {
        User userA = userStorage.add(createUser("a@mail.com", "a"));
        User userB = userStorage.add(createUser("b@mail.com", "b"));
        User mutual = userStorage.add(createUser("mutual@mail.com", "m"));

        userStorage.addFriend(userA.getId(), mutual.getId());
        userStorage.addFriend(userB.getId(), mutual.getId());

        List<User> commons = userStorage.getCommonFriends(userA.getId(), userB.getId());
        assertThat(commons).extracting(User::getId).contains(mutual.getId());
    }

    private User createUser(String email, String login) {
        User user = new User();
        user.setEmail(email);
        user.setLogin(login);
        user.setName("");
        user.setBirthday(LocalDate.of(2000, 1, 1));
        return user;
    }
}