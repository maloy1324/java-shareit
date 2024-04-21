package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDTO;

import java.util.List;

public interface ItemService {
    ItemDTO addItem(Long ownerId, ItemDTO itemDto);

    ItemDTO updateItem(Long itemId, Long ownerId, ItemDTO itemDto);

    ItemDTO getItemById(Long itemId);

    List<ItemDTO> getAllItemsByOwner(Long ownerId);

    List<ItemDTO> searchItems(String text);

    void deleteItem(Long itemId);
}
