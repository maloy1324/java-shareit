package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDTO;
import ru.practicum.shareit.user.dto.UserOutDTO;
import ru.practicum.shareit.user.service.UserService;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ActiveProfiles("test")
@WebMvcTest(controllers = UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private UserService service;

    private UserDTO userDTO;
    private UserOutDTO userOutDTO;
    private final Long userId = 1L;
    private final Long invalidId = 999L;
    private final NotFoundException notFoundException = new NotFoundException("Exception");

    void setUp() {

        userDTO = UserDTO.builder()
                .id(null)
                .email("test@test.com")
                .name("Test")
                .build();
        userOutDTO = UserOutDTO.builder()
                .id(userId)
                .email("test@test.com")
                .name("Test")
                .build();
    }

    @Test
    @SneakyThrows
    void testCreateUser_ResultStatusCreated() {
        setUp();

        when(service.createUser(any(UserDTO.class))).thenReturn(userOutDTO);

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userDTO))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().json(mapper.writeValueAsString(userOutDTO)));

        verify(service, times(1)).createUser(any(UserDTO.class));
    }

    @Test
    @SneakyThrows
    void testGetUser_ById_ResultStatusOk() {
        setUp();

        when(service.getUserById(anyLong())).thenReturn(userOutDTO);

        mvc.perform(get("/users/{userId}", userId))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(userOutDTO)));

        verify(service, times(1)).getUserById(anyLong());
    }

    @Test
    @SneakyThrows
    void testGetUser_ByInvalidId_ResultStatusNotFound() {
        setUp();

        when(service.getUserById(anyLong())).thenThrow(notFoundException);

        mvc.perform(get("/users/{userId}", invalidId))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertEquals(Objects.requireNonNull(result.getResolvedException()).getClass(),
                        NotFoundException.class));


        verify(service, times(1)).getUserById(invalidId);
    }

    @Test
    @SneakyThrows
    void testGetAllUsers_ResultStatusOk() {
        UserOutDTO userOutDTO1 = UserOutDTO.builder()
                .id(1L)
                .email("updateTest@test.com")
                .name("Test")
                .build();

        UserOutDTO userOutDTO2 = UserOutDTO.builder()
                .id(3L)
                .email("Test@test.com")
                .name("updateTest")
                .build();

        List<UserOutDTO> userList = Arrays.asList(userOutDTO1, userOutDTO2);

        when(service.getAllUsers()).thenReturn(userList);

        mvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(userList)));

        verify(service, times(1)).getAllUsers();
    }

    @Test
    @SneakyThrows
    void testDeleteUser_ResultStatusNoContent() {
        setUp();

        mvc.perform(delete("/users/{userId}", userId))
                .andExpect(status().isNoContent());

        verify(service, times(1)).deleteUser(userId);
    }
}