package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.MethodArgumentNotValidException;
import ru.practicum.shareit.booking.dto.BookingDTO;
import ru.practicum.shareit.booking.dto.BookingOutDTO;
import ru.practicum.shareit.item.dto.ItemOutDTO;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.dto.UserOutDTO;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@WebMvcTest(controllers = BookingController.class)
class BookingControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private BookingService service;

    private User user;
    private UserOutDTO userOutDTO;

    private ItemOutDTO itemOutDTO;

    private BookingDTO bookingDTO;
    private BookingOutDTO bookingOutDTO;

    private ItemRequest itemRequest;

    private final Long userId = 1L;
    private final Long itemId = 1L;
    private final Long commentId = 1L;
    private final Long bookingId = 1L;
    private final Long itemRequestId = 1L;
    private final LocalDateTime startTime = LocalDateTime.now().plusHours(1);
    private final LocalDateTime endTime = LocalDateTime.now().plusHours(2);

    void setUp() {
        setUpUser();
        setUpItem();
        setUpBooking();
        setUpItemRequest();
    }

    void setUpUser() {
        user = User.builder()
                .id(userId)
                .email("ruru@yandex.ru")
                .name("RuRu")
                .build();
        userOutDTO = UserOutDTO.builder()
                .id(userId)
                .email("ruru@yandex.ru")
                .name("RuRu")
                .build();
    }

    void setUpItem() {
        itemOutDTO = ItemOutDTO.builder()
                .id(itemId)
                .name("Дрель")
                .description("Простая дрель")
                .available(true)
                .lastBooking(null)
                .nextBooking(null)
                .comments(List.of())
                .build();
    }

    void setUpItemRequest() {
        itemRequest = ItemRequest.builder()
                .id(itemRequestId)
                .description("Хотел бы воспользоваться щёткой для обуви")
                .created(startTime)
                .requester(user)
                .build();
    }

    void setUpBooking() {
        bookingDTO = BookingDTO.builder()
                .id(bookingId)
                .start(startTime)
                .end(endTime)
                .status(Status.WAITING)
                .bookerId(userId)
                .itemId(itemId)
                .build();
        bookingOutDTO = BookingOutDTO.builder()
                .id(bookingId)
                .start(startTime)
                .end(endTime)
                .status(Status.WAITING)
                .booker(userOutDTO)
                .item(itemOutDTO)
                .build();
    }

    @Test
    @SneakyThrows
    void testCreateBooking_WithStartTimeEmpty_ResultStatusBadRequest() {

        setUp();

        bookingDTO = bookingDTO.toBuilder()
                .start(null)
                .build();

        mvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", userId)
                        .content(mapper.writeValueAsString(bookingDTO))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertEquals(Objects.requireNonNull(result.getResolvedException()).getClass(),
                        MethodArgumentNotValidException.class));

        verify(service, never()).createBooking(any(BookingDTO.class));

    }

    @Test
    @SneakyThrows
    void testCreateBooking_WithStartTimeInvalid_ResultStatusBadRequest() {

        setUp();

        bookingDTO = bookingDTO.toBuilder()
                .start(LocalDateTime.now().minusDays(1))
                .build();

        mvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", userId)
                        .content(mapper.writeValueAsString(bookingDTO))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertEquals(Objects.requireNonNull(result.getResolvedException()).getClass(),
                        MethodArgumentNotValidException.class));

        verify(service, never()).createBooking(any(BookingDTO.class));

    }

    @Test
    @SneakyThrows
    void testCreateBooking_ResultStatusCreated() {

        setUp();

        when(service.createBooking(any(BookingDTO.class))).thenReturn(bookingOutDTO);

        mvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", userId)
                        .content(mapper.writeValueAsString(bookingDTO))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().json(mapper.writeValueAsString(bookingOutDTO)));

        verify(service, times(1)).createBooking(any(BookingDTO.class));

    }

    @Test
    @SneakyThrows
    void testUpdateBooking_ResultStatusOk() {

        setUp();

        bookingOutDTO = bookingOutDTO.toBuilder()
                .status(Status.APPROVED)
                .build();

        when(service.updateBookingStatus(anyLong(), anyLong(), anyBoolean())).thenReturn(bookingOutDTO);

        mvc.perform(patch("/bookings/{bookingId}", bookingId)
                        .header("X-Sharer-User-Id", userId)
                        .param("approved", "true")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(bookingOutDTO)));

        verify(service, times(1)).updateBookingStatus(anyLong(), anyLong(), anyBoolean());

    }

    @Test
    @SneakyThrows
    void testGetBooking_ResultStatusOk() {

        setUp();

        bookingOutDTO = bookingOutDTO.toBuilder()
                .status(Status.APPROVED)
                .build();

        when(service.getBooking(anyLong(), anyLong())).thenReturn(bookingOutDTO);

        mvc.perform(get("/bookings/{bookingId}", bookingId)
                        .header("X-Sharer-User-Id", userId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(bookingOutDTO)));

        verify(service, times(1)).getBooking(anyLong(), anyLong());

    }
}