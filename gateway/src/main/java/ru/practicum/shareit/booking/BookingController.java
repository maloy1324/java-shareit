package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDTO;
import ru.practicum.shareit.exception.BadRequestException;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Validated
@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingClient bookingClient;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> createBooking(@RequestHeader("X-Sharer-User-Id") Long bookerId,
                                                @RequestBody @Valid BookingDTO bookingDTO) {
        return bookingClient.createBooking(bookerId, bookingDTO);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                                             @PathVariable Long bookingId) {
        return bookingClient.getBooking(userId, bookingId);
    }

    @GetMapping
    public ResponseEntity<Object> getUserBookings(@RequestHeader("X-Sharer-User-Id") Long bookerId,
                                                  @RequestParam(required = false, defaultValue = "ALL") String state,
                                                  @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                                  @RequestParam(defaultValue = "10") @Positive Integer size) {
        validState(state);
        State validatedState = validState(state);
        return bookingClient.getUserBookings(bookerId, validatedState, from, size);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getOwnerBookings(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                                                   @RequestParam(required = false, defaultValue = "ALL") String state,
                                                   @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                                   @RequestParam(defaultValue = "10") @Positive Integer size) {
        validState(state);
        State validatedState = validState(state);
        return bookingClient.getOwnerBookings(ownerId, validatedState, from, size);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> updateBookingStatus(@RequestHeader("X-Sharer-User-Id") Long bookerId,
                                                      @PathVariable Long bookingId,
                                                      @RequestParam boolean approved) {
        return bookingClient.updateBookingStatus(bookerId, bookingId, approved);
    }

    private State validState(String state) {
        try {
            return State.valueOf(state);
        } catch (IllegalArgumentException e) {
            throw new BadRequestException(String.format("Unknown state: %s", state));
        }
    }
}
