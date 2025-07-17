package ru.yandex.practicum.filmorate.ControllerTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.service.FilmService;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FilmController.class)
class FilmControllerLikeTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FilmService filmService;

    @Autowired
    private ObjectMapper objectMapper;

    @Nested
    @DisplayName("PUT /films/{id}/like/{userId}")
    class AddLike {

        @Test
        @DisplayName("Успешное добавление лайка")
        void shouldReturn200() throws Exception {
            mockMvc.perform(put("/films/1/like/2"))
                    .andExpect(status().isOk());
            verify(filmService).addLike(1L, 2L);
        }

        @Test
        @DisplayName("Ошибка: фильм не найден")
        void filmNotFound() throws Exception {
            doThrow(new NotFoundException("Фильм не найден"))
                    .when(filmService).addLike(100L, 2L);

            mockMvc.perform(put("/films/100/like/2"))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.error").value("Объект не найден."))
                    .andExpect(jsonPath("$.message").value("Фильм не найден"));
        }

        @Test
        @DisplayName("Ошибка: пользователь не найден")
        void userNotFound() throws Exception {
            doThrow(new NotFoundException("Пользователь не найден"))
                    .when(filmService).addLike(1L, 999L);

            mockMvc.perform(put("/films/1/like/999"))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.error").value("Объект не найден."))
                    .andExpect(jsonPath("$.message").value("Пользователь не найден"));
        }

        @Test
        @DisplayName("Ошибка: некорректный ID (не число)")
        void invalidIds() throws Exception {
            mockMvc.perform(put("/films/abc/like/xyz"))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("DELETE /films/{id}/like/{userId}")
    class RemoveLike {

        @Test
        @DisplayName("Успешное удаление лайка")
        void shouldReturn200() throws Exception {
            mockMvc.perform(delete("/films/1/like/2"))
                    .andExpect(status().isOk());
            verify(filmService).removeLike(1L, 2L);
        }

        @Test
        @DisplayName("Ошибка: фильм не найден")
        void filmNotFound() throws Exception {
            doThrow(new NotFoundException("Фильм не найден"))
                    .when(filmService).removeLike(100L, 2L);

            mockMvc.perform(delete("/films/100/like/2"))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.error").value("Объект не найден."))
                    .andExpect(jsonPath("$.message").value("Фильм не найден"));
        }

        @Test
        @DisplayName("Ошибка: пользователь не найден")
        void userNotFound() throws Exception {
            doThrow(new NotFoundException("Пользователь не найден"))
                    .when(filmService).removeLike(1L, 999L);

            mockMvc.perform(delete("/films/1/like/999"))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.error").value("Объект не найден."))
                    .andExpect(jsonPath("$.message").value("Пользователь не найден"));
        }

        @Test
        @DisplayName("Ошибка: некорректный ID (не число)")
        void invalidIds() throws Exception {
            mockMvc.perform(delete("/films/abc/like/xyz"))
                    .andExpect(status().isBadRequest());
        }
    }
}