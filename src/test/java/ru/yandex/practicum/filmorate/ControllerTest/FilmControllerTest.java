package ru.yandex.practicum.filmorate.ControllerTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class FilmControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    @Test
    @DisplayName("POST /films — успешное создание фильма")
    void createFilm_shouldReturn201() throws Exception {
        var body = mapper.createObjectNode()
                .put("name", "Matrix")
                .put("description", "Neo chooses pill")
                .put("releaseDate", "1999-03-31")
                .put("duration", 136)
                .set("mpa", mapper.createObjectNode().put("id", 1));

        mvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body.toString()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", is("Matrix")))
                .andExpect(jsonPath("$.mpa.id", is(1)));
    }

    @Test
    @DisplayName("POST /films — ошибка: пустое название")
    void createFilm_withEmptyName_shouldReturn400() throws Exception {
        var body = mapper.createObjectNode()
                .put("name", " ")
                .put("description", "desc")
                .put("releaseDate", "2000-01-01")
                .put("duration", 100)
                .set("mpa", mapper.createObjectNode().put("id", 1));

        mvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body.toString()))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /films — ошибка: отсутствует mpa")
    void createFilm_withoutMpa_shouldReturn400() throws Exception {
        var body = mapper.createObjectNode()
                .put("name", "Film")
                .put("description", "desc")
                .put("releaseDate", "2000-01-01")
                .put("duration", 120);

        mvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body.toString()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Ошибка валидации входных данных."));
    }

    @Test
    @DisplayName("POST /films — ошибка: отрицательная продолжительность")
    void createFilm_withNegativeDuration_shouldReturn400() throws Exception {
        var body = mapper.createObjectNode()
                .put("name", "Good Film")
                .put("description", "desc")
                .put("releaseDate", "2000-01-01")
                .put("duration", -10)
                .set("mpa", mapper.createObjectNode().put("id", 1));

        mvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body.toString()))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /films — ошибка: релиз раньше 1895-12-28")
    void createFilm_withEarlyReleaseDate_shouldReturn400() throws Exception {
        var body = mapper.createObjectNode()
                .put("name", "Old Film")
                .put("description", "desc")
                .put("releaseDate", "1895-12-27")
                .put("duration", 100)
                .set("mpa", mapper.createObjectNode().put("id", 1));

        mvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body.toString()))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("PUT /films — успешное обновление фильма")
    void updateFilm_shouldReturn200() throws Exception {
        var createBody = mapper.createObjectNode()
                .put("name", "Matrix")
                .put("description", "Neo chooses pill")
                .put("releaseDate", "1999-03-31")
                .put("duration", 136)
                .set("mpa", mapper.createObjectNode().put("id", 1));

        String response = mvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createBody.toString()))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        var createdFilm = mapper.readTree(response);
        long id = createdFilm.get("id").asLong();

        var updateBody = mapper.createObjectNode()
                .put("id", id)
                .put("name", "Updated Matrix")
                .put("description", "Updated description")
                .put("releaseDate", "2000-01-01")
                .put("duration", 150)
                .set("mpa", mapper.createObjectNode().put("id", 2));

        mvc.perform(put("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateBody.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Updated Matrix")))
                .andExpect(jsonPath("$.mpa.id", is(2)));
    }

    @Test
    @DisplayName("PUT /films — обновление несуществующего фильма → 404")
    void updateFilm_withNonExistentId_shouldReturn404() throws Exception {
        var updateBody = mapper.createObjectNode()
                .put("id", 9999)
                .put("name", "Nonexistent")
                .put("description", "Doesn't exist")
                .put("releaseDate", "2000-01-01")
                .put("duration", 150)
                .set("mpa", mapper.createObjectNode().put("id", 1));

        mvc.perform(put("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateBody.toString()))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /films — получить все фильмы")
    void getAllFilms_shouldReturn200() throws Exception {
        mvc.perform(get("/films"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /films/{id} — получить фильм по ID")
    void getFilmById_shouldReturn200() throws Exception {
        var createBody = mapper.createObjectNode()
                .put("name", "Film")
                .put("description", "desc")
                .put("releaseDate", "2001-01-01")
                .put("duration", 120)
                .set("mpa", mapper.createObjectNode().put("id", 1));

        String response = mvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createBody.toString()))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        var createdFilm = mapper.readTree(response);
        long id = createdFilm.get("id").asLong();

        mvc.perform(get("/films/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is((int) id)));
    }
}