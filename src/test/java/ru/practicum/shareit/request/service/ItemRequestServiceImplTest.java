package ru.practicum.shareit.request.service;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dao.ItemDao;
import ru.practicum.shareit.item.dto.ItemOutDTO;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dao.ItemRequestDao;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapping.ItemRequestMapping;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.dao.UserDao;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemRequestServiceImplTest {

    @Mock
    private ItemRequestDao itemRequestDao;

    @Mock
    private UserDao userDao;

    @Mock
    private ItemDao itemDao;

    @Mock
    private ItemRequestMapping itemRequestMapping;

    @Mock
    private ItemMapper itemMapper;

    @InjectMocks
    private ItemRequestServiceImpl itemRequestService;

    private ItemRequest itemRequest;
    private ItemRequestDto itemRequestDto;
    private Item item;
    private ItemOutDTO itemOutDTO;
    private final Long userId = 1L;
    private final Long itemRequestId = 1L;
    private final Long invalidId = 999L;
    private final LocalDateTime now = LocalDateTime.now();

    void setUp() {
        item = Item.builder()
                .id(1L)
                .requestId(itemRequestId)
                .build();

        itemOutDTO = ItemOutDTO.builder()
                .id(1L)
                .requestId(itemRequestId)
                .build();

        itemRequest = ItemRequest.builder()
                .id(itemRequestId)
                .description("Хотел бы воспользоваться щёткой для обуви")
                .created(now)
                .requester(User.builder()
                        .id(userId)
                        .name("Test")
                        .email("Test@test.com")
                        .build())
                .build();

        itemRequestDto = ItemRequestDto.builder()
                .id(itemRequestId)
                .description("Хотел бы воспользоваться щёткой для обуви")
                .created(now)
                .items(new ArrayList<>())
                .build();
    }

    @Test
    @SneakyThrows
    void testCreateItemRequest_ReturnItemRequestDto() {
        setUp();
        User user = User.builder()
                .id(userId)
                .name("Test")
                .email("Test@test.com")
                .build();
        when(itemRequestMapping.fromDto(itemRequestDto)).thenReturn(itemRequest);
        when(userDao.findById(userId)).thenReturn(Optional.of(user));
        when(itemRequestDao.save(itemRequest)).thenReturn(itemRequest);
        when(itemRequestMapping.toDto(itemRequest)).thenReturn(itemRequestDto);
        assertEquals(itemRequestDto, itemRequestService.create(userId, itemRequestDto));
    }

    @Test
    @SneakyThrows
    void testCreateItemRequest_WithInvalidUserId_ReturnNotFoundException() {
        assertThrows(NotFoundException.class, () -> itemRequestService.create(invalidId, itemRequestDto));
    }

    @Test
    @SneakyThrows
    void testGetItemRequest_ByRequestId_ReturnItemRequestDto() {
        setUp();
        when(userDao.existsById(userId)).thenReturn(true);
        when(itemRequestDao.findById(itemRequestId)).thenReturn(Optional.of(itemRequest));
        when(itemRequestMapping.toDto(itemRequest)).thenReturn(itemRequestDto);
        when(itemDao.findAllByRequestId(itemRequestId)).thenReturn(List.of());
        assertEquals(itemRequestDto, itemRequestService.findById(userId, itemRequestId));
    }

    @Test
    @SneakyThrows
    void testGetItemRequest_ByInvalidRequestId_ReturnNotFoundException() {
        when(userDao.existsById(userId)).thenReturn(true);
        when(itemRequestDao.findById(invalidId)).thenThrow(NotFoundException.class);
        assertThrows(NotFoundException.class, () -> itemRequestService.findById(userId, invalidId));
    }

    @Test
    @SneakyThrows
    void testGetAllItemRequest_ByRequesterId_ReturnListOfItemRequestDto() {
        setUp();
        User user = new User();
        user.setId(1L);
        user.setEmail("test@test.com");
        user.setName("Test");

        when(userDao.existsById(user.getId())).thenReturn(true);
        when(itemRequestDao.findAllByRequesterId(user.getId())).thenReturn(List.of(itemRequest));
        when(itemDao.findAllByRequestIdIn(List.of(1L))).thenReturn(List.of(item));
        when(itemRequestMapping.toDto(itemRequest)).thenReturn(itemRequestDto);
        when(itemMapper.toOutDTO(item)).thenReturn(itemOutDTO);
        List<ItemRequestDto> items = itemRequestService.findAllByUser(user.getId());
        assertNotNull(items);
        assertEquals(1, items.size());
    }

    @Test
    @SneakyThrows
    void testGetAllItemRequestFromOtherUsers_ByPageParam_ReturnListOfItemRequestDto() {
        Long userId = 1L;
        Integer from = 0;
        Integer size = 10;
        List<ItemRequest> itemRequests = Collections.emptyList();
        Page<ItemRequest> pagedResponse = new PageImpl<>(itemRequests);

        when(userDao.existsById(userId)).thenReturn(true);
        when(itemRequestDao.findAllByRequesterIdNot(eq(userId), any(Pageable.class))).thenReturn(pagedResponse);

        List<ItemRequestDto> result = itemRequestService.findAllByOtherUsers(userId, from, size);

        assertEquals(Collections.emptyList(), result);
    }

    @Test
    void getAllMineRequests_NotExistUserTest() {
        Long userId = 100L;
        assertThrows(NotFoundException.class, () -> itemRequestService.findAllByUser(userId));
    }

    @Test
    void getAllItemRequests_InvalidUserId_ExceptionThrown() {
        Long userId = 2L;
        when(userDao.existsById(userId)).thenReturn(false);
        assertThrows(NotFoundException.class, () -> itemRequestService.findAllByOtherUsers(userId, 0, 5));
    }

    @Test
    void testFindById_UserNotFound() {
        Long userId = 999L;
        when(userDao.existsById(userId)).thenReturn(false);
        assertThrows(NotFoundException.class, () -> itemRequestService.findById(userId, 1L));
    }
}