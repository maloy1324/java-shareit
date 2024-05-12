package ru.practicum.shareit.request.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemRequestController.class)
public class ItemRequestControllerTest {

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    private ItemRequestService itemRequestService;

    @Autowired
    private MockMvc mockMvc;

    private ItemRequestDto itemRequestDto;

    @BeforeEach
    void setUp() {
        String description = "description";
        LocalDateTime dateTime = LocalDateTime.of(2024, 4, 1, 23, 0, 0);

        itemRequestDto = ItemRequestDto.builder()
                .id(1L)
                .description(description)
                .created(dateTime)
                .build();
    }

    @SneakyThrows
    @Test
    void addItemRequestTest() {
        when(itemRequestService.create(1L, itemRequestDto))
                .thenReturn(itemRequestDto);
        String result = mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 1)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(itemRequestDto)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();
        assertEquals(objectMapper.writeValueAsString(itemRequestDto), result);
    }

    @SneakyThrows
    @Test
    void getAllMineRequestsTest() {
        when(itemRequestService.findAllByUser(1L))
                .thenReturn(List.of(itemRequestDto));
        String result = mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        assertEquals(objectMapper.writeValueAsString(List.of(itemRequestDto)), result);
    }

    @SneakyThrows
    @Test
    void getItemRequestByIdTest() {
        long userId = 1L;
        long itemRequestId = 1L;
        when(itemRequestService.findById(userId, itemRequestId))
                .thenReturn(itemRequestDto);
        String result = mockMvc.perform(get("/requests/{requestId}", itemRequestId)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        assertEquals(objectMapper.writeValueAsString(itemRequestDto), result);
    }
}