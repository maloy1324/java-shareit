package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dao.UserDao;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {

    @Mock
    private UserDao userRepository;

    @InjectMocks
    private BookingServiceImpl bookingService;

    @Test
    void testGetUserBookings_WhenUserNotFound_ShouldThrowNotFoundException() {
        Long userId = 1L;
        State state = State.valueOf("ALL");
        Integer from = 0;
        Integer size = 10;

        when(userRepository.existsById(userId)).thenReturn(false);

        assertThrows(NotFoundException.class, () ->
                bookingService.getUserBookings(userId, state, from, size)
        );
    }
}