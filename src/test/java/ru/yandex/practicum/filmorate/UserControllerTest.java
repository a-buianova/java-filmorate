package ru.yandex.practicum.filmorate;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
        var createBody = mapper.createObjectNode()
                .put("email", "user@mail.com")
                .put("login", "username")
                .put("name", "User Name")
                .put("birthday", "2000-01-01");

        String response = mvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createBody.toString()))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        var createdUser = mapper.readTree(response);
        int id = createdUser.get("id").asInt();

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
    @DisplayName("GET /users — получение списка пользователей")
    void getAllUsers_shouldReturn200() throws Exception {
        mvc.perform(get("/users"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /users/{id} — получение пользователя по ID")
    void getUserById_shouldReturn200() throws Exception {
        var createBody = mapper.createObjectNode()
                .put("email", "user2@mail.com")
                .put("login", "username2")
                .put("name", "User Name 2")
                .put("birthday", "1999-01-01");

        String response = mvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createBody.toString()))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        var createdUser = mapper.readTree(response);
        int id = createdUser.get("id").asInt();

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
}