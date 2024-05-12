package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dao.ItemDao;
import ru.practicum.shareit.item.dto.ItemDTO;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dao.UserDao;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {

    @Mock
    private ItemDao itemDao;

    @Mock
    private ItemMapper itemMapper;

    @Mock
    private UserDao userDao;

    @InjectMocks
    private ItemServiceImpl itemService;

    @Test
    void testUpdateItem_WhenOwnerNotFound_ShouldThrowNotFoundException() {
        Long itemId = 1L;
        Long ownerId = 1L;

        Item item = Item.builder()
                .name("Updated Item")
                .description("Updated Description")
                .available(true)
                .build();

        ItemDTO itemDto = ItemDTO.builder()
                .name("Updated Item")
                .description("Updated Description")
                .available(true)
                .build();
        when(itemDao.findById(itemId)).thenReturn(Optional.of(item));
        when(itemMapper.toDTO(item)).thenReturn(itemDto);
        when(userDao.existsById(ownerId)).thenReturn(false);

        assertThrows(NotFoundException.class, () ->
                itemService.updateItem(itemId, ownerId, itemDto)
        );
    }
}