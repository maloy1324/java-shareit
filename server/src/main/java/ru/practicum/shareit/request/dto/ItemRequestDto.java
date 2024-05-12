package ru.practicum.shareit.request.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.item.dto.ItemOutDTO;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder(toBuilder = true)
public class ItemRequestDto {
    private Long id;
    @NotBlank(message = "description field must not be empty")
    private String description;
    private LocalDateTime created;
    private List<ItemOutDTO> items;
}
