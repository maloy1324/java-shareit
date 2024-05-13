package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.dto.BookingDTO;
import ru.practicum.shareit.booking.dto.BookingShortDTO;
import ru.practicum.shareit.user.dto.UserOutDTO;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BookingMapperTest {

    @InjectMocks
    private BookingMapperImpl bookingMapper;

    @Mock
    private Booking booking;

    @Mock
    private BookingDTO bookingDTO;

    @Test
    public void testToShortDTO() {
        // Arrange
        when(bookingDTO.getId()).thenReturn(1L);
        when(bookingDTO.getBookerId()).thenReturn(2L);

        // Act
        BookingShortDTO result = bookingMapper.toShortDTO(bookingDTO);

        // Assert
        assertEquals(1L, result.getId());
        assertEquals(2L, result.getBookerId());
    }

    @Test
    public void testUserToUserOutDTO_WithNullUser_ShouldReturnNull() {
        // Arrange
        User user = null;

        // Act
        UserOutDTO result = bookingMapper.userToUserOutDTO(user);

        // Assert
        assertNull(result);
    }

    @Test
    public void testUserToUserOutDTO_WithValidUser_ShouldReturnUserOutDTO() {
        // Arrange
        User user = new User();
        user.setId(1L);
        user.setName("John");
        user.setEmail("john@example.com");

        // Act
        UserOutDTO result = bookingMapper.userToUserOutDTO(user);

        // Assert
        assertEquals(1L, result.getId());
        assertEquals("John", result.getName());
        assertEquals("john@example.com", result.getEmail());
    }

    @Test
    public void testToDTO() {
        // Arrange
        LocalDateTime start = LocalDateTime.of(2024, 5, 11, 10, 0);
        LocalDateTime end = LocalDateTime.of(2024, 5, 11, 12, 0);

        when(booking.getId()).thenReturn(1L);
        when(booking.getStart()).thenReturn(start);
        when(booking.getEnd()).thenReturn(end);
        when(booking.getStatus()).thenReturn(Status.valueOf("APPROVED"));

        // Act
        BookingDTO result = bookingMapper.toDTO(booking);

        // Assert
        assertEquals(1L, result.getId());
        assertEquals(start, result.getStart());
        assertEquals(end, result.getEnd());
        assertEquals("APPROVED", result.getStatus().toString());

        // Verify that each setter method is called with correct argument
        verify(booking).getId();
        verify(booking).getStart();
        verify(booking).getEnd();
        verify(booking).getStatus();
    }

    @Test
    public void testToDTO_ReturnNull() {
        assertNull(bookingMapper.toDTO(null));
    }

    @Test
    public void testToOutDTO_ReturnNull() {
        assertNull(bookingMapper.toOutDTO(null));
    }

    @Test
    public void testToShortDTO_ReturnNull() {
        assertNull(bookingMapper.toShortDTO(null));
    }

    @Test
    public void testToModel_ReturnNull() {
        assertNull(bookingMapper.toModel(null));
    }

    @Test
    public void testToListDTO_ReturnNull() {
        assertNull(bookingMapper.toListDTO(null));
    }

    @Test
    public void testToListOutDTO_ReturnNull() {
        assertNull(bookingMapper.toListOutDTO(null));
    }
}