package ru.practicum.shareit.request.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class ItemDTOTest {
    @Autowired
    private JacksonTester<ItemRequestDto> json;

    private final LocalDateTime now = LocalDateTime.now();

    private final ItemRequestDto itemRequestDto = ItemRequestDto.builder()
            .id(1L)
            .description("Хотел бы воспользоваться щёткой для обуви")
            .created(now)
            .items(new ArrayList<>())
            .build();

    @Test
    public void testJsonSerialization() throws Exception {
        // Сериализация объекта в JSON
        var result = json.write(itemRequestDto);

        // Проверка наличия необходимых полей в JSON
        assertThat(result).hasJsonPath("$.id");
        assertThat(result).hasJsonPath("$.description");
        assertThat(result).hasJsonPath("$.created");

        // Проверка значений полей в JSON
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(itemRequestDto.getId().intValue());
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo(itemRequestDto.getDescription());
        assertThat(result).extractingJsonPathStringValue("$.created").isNotEmpty();
    }

    @Test
    public void testJsonDeserialization() throws Exception {
        String content = "{\"description\":\"Хотел бы воспользоваться щёткой для обуви\"}";

        ItemRequestDto result = json.parse(content).getObject();

        assertThat(result.getDescription()).isEqualTo(itemRequestDto.getDescription());
    }
}