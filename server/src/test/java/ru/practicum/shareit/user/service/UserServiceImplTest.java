package ru.practicum.shareit.user.service;

import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.dao.UserDao;
import ru.practicum.shareit.user.dto.UserDTO;
import ru.practicum.shareit.user.dto.UserOutDTO;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {
    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private UserDao userDao;

    @Mock
    private UserMapper userMapper;

    private User user;

    private UserDTO userDto;

    private UserOutDTO userOutDto;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .name("vvvvv")
                .email("vvvvv@vvvvvvv.ru")
                .build();

        userDto = UserDTO.builder()
                .id(1L)
                .name("vvvvv")
                .email("vvvvv@vvvvvvv.ru")
                .build();

        userOutDto = UserOutDTO.builder()
                .id(1L)
                .name("vvvvv")
                .email("vvvvv@vvvvvvv.ru")
                .build();
    }

    @AfterEach
    void afterFinish() {
        userDao.deleteAll();
    }

    @Test
    @SneakyThrows
    void createUserTest() {
        when(userMapper.toModel(userDto)).thenReturn(user);
        when(userDao.save(user)).thenReturn(user);
        when(userMapper.toOutDTO(user)).thenReturn(userOutDto);
        UserOutDTO result = userService.createUser(userDto);
        assertNotNull(result);
        assertEquals(userOutDto.getId(), result.getId());
    }

    @Test
    public void testUpdateUser_userNotFoundTest() {
        Long userId = 100L;
        when(userDao.findById(userId)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> userService.updateUser(userId, userDto));
    }

    @Test
    @SneakyThrows
    void getUserByIdTest() {
        when(userDao.findById(userOutDto.getId())).thenReturn(Optional.of(user));
        when(userMapper.toOutDTO(user)).thenReturn(userOutDto);
        UserOutDTO result = userService.getUserById(userOutDto.getId());
        assertNotNull(result);
        assertEquals(userOutDto.getId(), result.getId());
    }

    @Test
    void testGetUser_userNotFoundTest() {
        Long userId = 1L;
        when(userDao.findById(userId)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> userService.getUserById(userId));
    }

    @Test
    @SneakyThrows
    void getAllUsersTest() {
        when(userDao.findAll()).thenReturn(List.of(user));
        when(userMapper.toListOutDTO(List.of(user))).thenReturn(List.of(userOutDto));
        List<UserDTO> users = List.of(userDto);
        assertNotNull(users);
        assertEquals(users.size(), userService.getAllUsers().size());
    }

    @Test
    @SneakyThrows
    void deleteUserTest() {
        when(userDao.existsById(user.getId())).thenReturn(true);
        userService.deleteUser(user.getId());
    }

    @Test
    @SneakyThrows
    void testUpdateUser_WhenEmailIsNull_ShouldUseEmailFromDatabase() {
        Long userId = 1L;
        String updatedName = "Updated Name";
        String databaseEmail = "test@example.com";

        UserDTO userDto = UserDTO.builder()
                .name(updatedName)
                .email(null)
                .build();

        User userFromDB = new User();
        userFromDB.setId(userId);
        userFromDB.setName("Original Name");
        userFromDB.setEmail(databaseEmail);

        when(userDao.findById(userId)).thenReturn(Optional.of(userFromDB));
        userService.updateUser(userId, userDto);
        assertEquals(databaseEmail, userDto.getEmail());
    }
}
