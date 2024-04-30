package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDTO;
import ru.practicum.shareit.user.dto.UserOutDTO;

import java.util.List;

public interface UserService {
    UserOutDTO createUser(UserDTO userDto);

    UserOutDTO getUserById(Long userId);

    List<UserOutDTO> getAllUsers();

    UserOutDTO updateUser(Long userId, UserDTO userDto);

    void deleteUser(Long userId);
}
