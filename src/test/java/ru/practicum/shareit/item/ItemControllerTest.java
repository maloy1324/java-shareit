package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.MethodArgumentNotValidException;
import ru.practicum.shareit.item.dto.ItemDTO;
import ru.practicum.shareit.item.service.ItemService;

import java.nio.charset.StandardCharsets;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@ActiveProfiles("test")
@WebMvcTest(controllers = ItemController.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ItemControllerTest {
    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private ItemService service;

    private ItemDTO itemDTO;
    private final Long userId1 = 1L;

    void setUp() {

        itemDTO = ItemDTO.builder()
                .name("Дрель")
                .description("Простая дрель")
                .available(true)
                .build();
    }

    @Test
    @SneakyThrows
    void testCreateItem_WithEmptyName_ResultException() {
        setUp();
        itemDTO = itemDTO.toBuilder()
                .name(null)
                .build();

        mvc.perform(post("/items")
                        .header("X-Sharer-User-Id", userId1)
                        .content(mapper.writeValueAsString(itemDTO))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError())
                .andExpect(result -> assertEquals(Objects.requireNonNull(result.getResolvedException()).getClass(),
                        MethodArgumentNotValidException.class));

        verify(service, never()).addItem(userId1, itemDTO);
    }
}