package ru.practicum.shareit.booking.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.item.dto.ItemOutDTO;
import ru.practicum.shareit.user.dto.UserOutDTO;

import java.time.LocalDateTime;

@Data
@Builder(toBuilder = true)
public class BookingOutDTO {
    private Long id;

    private LocalDateTime start;

    private LocalDateTime end;

    private Status status;

    private UserOutDTO booker;

    private ItemOutDTO item;
}
