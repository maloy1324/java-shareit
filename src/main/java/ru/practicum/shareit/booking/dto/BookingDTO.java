package ru.practicum.shareit.booking.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.Status;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@Builder(toBuilder = true)
public class BookingDTO {
    private Long id;

    @FutureOrPresent(message = "")
    @NotNull
    private LocalDateTime start;

    @Future(message = "")
    @NotNull
    private LocalDateTime end;

    private Status status;

    private Long bookerId;

    @NotNull
    private Long itemId;
}