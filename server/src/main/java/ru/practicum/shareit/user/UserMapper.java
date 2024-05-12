package ru.practicum.shareit.user;

import org.mapstruct.Mapper;
import ru.practicum.shareit.user.dto.UserDTO;
import ru.practicum.shareit.user.dto.UserOutDTO;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserDTO toDTO(User user);

    UserOutDTO toOutDTO(User user);

    User toModel(UserDTO userDTO);

    List<UserOutDTO> toListOutDTO(List<User> modelList);
}
