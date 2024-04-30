package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.dto.BookingShortDTO;

import java.util.List;

@Data
@Builder(toBuilder = true)
public class ItemOutDTO {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private BookingShortDTO lastBooking;
    private BookingShortDTO nextBooking;
    private List<CommentOutDTO> comments;
}
