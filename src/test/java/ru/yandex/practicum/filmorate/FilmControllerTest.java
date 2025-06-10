package ru.yandex.practicum.filmorate;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.controller.FilmController;

import static org.hamcrest.Matchers.endsWith;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FilmController.class)
class FilmControllerTest {

    @Autowired
    MockMvc mvc;

    @Autowired
    ObjectMapper mapper;

    @Test
    @DisplayName("POST /films — happy-path 201 + абсолютный Location-header")
    void createFilm201() throws Exception {
        var body = mapper.createObjectNode()
                .put("name", "Matrix")
                .put("description", "Neo chooses pill")
                .put("releaseDate", "1999-03-31")
                .put("duration", 136);

        mvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body.toString()))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", endsWith("/films/1")));
    }

    @Test
    @DisplayName("POST /films bad duration → 400 + ValidationException JSON")
    void createFilmBad400() throws Exception {
        var body = mapper.createObjectNode()
                .put("name", "Bad")
                .put("description", "desc")
                .put("releaseDate", "2000-01-01")
                .put("duration", -10);

        mvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body.toString()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("ValidationException"));
    }
}