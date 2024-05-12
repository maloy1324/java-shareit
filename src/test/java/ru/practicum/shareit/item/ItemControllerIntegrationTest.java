package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.booking.dto.BookingDTO;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.CommentDTO;
import ru.practicum.shareit.item.dto.CommentOutDTO;
import ru.practicum.shareit.item.dto.ItemDTO;
import ru.practicum.shareit.item.dto.ItemOutDTO;
import ru.practicum.shareit.request.dao.ItemRequestDao;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.dao.UserDao;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.*;

import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static ru.practicum.shareit.booking.Status.APPROVED;

@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class ItemControllerIntegrationTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private UserDao userDao;

    @Autowired
    private ItemRequestDao itemRequestDao;

    @Autowired
    private BookingService bookingService;

    private ItemDTO itemDTO;
    private ItemOutDTO itemOutDTO;
    private User user1;
    private User user2;
    private final ObjectMapper mapper = new ObjectMapper();
    private final Long itemId1 = 1L;
    private final Long userId1 = 1L;
    private final Long userId2 = 2L;
    private final Long invalidId = 999L;
    private final int from = 0;
    private final int size = 2;

    public void init() {
        user1 = User.builder()
                .email("test@test.com")
                .name("Test")
                .build();
        user2 = User.builder()
                .email("test1@test.com")
                .name("Test1")
                .build();
        userDao.save(user1);
        userDao.save(user2);
    }

    public void setUp() {

        itemDTO = ItemDTO.builder()
                .name("Дрель")
                .description("Простая дрель")
                .available(true)
                .build();

        itemOutDTO = ItemOutDTO.builder()
                .id(itemId1)
                .name("Дрель")
                .description("Простая дрель")
                .available(true)
                .comments(new ArrayList<>())
                .build();
    }

    @Test
    @Order(0)
    @SneakyThrows
    public void testCreateItem_ReturnsStatusCreated() {
        init();
        setUp();

        mvc.perform(post("/items")
                        .header("X-Sharer-User-Id", userId1)
                        .content(mapper.writeValueAsString(itemDTO))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().json(mapper.writeValueAsString(itemOutDTO)));
    }

    @Test
    @Order(1)
    @SneakyThrows
    public void testCreateItem_WithInvalidUserId_ReturnsStatusNotFound() {
        setUp();

        mvc.perform(post("/items")
                        .header("X-Sharer-User-Id", invalidId)
                        .content(mapper.writeValueAsString(itemDTO))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertEquals(Objects.requireNonNull(result.getResolvedException()).getClass(),
                        NotFoundException.class));

    }

    @Test
    @Order(2)
    @SneakyThrows
    public void testUpdateItem1_WithInvalidItemId_ReturnsStatusNotFound() {
        setUp();

        itemDTO.toBuilder().name("Дрель--").build();

        mvc.perform(patch("/items/{itemId}", invalidId)
                        .header("X-Sharer-User-Id", userId1)
                        .content(mapper.writeValueAsString(itemDTO))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertEquals(Objects.requireNonNull(result.getResolvedException()).getClass(),
                        NotFoundException.class));

    }

    @Test
    @Order(3)
    @SneakyThrows
    public void testCreateItem2_WithUser2_ReturnsStatusCreated() {
        setUp();

        itemDTO = itemDTO.toBuilder()
                .name("Отвертка")
                .description("Аккумуляторная отвертка")
                .available(true)
                .build();
        itemOutDTO = itemOutDTO.toBuilder()
                .id(2L)
                .name("Отвертка")
                .description("Аккумуляторная отвертка")
                .available(true)
                .build();

        mvc.perform(post("/items")
                        .header("X-Sharer-User-Id", userId2)
                        .content(mapper.writeValueAsString(itemDTO))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().json(mapper.writeValueAsString(itemOutDTO)))
                .andReturn();

    }

    @Test
    @Order(4)
    @SneakyThrows
    public void testCreateItem3_WithUser2_ReturnsStatusCreated() {
        setUp();

        itemDTO = itemDTO.toBuilder()
                .name("Клей Момент")
                .description("Тюбик суперклея марки Момент")
                .available(true)
                .build();
        itemOutDTO = itemOutDTO.toBuilder()
                .id(3L)
                .name("Клей Момент")
                .description("Тюбик суперклея марки Момент")
                .available(true)
                .build();

        mvc.perform(post("/items")
                        .header("X-Sharer-User-Id", userId2)
                        .content(mapper.writeValueAsString(itemDTO))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().json(mapper.writeValueAsString(itemOutDTO)))
                .andReturn();

    }

    @Test
    @Order(5)
    @SneakyThrows
    public void testGetItem_ReturnsStatusOk() {
        setUp();

        mvc.perform(get("/items/{itemId}", itemId1)
                        .header("X-Sharer-User-Id", userId1)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(itemOutDTO)));

    }

    @Test
    @Order(6)
    @SneakyThrows
    public void testGetItem_WithUserIdNotOwner_ReturnsStatusOk() {
        setUp();

        mvc.perform(get("/items/{itemId}", itemId1)
                        .header("X-Sharer-User-Id", userId2)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(itemOutDTO)));

    }

    @Test
    @Order(7)
    @SneakyThrows
    public void testGetItem_WithInvalidItemId_ReturnsStatusNotFound() {
        setUp();

        mvc.perform(get("/items/{itemId}", invalidId)
                        .header("X-Sharer-User-Id", userId1)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertEquals(Objects.requireNonNull(result.getResolvedException()).getClass(),
                        NotFoundException.class));

    }

    @Test
    @Order(8)
    @SneakyThrows
    public void testGetItem_WithInvalidUserId_ReturnsStatusNotFound() {
        setUp();

        mvc.perform(get("/items/{itemId}", itemId1)
                        .header("X-Sharer-User-Id", invalidId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertEquals(Objects.requireNonNull(result.getResolvedException()).getClass(),
                        NotFoundException.class));

    }

    @Test
    @Order(9)
    @SneakyThrows
    public void testGetAllItems_WithOwnerId_ReturnsStatusOk() {

        ItemOutDTO itemOutDTO1 = ItemOutDTO.builder()
                .id(2L)
                .name("Отвертка")
                .description("Аккумуляторная отвертка")
                .available(true)
                .build();

        ItemOutDTO itemOutDTO2 = ItemOutDTO.builder()
                .id(3L)
                .name("Клей Момент")
                .description("Тюбик суперклея марки Момент")
                .available(true)
                .build();
        List<ItemOutDTO> items = Arrays.asList(itemOutDTO1, itemOutDTO2);

        mvc.perform(get("/items")
                        .header("X-Sharer-User-Id", userId2)
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

    }

    @Test
    @Order(10)
    @SneakyThrows
    public void testSearchAllItems_WithText_ReturnsStatusOk() {

        ItemOutDTO itemOutDTO = ItemOutDTO.builder()
                .id(2L)
                .name("Отвертка")
                .description("Аккумуляторная отвертка")
                .available(true)
                .build();
        String text = "отвер";

        List<ItemOutDTO> items = Collections.singletonList(itemOutDTO);

        mvc.perform(get("/items/search")
                        .header("X-Sharer-User-Id", userId2)
                        .param("text", text)
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(items)));

    }

    @Test
    @Order(11)
    @SneakyThrows
    public void testAddComment_ReturnsStatusOk() {
        setUp();
        LocalDateTime now = LocalDateTime.now();

        BookingDTO bookingInputDTO = BookingDTO.builder()
                .start(now.minusHours(2))
                .end(now.minusHours(1))
                .status(APPROVED)
                .bookerId(userId2)
                .itemId(itemId1)
                .build();
        bookingService.createBooking(bookingInputDTO);
        bookingService.updateBookingStatus(userId1, itemId1, true);

        CommentDTO commentDTO = CommentDTO.builder()
                .text("Add comment from user1")
                .build();

        CommentOutDTO commentOutDTO = CommentOutDTO.builder()
                .id(1L)
                .text("Add comment from user1")
                .authorName("Test1")
                .created(now)
                .build();

        mvc.perform(post("/items/{itemId}/comment", itemId1)
                        .header("X-Sharer-User-Id", userId2)
                        .content(mapper.writeValueAsString(commentDTO))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text", is(commentOutDTO.getText())))
                .andExpect(jsonPath("$.authorName", is(commentOutDTO.getAuthorName())));

    }

    @Test
    @Order(12)
    @SneakyThrows
    public void testUpdateItem_OnlyName_ReturnsStatusOk() {
        setUp();

        ItemDTO itemDTO = ItemDTO.builder()
                .id(itemId1)
                .name("Дрель++")
                .build();
        ItemOutDTO itemOutDTO = ItemOutDTO.builder()
                .id(itemId1)
                .name("Дрель++")
                .available(true)
                .build();

        mvc.perform(patch("/items/{itemId}", itemId1)
                        .header("X-Sharer-User-Id", userId1)
                        .content(mapper.writeValueAsString(itemDTO))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

    }

    @Test
    @Order(13)
    @SneakyThrows
    public void testUpdateItem_OnlyDescription_ReturnsStatusOk() {
        setUp();
        ItemDTO itemDTO = ItemDTO.builder()
                .id(itemId1)
                .description("Простая дрель++")
                .build();
        ItemOutDTO itemOutDTO = ItemOutDTO.builder()
                .id(itemId1)
                .description("Простая дрель++")
                .available(true)
                .build();

        mvc.perform(patch("/items/{itemId}", itemId1)
                        .header("X-Sharer-User-Id", userId1)
                        .content(mapper.writeValueAsString(itemDTO))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

    }

    @Test
    @Order(14)
    @SneakyThrows
    public void testUpdateItem_InvalidRequestId_ReturnsStatusNotFound() {
        setUp();
        ItemDTO itemDTO = ItemDTO.builder()
                .id(itemId1)
                .requestId(invalidId)
                .build();

        mvc.perform(patch("/items/{itemId}", itemId1)
                        .header("X-Sharer-User-Id", userId1)
                        .content(mapper.writeValueAsString(itemDTO))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertEquals(Objects.requireNonNull(result.getResolvedException()).getClass(),
                        NotFoundException.class));

    }

    @Test
    @Order(15)
    @SneakyThrows
    public void testAddComment_WithNotConfirmBooking_ReturnsStatusBadRequest() {
        setUp();

        CommentDTO commentDTO = CommentDTO.builder()
                .text("Add comment from user1")
                .build();

        mvc.perform(post("/items/{itemId}/comment", 2L)
                        .header("X-Sharer-User-Id", userId2)
                        .content(mapper.writeValueAsString(commentDTO))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertEquals(Objects.requireNonNull(result.getResolvedException()).getClass(),
                        BadRequestException.class));

    }

    @Test
    @Order(16)
    @SneakyThrows
    public void testCreate_WithItemRequestId_ReturnsStatusOk() {
        setUp();

        ItemRequest itemRequest = ItemRequest.builder().description("Нужен диван").requester(user2).build();
        itemRequestDao.save(itemRequest);
        ItemDTO itemDTO = ItemDTO.builder()
                .name("Диван")
                .description("Мягкий диван.")
                .available(true)
                .requestId(1L)
                .build();
        ItemOutDTO itemOutDTO = ItemOutDTO.builder()
                .id(4L)
                .name("Диван")
                .description("Мягкий диван.")
                .available(true)
                .requestId(1L)
                .build();

        mvc.perform(post("/items")
                        .header("X-Sharer-User-Id", userId1)
                        .content(mapper.writeValueAsString(itemDTO))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("id", is(itemOutDTO.getId()), Long.class))
                .andExpect(jsonPath("name", is(itemOutDTO.getName())))
                .andExpect(jsonPath("description", is(itemOutDTO.getDescription())))
                .andExpect(jsonPath("available", is(itemOutDTO.getAvailable())));

    }

    @Test
    @Order(17)
    @SneakyThrows
    public void testUpdateItem_OnlyItemRequestId_ReturnsStatusOk() {
        setUp();
        ItemDTO itemDTO = ItemDTO.builder()
                .id(itemId1)
                .requestId(1L)
                .build();
        ItemOutDTO itemOutDTO = ItemOutDTO.builder()
                .id(itemId1)
                .requestId(1L)
                .available(true)
                .build();

        mvc.perform(patch("/items/{itemId}", itemId1)
                        .header("X-Sharer-User-Id", userId1)
                        .content(mapper.writeValueAsString(itemDTO))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

    }

    @Test
    @Order(18)
    @SneakyThrows
    public void testUpdateItem_OnlyNameAndHaveItemRequestId_ReturnsStatusOk() {
        setUp();
        ItemDTO itemDTO = ItemDTO.builder()
                .id(itemId1)
                .name("Дрель!!!")
                .build();
        ItemOutDTO itemOutDTO = ItemOutDTO.builder()
                .id(itemId1)
                .name("Дрель!!!")
                .requestId(1L)
                .available(true)
                .build();

        mvc.perform(patch("/items/{itemId}", itemId1)
                        .header("X-Sharer-User-Id", userId1)
                        .content(mapper.writeValueAsString(itemDTO))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

    }

    @Test
    @Order(19)
    @SneakyThrows
    public void testUpdateItem_WithNotOwner_ReturnsStatusNotFound() {
        setUp();

        mvc.perform(patch("/items/{itemId}", itemId1)
                        .header("X-Sharer-User-Id", userId2)
                        .content(mapper.writeValueAsString(itemDTO))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andExpect(result -> assertEquals(Objects.requireNonNull(result.getResolvedException()).getClass(),
                        ForbiddenException.class));

    }

    @Test
    @Order(20)
    @SneakyThrows
    public void testAddComment_WithInvalidId_ReturnsStatusNotFound() {
        setUp();

        CommentDTO commentDTO = CommentDTO.builder()
                .text("Add comment from user1")
                .build();

        mvc.perform(post("/items/{itemId}/comment", invalidId)
                        .header("X-Sharer-User-Id", userId2)
                        .content(mapper.writeValueAsString(commentDTO))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertEquals(Objects.requireNonNull(result.getResolvedException()).getClass(),
                        NotFoundException.class));

    }

    @Test
    @Order(21)
    @SneakyThrows
    public void testSearchAllItems_WithEmptyText_ReturnsStatusOk() {
        String text = "";

        mvc.perform(get("/items/search")
                        .header("X-Sharer-User-Id", userId2)
                        .param("text", text)
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(List.of())));

    }
}


