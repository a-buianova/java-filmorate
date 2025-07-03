package ru.yandex.practicum.filmorate;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.service.FilmService;

import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(FilmController.class)
class FilmControllerLikeTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FilmService filmService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Успешное добавление лайка")
    void addLikeShouldReturn200() throws Exception {
        mockMvc.perform(put("/films/1/like/2"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Попытка поставить лайк несуществующему фильму -> 404")
    void addLikeFilmNotFound() throws Exception {
        doThrow(new NotFoundException("Фильм не найден"))
                .when(filmService).addLike(100L, 2L);

        mockMvc.perform(put("/films/100/like/2"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Попытка поставить лайк несуществующему пользователю -> 404")
    void addLikeUserNotFound() throws Exception {
        doThrow(new NotFoundException("Пользователь не найден"))
                .when(filmService).addLike(1L, 999L);

        mockMvc.perform(put("/films/1/like/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Успешное удаление лайка")
    void removeLikeShouldReturn200() throws Exception {
        mockMvc.perform(delete("/films/1/like/2"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Попытка удалить лайк у несуществующего фильма -> 404")
    void removeLikeFilmNotFound() throws Exception {
        doThrow(new NotFoundException("Фильм не найден"))
                .when(filmService).removeLike(100L, 2L);

        mockMvc.perform(delete("/films/100/like/2"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Попытка удалить лайк несуществующего пользователя -> 404")
    void removeLikeUserNotFound() throws Exception {
        doThrow(new NotFoundException("Пользователь не найден"))
                .when(filmService).removeLike(1L, 999L);

        mockMvc.perform(delete("/films/1/like/999"))
                .andExpect(status().isNotFound());
    }
}
