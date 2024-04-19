package ru.practicum.shareit.item.dao;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemDao {
    Item addItem(Long ownerId, Item item);

    Item getItemById(Long itemId);

    List<Item> getAllItemsByOwner(Long ownerId);

    List<Item> searchItems(String text);

    Item updateItem(Long itemId, Long ownerId, Item item);

    void deleteItem(Long itemId);

    boolean isExists(Long id);
}
