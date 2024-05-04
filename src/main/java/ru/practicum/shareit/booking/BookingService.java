package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDTO;
import ru.practicum.shareit.booking.dto.BookingOutDTO;

import java.util.List;

public interface BookingService {
    BookingOutDTO createBooking(BookingDTO bookingDTO);

    List<BookingOutDTO> getUserBookings(Long userId, String state);

    List<BookingOutDTO> getOwnerBookings(Long ownerId, String state);

    BookingOutDTO updateBookingStatus(Long bookerId, Long bookingId, boolean approved);

    BookingOutDTO getBooking(Long userId, Long bookingId);
}
