package ru.yandex.practicum.filmorate.DbStorageTest;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.DbStorage.GenreDbStorage;

import java.util.Collection;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@Import(GenreDbStorage.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class GenreDbStorageTest {

    private final GenreDbStorage genreStorage;

    @Test
    @DisplayName("Должен вернуть все жанры")
    void shouldReturnAllGenres() {
        Collection<Genre> genres = genreStorage.findAll();

        assertThat(genres)
                .isNotNull()
                .hasSize(6)
                .extracting(Genre::getName)
                .contains("Комедия", "Драма", "Мультфильм", "Триллер", "Документальный", "Боевик");
    }

    @Test
    @DisplayName("Должен вернуть жанр по id")
    void shouldReturnGenreById() {
        Optional<Genre> genreOptional = genreStorage.findById(2);

        assertThat(genreOptional)
                .isPresent()
                .hasValueSatisfying(genre ->
                        assertThat(genre.getName()).isEqualTo("Драма"));
    }

    @Test
    @DisplayName("Жанр с несуществующим id возвращает пустой Optional")
    void shouldReturnEmptyOptionalIfNotExists() {
        Optional<Genre> genreOptional = genreStorage.findById(999);

        assertThat(genreOptional).isEmpty();
    }
}