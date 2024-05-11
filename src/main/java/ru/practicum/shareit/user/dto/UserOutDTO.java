package ru.practicum.shareit.user.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true)
public class UserOutDTO {
    private Long id;
    private String name;
    private String email;
}
