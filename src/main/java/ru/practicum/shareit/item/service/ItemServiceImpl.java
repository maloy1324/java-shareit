package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dao.ItemDao;
import ru.practicum.shareit.item.dto.ItemDTO;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dao.UserDao;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemDao itemDao;
    private final ItemMapper itemMapper;
    private final UserDao userDao;

    @Override
    public ItemDTO addItem(Long ownerId, ItemDTO itemDto) {
        if (!userDao.isExists(ownerId)) {
            throw new NotFoundException("Пользователь с таким ID не найден.");
        }
        return itemMapper.toDTO(itemDao.addItem(ownerId, itemMapper.toModel(itemDto)));
    }

    @Override
    public ItemDTO updateItem(Long itemId, Long ownerId, ItemDTO itemDto) {
        if (ownerId == null || itemDto == null) {
            throw new BadRequestException("Переданы неверные данные");
        }
        if (!userDao.isExists(ownerId)) {
            throw new NotFoundException("Пользователь с таким ID не найден.");
        }
        if (!itemDao.isExists(itemId)) {
            throw new NotFoundException("Вещь с таким ID не найдена.");
        }
        if (!ownerId.equals(itemDao.getItemById(itemId).getOwnerId())) {
            throw new ForbiddenException("Вы не имеете права редактировать данный предмет");
        }
        Item existingItem = itemDao.getItemById(itemId);
        Item updatedItem = Item.builder()
                .id(itemId)
                .ownerId(ownerId)
                .name(existingItem.getName())
                .description(existingItem.getDescription())
                .available(existingItem.getAvailable())
                .build();
        if (itemDto.getName() != null) {
            updatedItem.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            updatedItem.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            updatedItem.setAvailable(itemDto.getAvailable());
        }
        return itemMapper.toDTO(itemDao.updateItem(updatedItem));
    }

    @Override
    public ItemDTO getItemById(Long itemId) {
        if (!itemDao.isExists(itemId)) {
            throw new NotFoundException("Вещь с таким ID не найдена.");
        }
        return itemMapper.toDTO(itemDao.getItemById(itemId));
    }

    @Override
    public List<ItemDTO> getAllItemsByOwner(Long ownerId) {
        if (!userDao.isExists(ownerId)) {
            throw new NotFoundException("Пользователь с таким ID не найден.");
        }
        return itemMapper.toListDTO(itemDao.getAllItemsByOwner(ownerId));
    }

    @Override
    public List<ItemDTO> searchItems(String text) {
        if (text == null || text.isEmpty()) {
            return new ArrayList<>();
        }
        return itemMapper.toListDTO(itemDao.searchItems(text));
    }

    @Override
    public void deleteItem(Long itemId) {
        if (!itemDao.isExists(itemId)) {
            throw new NotFoundException("Вещь с таким ID не найдена.");
        }
        itemDao.deleteItem(itemId);
    }
}

