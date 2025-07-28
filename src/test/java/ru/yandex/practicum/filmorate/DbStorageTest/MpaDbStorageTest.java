package ru.yandex.practicum.filmorate.DbStorageTest;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.model.RatingMpa;
import ru.yandex.practicum.filmorate.storage.DbStorage.MpaDbStorage;

import java.util.Collection;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@Import(MpaDbStorage.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class MpaDbStorageTest {

    private final MpaDbStorage mpaDbStorage;

    @Test
    @DisplayName("Получение всех рейтингов MPA")
    void shouldReturnAllMpaRatings() {
        Collection<RatingMpa> all = mpaDbStorage.findAll();

        assertThat(all)
                .isNotNull()
                .hasSize(5)
                .extracting(RatingMpa::getName)
                .contains("G", "PG", "PG-13", "R", "NC-17");
    }

    @Test
    @DisplayName("Получение рейтинга MPA по id")
    void shouldReturnMpaById() {
        Optional<RatingMpa> rating = mpaDbStorage.findById(1);

        assertThat(rating)
                .isPresent()
                .hasValueSatisfying(mpa ->
                        assertThat(mpa.getName()).isEqualTo("G")
                );
    }

    @Test
    @DisplayName("Попытка получить несуществующий рейтинг MPA по id")
    void shouldReturnEmptyIfMpaNotFound() {
        Optional<RatingMpa> rating = mpaDbStorage.findById(999);

        assertThat(rating).isNotPresent();
    }
}