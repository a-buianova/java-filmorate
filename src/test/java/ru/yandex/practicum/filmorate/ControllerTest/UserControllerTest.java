package ru.yandex.practicum.filmorate.ControllerTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    @Test
    @DisplayName("POST /users — успешное создание пользователя")
    void createUser_shouldReturn201() throws Exception {
        var body = mapper.createObjectNode()
                .put("email", "user@mail.com")
                .put("login", "username")
                .put("name", "User Name")
                .put("birthday", "2000-01-01");

        mvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body.toString()))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("POST /users — auto-fill имени, если пустое")
    void createUser_emptyName_shouldUseLoginAsName() throws Exception {
        var body = mapper.createObjectNode()
                .put("email", "auto@mail.com")
                .put("login", "autologin")
                .put("name", "")
                .put("birthday", "1999-01-01");

        String response = mvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body.toString()))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        var createdUser = mapper.readTree(response);
        assertThat(createdUser.get("name").asText()).isEqualTo("autologin");
    }

    @Test
    @DisplayName("POST /users — ошибка при пустом email")
    void createUser_withEmptyEmail_shouldReturn400() throws Exception {
        var body = mapper.createObjectNode()
                .put("email", "")
                .put("login", "username")
                .put("name", "User Name")
                .put("birthday", "2000-01-01");

        mvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body.toString()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Ошибка валидации входных данных."));
    }

    @Test
    @DisplayName("POST /users — ошибка при логине с пробелами")
    void createUser_withLoginWithSpaces_shouldReturn400() throws Exception {
        var body = mapper.createObjectNode()
                .put("email", "user@mail.com")
                .put("login", "user name")
                .put("name", "User Name")
                .put("birthday", "2000-01-01");

        mvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body.toString()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Ошибка валидации входных данных."));
    }

    @Test
    @DisplayName("POST /users — ошибка при дате рождения в будущем")
    void createUser_withFutureBirthday_shouldReturn400() throws Exception {
        var body = mapper.createObjectNode()
                .put("email", "user@mail.com")
                .put("login", "username")
                .put("name", "User Name")
                .put("birthday", LocalDate.now().plusDays(1).toString());

        mvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body.toString()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Ошибка валидации входных данных."));
    }

    @Test
    @DisplayName("PUT /users — успешное обновление пользователя")
    void updateUser_shouldReturn200() throws Exception {
        long id = createUser("update@mail.com", "updatelogin");

        var updateBody = mapper.createObjectNode()
                .put("id", id)
                .put("email", "updated@mail.com")
                .put("login", "newlogin")
                .put("name", "Updated Name")
                .put("birthday", "1990-05-10");

        mvc.perform(put("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateBody.toString()))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("PUT /users — обновление несуществующего пользователя → 404")
    void updateUser_notFound_shouldReturn404() throws Exception {
        var updateBody = mapper.createObjectNode()
                .put("id", 9999)
                .put("email", "test@mail.com")
                .put("login", "testlogin")
                .put("name", "Test")
                .put("birthday", "1990-01-01");

        mvc.perform(put("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateBody.toString()))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /users — получение списка пользователей")
    void getAllUsers_shouldReturn200() throws Exception {
        mvc.perform(get("/users"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /users/{id} — получение пользователя по ID")
    void getUserById_shouldReturn200() throws Exception {
        long id = createUser("get@mail.com", "getuser");

        mvc.perform(get("/users/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id));
    }

    @Test
    @DisplayName("GET /users/{id} — пользователь не найден → 404")
    void getUserByIdNotFound_shouldReturn404() throws Exception {
        mvc.perform(get("/users/{id}", 9999))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("PUT /users/{id}/friends/{friendId} — добавить друга")
    void addFriend_shouldReturn200() throws Exception {
        long userId = createUser("a@mail.com", "aaa");
        long friendId = createUser("b@mail.com", "bbb");

        mvc.perform(put("/users/{id}/friends/{friendId}", userId, friendId))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("DELETE /users/{id}/friends/{friendId} — удалить друга")
    void removeFriend_shouldReturn200() throws Exception {
        long userId = createUser("c@mail.com", "ccc");
        long friendId = createUser("d@mail.com", "ddd");

        mvc.perform(put("/users/{id}/friends/{friendId}", userId, friendId))
                .andExpect(status().isOk());

        mvc.perform(delete("/users/{id}/friends/{friendId}", userId, friendId))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /users/{id}/friends — получить список друзей")
    void getFriends_shouldReturn200() throws Exception {
        long userId = createUser("e@mail.com", "eee");
        long friendId = createUser("f@mail.com", "fff");

        mvc.perform(put("/users/{id}/friends/{friendId}", userId, friendId))
                .andExpect(status().isOk());

        mvc.perform(get("/users/{id}/friends", userId))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /users/{id}/friends/common/{otherId} — общие друзья")
    void getCommonFriends_shouldReturn200() throws Exception {
        long user1 = createUser("g@mail.com", "ggg");
        long user2 = createUser("h@mail.com", "hhh");
        long mutual = createUser("mutual@mail.com", "mut");

        mvc.perform(put("/users/{id}/friends/{friendId}", user1, mutual))
                .andExpect(status().isOk());
        mvc.perform(put("/users/{id}/friends/{friendId}", user2, mutual))
                .andExpect(status().isOk());

        mvc.perform(get("/users/{id}/friends/common/{otherId}", user1, user2))
                .andExpect(status().isOk());
    }

    private long createUser(String email, String login) throws Exception {
        var body = mapper.createObjectNode()
                .put("email", email)
                .put("login", login)
                .put("name", "")
                .put("birthday", "2000-01-01");

        String response = mvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body.toString()))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        return mapper.readTree(response).get("id").asLong();
    }
}