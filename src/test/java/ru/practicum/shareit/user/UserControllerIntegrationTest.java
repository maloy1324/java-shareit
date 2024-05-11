package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDTO;
import ru.practicum.shareit.user.dto.UserOutDTO;

import java.nio.charset.StandardCharsets;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class UserControllerIntegrationTest {

    @Autowired
    private MockMvc mvc;

    private UserDTO userDTO;
    private UserOutDTO userOutDTO;
    private final ObjectMapper mapper = new ObjectMapper();
    private final Long userId1 = 1L;
    private final Long invalidId = 999L;

    public void setUp() {

        userDTO = UserDTO.builder()
                .email("test@test.com")
                .name("Test")
                .build();
        userOutDTO = UserOutDTO.builder()
                .id(userId1)
                .email("test@test.com")
                .name("Test")
                .build();
    }

    @Test
    @Order(0)
    @SneakyThrows
    public void testCreateUser_ResulStatusCreated() {
        setUp();
        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userDTO))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().json(mapper.writeValueAsString(userOutDTO)));
    }

    @Test
    @Order(1)
    @SneakyThrows
    public void testGetUserById_ResulStatusOk() {
        setUp();

        mvc.perform(get("/users/{userId}", userId1))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(userOutDTO)))
                .andReturn();
    }

    @Test
    @Order(2)
    @SneakyThrows
    public void testGetUserById_WithInvalidId_ResulStatusNotFound() {
        setUp();
        mvc.perform(get("/users/{invalidUserId}", invalidId))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertEquals(Objects.requireNonNull(result.getResolvedException()).getClass(),
                        NotFoundException.class));
    }

    @Test
    @Order(3)
    @SneakyThrows
    public void testUpdateUser_OnlyEmail_ResulStatusOk() {
        setUp();
        UserDTO userDTO = UserDTO.builder().email("updatetest@test.com").build();
        userOutDTO = userOutDTO.toBuilder().email("updatetest@test.com").build();

        mvc.perform(patch("/users/{userId}", userId1)
                        .content(mapper.writeValueAsString(userDTO))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(userOutDTO)));
    }

    @Test
    @Order(4)
    @SneakyThrows
    public void testUpdateUser_WithInvalidId_ResulStatusNotFound() {
        setUp();
        userDTO = userDTO.toBuilder().name("updateTest").build();

        mvc.perform(patch("/users/{userId}", invalidId)
                        .content(mapper.writeValueAsString(userDTO))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertEquals(Objects.requireNonNull(result.getResolvedException()).getClass(),
                        NotFoundException.class));
    }

    @Test
    @Order(5)
    @SneakyThrows
    public void testDeleteUser_ResulStatusOk() {

        mvc.perform(delete("/users/{userId}", userId1))
                .andExpect(status().isNoContent());
    }

    @Test
    @Order(6)
    @SneakyThrows
    public void testDeleteUser_WithInvalidId_ResulStatusOk() {
        mvc.perform(delete("/users/{userId}", userId1))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertEquals(Objects.requireNonNull(result.getResolvedException()).getClass(),
                        NotFoundException.class));
    }
}