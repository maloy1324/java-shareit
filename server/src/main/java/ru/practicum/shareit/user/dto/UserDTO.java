package ru.practicum.shareit.user.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true)
public class UserDTO {
    private Long id;
    private String name;
    private String email;
}
