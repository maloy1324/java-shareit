package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.SneakyThrows;
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
import ru.practicum.shareit.booking.dto.BookingDTO;
import ru.practicum.shareit.booking.dto.BookingOutDTO;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dao.ItemDao;
import ru.practicum.shareit.item.dto.ItemOutDTO;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dao.UserDao;
import ru.practicum.shareit.user.dto.UserOutDTO;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class BookingControllerIntegrationTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private UserDao userDao;

    @Autowired
    private ItemDao itemDao;
    private final ObjectMapper mapper = new ObjectMapper();

    private User user1;
    private User user2;
    private UserOutDTO userOutDTO1;

    private Item item1;
    private Item item2;
    private Item item3;
    private ItemOutDTO itemOutDTO2;

    
    private BookingDTO bookingDTO;
    private BookingOutDTO bookingOutDTO;


    private final Long userId1 = 1L;
    private final Long userId2 = 2L;
    private final Long itemId1 = 1L;
    private final Long itemId2 = 2L;
    private final Long itemId3 = 3L;
    private final Long bookingId1 = 1L;
    private final Long invalidId = 999L;
    private final int from = 0;
    private final int size = 2;
    private final LocalDateTime startTime = LocalDateTime.now().plusHours(1);
    private final LocalDateTime endTime = LocalDateTime.now().plusHours(2);


    public void init() {
        userDao.save(user1);
        userDao.save(user2);
        itemDao.save(item1);
        itemDao.save(item2);
        itemDao.save(item3);
    }

    void setUpBooking() {
        mapper.registerModule(new JavaTimeModule());
        user1 = User.builder()
                .email("test@test.ru")
                .name("Test")
                .build();
        userOutDTO1 = UserOutDTO.builder()
                .email("test@test.ru")
                .name("Test")
                .build();

        user2 = User.builder()
                .email("test1@test.com")
                .name("Test1")
                .build();

        item1 = Item.builder()
                .name("Дрель")
                .description("Простая дрель")
                .available(true)
                .owner(user1.toBuilder().id(1L).build())
                .build();
        item2 = Item.builder()
                .name("Отвертка")
                .description("Крестовая отвертка")
                .available(true)
                .owner(user2.toBuilder().id(2L).build())
                .build();
        itemOutDTO2 = ItemOutDTO.builder()
                .id(itemId2)
                .name("Отвертка")
                .description("Крестовая отвертка")
                .available(true)
                .build();
        item3 = Item.builder()
                .name("Стул")
                .description("Пластиковый стул")
                .available(false)
                .owner(user1.toBuilder().id(1L).build())
                .build();

        bookingDTO = BookingDTO.builder()
                .start(startTime)
                .end(endTime)
                .itemId(itemId2)
                .build();

        bookingOutDTO = BookingOutDTO.builder()
                .id(bookingId1)
                .start(startTime)
                .end(endTime)
                .status(Status.WAITING)
                .booker(userOutDTO1)
                .item(itemOutDTO2)
                .build();
    }

    @Test
    @Order(0)
    @SneakyThrows
    public void testCreateBooking_ResulStatusCreated() {
        setUpBooking();
        init();

        mvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", userId1)
                        .content(mapper.writeValueAsString(bookingDTO))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("id", is(bookingOutDTO.getId()), Long.class))
                .andReturn();


    }

    @Test
    @Order(1)
    @SneakyThrows
    public void testGetBookingById_ResulStatusOk() {
        setUpBooking();

        mvc.perform(get("/bookings/{bookingId}", bookingId1)
                        .header("X-Sharer-User-Id", userId1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("id", is(bookingOutDTO.getId()), Long.class))
                .andReturn();

    }

    @Test
    @Order(2)
    @SneakyThrows
    public void testUpdateBooking_WithInvalidUserId_ResulStatusNotFound() {
        setUpBooking();

        mvc.perform(patch("/bookings/{bookingId}", bookingId1)
                        .header("X-Sharer-User-Id", invalidId)
                        .param("approved", "true")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertEquals(Objects.requireNonNull(result.getResolvedException()).getClass(),
                        NotFoundException.class));

    }

    @Test
    @Order(3)
    @SneakyThrows
    public void testUpdateBooking_WithInvalidBookingId_ResulStatusNotFound() {
        setUpBooking();

        mvc.perform(patch("/bookings/{bookingId}", invalidId)
                        .header("X-Sharer-User-Id", userId1)
                        .param("approved", "true")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertEquals(Objects.requireNonNull(result.getResolvedException()).getClass(),
                        NotFoundException.class));

    }

    @Test
    @Order(4)
    @SneakyThrows
    public void testUpdateBooking_WithUserNotItemOwner_ResulStatusNotFound() {
        setUpBooking();

        mvc.perform(patch("/bookings/{bookingId}", bookingId1)
                        .header("X-Sharer-User-Id", userId1)
                        .param("approved", "true")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertEquals(Objects.requireNonNull(result.getResolvedException()).getClass(),
                        NotFoundException.class));

    }

    @Test
    @Order(5)
    @SneakyThrows
    public void testUpdateBooking_WithRejected_ResulStatusOk() {
        setUpBooking();

        bookingOutDTO = bookingOutDTO.toBuilder()
                .status(Status.REJECTED)
                .build();

        mvc.perform(patch("/bookings/{bookingId}", bookingId1)
                        .header("X-Sharer-User-Id", userId2)
                        .param("approved", "false")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("id", is(bookingOutDTO.getId()), Long.class))
                .andExpect(jsonPath("status", is(bookingOutDTO.getStatus().toString()), String.class))
                .andReturn();

    }

    @Test
    @Order(6)
    @SneakyThrows
    public void testUpdateBooking_WithApproved_ResulStatusOk() {
        setUpBooking();
        bookingOutDTO = bookingOutDTO.toBuilder()
                .status(Status.APPROVED)
                .build();

        mvc.perform(patch("/bookings/{bookingId}", bookingId1)
                        .header("X-Sharer-User-Id", userId2)
                        .param("approved", "true")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("id", is(bookingOutDTO.getId()), Long.class))
                .andExpect(jsonPath("status", is(bookingOutDTO.getStatus().toString()), String.class))
                .andReturn();

    }

    @Test
    @Order(7)
    @SneakyThrows
    public void testUpdateBooking_WithRejectedAfterApproved_ResulStatusBadRequest() {
        setUpBooking();

        mvc.perform(patch("/bookings/{bookingId}", bookingId1)
                        .header("X-Sharer-User-Id", userId2)
                        .param("approved", "false")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertEquals(Objects.requireNonNull(result.getResolvedException()).getClass(),
                        BadRequestException.class));

    }

    @Test
    @Order(8)
    @SneakyThrows
    public void testGetAllBookings_WithUserBookerAndStateAll_ResulStatusOk() {
        setUpBooking();

        bookingOutDTO = BookingOutDTO.builder()
                .id(bookingId1)
                .build();
        List<BookingOutDTO> bookings = Collections.singletonList(bookingOutDTO);

        mvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", userId1)
                        .param("state", "ALL")
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @Order(9)
    @SneakyThrows
    public void testGetAllBookings_WithUserBookerAndStateCurrent_ResulStatusOk() {
        setUpBooking();

        mvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", userId1)
                        .param("state", "CURRENT")
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(List.of())));

    }

    @Test
    @Order(10)
    @SneakyThrows
    public void testGetAllBookings_WithUserBookerAndStatePast_ResulStatusOk() {
        setUpBooking();

        mvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", userId1)
                        .param("state", "PAST")
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(List.of())));

    }

    @Test
    @Order(11)
    @SneakyThrows
    public void testGetAllBookings_WithUserBookerAndStateFuture_ResulStatusOk() {
        setUpBooking();

        bookingOutDTO = BookingOutDTO.builder()
                .id(bookingId1)
                .build();
        List<BookingOutDTO> bookings = Collections.singletonList(bookingOutDTO);

        mvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", userId1)
                        .param("state", "FUTURE")
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @Order(12)
    @SneakyThrows
    public void testGetAllBookings_WithUserBookerAndStateWaiting_ResulStatusOk() {
        setUpBooking();

        mvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", userId1)
                        .param("state", "WAITING")
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(List.of())));

    }

    @Test
    @Order(13)
    @SneakyThrows
    public void testGetAllBookings_WithUserBookerAndStateRejected_ResulStatusOk() {
        setUpBooking();

        mvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", userId1)
                        .param("state", "REJECTED")
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(List.of())));

    }

    @Test
    @Order(14)
    @SneakyThrows
    public void testGetAllBookings_WithUserBookerAndStateUnknown_ResulStatusBadRequest() {
        setUpBooking();

        mvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", userId1)
                        .param("state", "Unknown")
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertEquals(Objects.requireNonNull(result.getResolvedException()).getClass(),
                        BadRequestException.class));

    }

    @Test
    @Order(15)
    @SneakyThrows
    public void testGetBookingById_WithInvalidBookingId_ResulStatusNotFound() {
        setUpBooking();

        mvc.perform(get("/bookings/{bookingId}", invalidId)
                        .header("X-Sharer-User-Id", userId1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertEquals(Objects.requireNonNull(result.getResolvedException()).getClass(),
                        NotFoundException.class));

    }


    @Test
    @Order(16)
    @SneakyThrows
    public void testGetAllBookings_WithUserItemOwner_ResulStatusOk() {
        setUpBooking();

        bookingOutDTO = BookingOutDTO.builder()
                .id(bookingId1)
                .build();
        List<BookingOutDTO> bookings = Collections.singletonList(bookingOutDTO);

        mvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", userId2)
                        .param("state", "ALL")
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @Order(17)
    @SneakyThrows
    public void testGetAllBookings_WithUserItemOwnerAndStateCurrent_ResulStatusOk() {
        setUpBooking();

        mvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", userId2)
                        .param("state", "CURRENT")
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(List.of())));

    }

    @Test
    @Order(18)
    @SneakyThrows
    public void testGetAllBookings_WithUserItemOwnerAndStatePast_ResulStatusOk() {
        setUpBooking();

        mvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", userId2)
                        .param("state", "PAST")
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(List.of())));

    }

    @Test
    @Order(19)
    @SneakyThrows
    public void testGetAllBookings_WithUserItemOwnerAndStateFuture_ResulStatusOk() {
        setUpBooking();

        bookingOutDTO = BookingOutDTO.builder()
                .id(bookingId1)
                .build();
        List<BookingOutDTO> bookings = Collections.singletonList(bookingOutDTO);

        mvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", userId2)
                        .param("state", "FUTURE")
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @Order(20)
    @SneakyThrows
    public void testGetAllBookings_WithUserItemOwnerAndStateWaiting_ResulStatusOk() {
        setUpBooking();

        mvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", userId2)
                        .param("state", "WAITING")
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(List.of())));

    }

    @Test
    @Order(21)
    @SneakyThrows
    public void testGetAllBookings_WithUserItemOwnerAndStateRejected_ResulStatusOk() {
        setUpBooking();

        mvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", userId2)
                        .param("state", "REJECTED")
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(List.of())));

    }

    @Test
    @Order(22)
    @SneakyThrows
    public void testGetAllBookings_WithUserItemOwnerAndStateUnknown_ResulStatusBadRequest() {
        setUpBooking();

        mvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", userId2)
                        .param("state", "Unknown")
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertEquals(Objects.requireNonNull(result.getResolvedException()).getClass(),
                        BadRequestException.class));

    }

    @Test
    @Order(23)
    @SneakyThrows
    public void testGetAllBookings_WithUserItemOwnerInvalidId_ResulStatusNotFound() {
        setUpBooking();

        mvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", invalidId)
                        .param("state", "ALL")
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertEquals(Objects.requireNonNull(result.getResolvedException()).getClass(),
                        NotFoundException.class));

    }

    @Test
    @Order(24)
    @SneakyThrows
    public void testCreateBooking_WithUserItemBooker_ResulStatusNotFound() {
        setUpBooking();
        bookingDTO = BookingDTO.builder()
                .start(startTime)
                .end(endTime)
                .itemId(itemId1)
                .build();

        mvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", userId1)
                        .content(mapper.writeValueAsString(bookingDTO))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertEquals(Objects.requireNonNull(result.getResolvedException()).getClass(),
                        NotFoundException.class));

    }

    @Test
    @Order(25)
    @SneakyThrows
    public void testCreateBooking_WithItemIdInvalid_ResulStatusNotFound() {
        setUpBooking();
        bookingDTO = BookingDTO.builder()
                .start(startTime)
                .end(endTime)
                .itemId(invalidId)
                .build();

        mvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", userId2)
                        .content(mapper.writeValueAsString(bookingDTO))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertEquals(Objects.requireNonNull(result.getResolvedException()).getClass(),
                        NotFoundException.class));

    }

    @Test
    @Order(26)
    @SneakyThrows
    public void testCreateBooking_WithStartAfterEndTime_ResulStatusBadRequest() {
        setUpBooking();
        bookingDTO = BookingDTO.builder()
                .start(LocalDateTime.now().plusDays(2))
                .end(LocalDateTime.now().plusDays(1))
                .itemId(itemId1)
                .build();

        mvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", userId2)
                        .content(mapper.writeValueAsString(bookingDTO))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertEquals(Objects.requireNonNull(result.getResolvedException()).getClass(),
                        BadRequestException.class));

    }

    @Test
    @Order(27)
    @SneakyThrows
    public void testCreateBooking_WithStartEqualsEndTime_ResulStatusBadRequest() {
        setUpBooking();
        LocalDateTime startAndEndTime = LocalDateTime.now().plusDays(1);
        bookingDTO = BookingDTO.builder()
                .start(startAndEndTime)
                .end(startAndEndTime)
                .itemId(itemId1)
                .build();

        mvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", userId2)
                        .content(mapper.writeValueAsString(bookingDTO))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertEquals(Objects.requireNonNull(result.getResolvedException()).getClass(),
                        BadRequestException.class));

    }

    @Test
    @Order(28)
    @SneakyThrows
    public void testCreateBooking_WithItemStatusNotAvailable_ResulStatusBadRequest() {
        setUpBooking();
        bookingDTO = BookingDTO.builder()
                .start(startTime)
                .end(endTime)
                .itemId(itemId3)
                .build();

        mvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", userId2)
                        .content(mapper.writeValueAsString(bookingDTO))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertEquals(Objects.requireNonNull(result.getResolvedException()).getClass(),
                        BadRequestException.class));

    }
}
