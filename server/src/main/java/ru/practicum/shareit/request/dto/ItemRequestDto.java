package ru.practicum.shareit.request.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.item.dto.ItemOutDTO;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder(toBuilder = true)
public class ItemRequestDto {
    private Long id;
    private String description;
    private LocalDateTime created;
    private List<ItemOutDTO> items;
}
