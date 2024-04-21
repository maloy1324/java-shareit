package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.dao.UserDao;
import ru.practicum.shareit.user.dto.UserDTO;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserDao userDao;
    private final UserMapper userMapper;

    @Override
    public UserDTO createUser(UserDTO userDto) {
        return userMapper.toDTO(userDao.createUser(userMapper.toModel(userDto)));
    }

    @Override
    public UserDTO getUserById(Long userId) {
        valid(userId);
        return userMapper.toDTO(userDao.getUserById(userId));
    }

    @Override
    public List<UserDTO> getAllUsers() {
        return userMapper.toListDTO(userDao.getAllUsers());
    }

    @Override
    public UserDTO updateUser(Long userId, UserDTO userDto) {
        valid(userId);
        return userMapper.toDTO(userDao.updateUser(userMapper.toModel(userDto)));
    }

    @Override
    public UserDTO patchUser(Long userId, UserDTO userDTO) {
        valid(userId);
        User existingUser = userDao.getUserById(userId);
        User updatedUser = User.builder()
                .id(existingUser.getId())
                .name(existingUser.getName())
                .email(existingUser.getEmail())
                .build();
        if (userDTO.getEmail() != null) {
            updatedUser.setEmail(userDTO.getEmail());
        }
        if (userDTO.getName() != null) {
            updatedUser.setName(userDTO.getName());
        }
        userDao.updateUser(updatedUser);
        return userMapper.toDTO(updatedUser);
    }

    @Override
    public void deleteUser(Long userId) {
        valid(userId);
        userDao.deleteUser(userId);
    }

    private void valid(Long id) {
        if (!userDao.isExists(id)) {
            throw new NotFoundException("Пользователь с id " + id + " не найден");
        }
    }
}