package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.dao.UserDao;
import ru.practicum.shareit.user.dto.UserDTO;
import ru.practicum.shareit.user.dto.UserOutDTO;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserDao userDao;
    private final UserMapper userMapper;

    @Override
    public UserOutDTO createUser(UserDTO userDto) {
        return userMapper.toOutDTO(userDao.save(userMapper.toModel(userDto)));
    }

    @Override
    public UserOutDTO getUserById(Long id) {
        return userMapper.toOutDTO(userDao.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found")));
    }

    @Override
    public List<UserOutDTO> getAllUsers() {
        return userMapper.toListOutDTO(userDao.findAll());
    }

    @Override
    public UserOutDTO updateUser(Long id, UserDTO userDto) {
        User userFromDB = userDao.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found"));

        userDto.setId(userFromDB.getId());
        if (Objects.isNull(userDto.getName())) {
            userDto.setName(userFromDB.getName());
        }
        if (Objects.isNull(userDto.getEmail())) {
            userDto.setEmail(userFromDB.getEmail());
        }

        return userMapper.toOutDTO(userDao.save(userMapper.toModel(userDto)));
    }

    @Override
    public void deleteUser(Long userId) {
        if (!userDao.existsById(userId)) {
            throw new NotFoundException("User not found");
        }
        userDao.deleteById(userId);
    }
}