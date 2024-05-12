package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDTO;
import ru.practicum.shareit.booking.dto.BookingOutDTO;

import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookingOutDTO createBooking(@RequestHeader("X-Sharer-User-Id") Long bookerId,
                                       @RequestBody BookingDTO bookingDTO) {
        bookingDTO.setBookerId(bookerId);
        return bookingService.createBooking(bookingDTO);
    }

    @GetMapping("/{bookingId}")
    public BookingOutDTO getBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                                    @PathVariable Long bookingId) {
        return bookingService.getBooking(userId, bookingId);
    }

    @GetMapping
    public List<BookingOutDTO> getUserBookings(@RequestHeader("X-Sharer-User-Id") Long bookerId,
                                               @RequestParam String state,
                                               @RequestParam Integer from,
                                               @RequestParam Integer size) {
        return bookingService.getUserBookings(bookerId, state, from, size);
    }

    @GetMapping("/owner")
    public List<BookingOutDTO> getOwnerBookings(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                                                @RequestParam String state,
                                                @RequestParam Integer from,
                                                @RequestParam Integer size) {
        return bookingService.getOwnerBookings(ownerId, state, from, size);
    }

    @PatchMapping("/{bookingId}")
    public BookingOutDTO updateBookingStatus(@RequestHeader("X-Sharer-User-Id") Long bookerId,
                                             @PathVariable Long bookingId,
                                             @RequestParam boolean approved) {
        return bookingService.updateBookingStatus(bookerId, bookingId, approved);
    }
}
