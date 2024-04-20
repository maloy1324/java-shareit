package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserDTO;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserMapper {
    UserDTO toDTO(User user);

    User toModel(UserDTO userDTO);

    List<UserDTO> toListDTO(List<User> modelList);

    List<User> toModelList(List<UserDTO> userDTOList);
}
