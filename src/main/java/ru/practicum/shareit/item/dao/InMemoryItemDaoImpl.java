package ru.practicum.shareit.item.dao;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
public class InMemoryItemDaoImpl implements ItemDao {

    private final Map<Long, Item> items = new HashMap<>();
    private Long nextItemId = 1L;

    @Override
    public Item addItem(Long ownerId, Item item) {
        Long itemId = nextItemId++;
        item.setId(itemId);
        item.setOwnerId(ownerId);
        items.put(itemId, item);
        return item;
    }

    @Override
    public Item getItemById(Long itemId) {
        return items.get(itemId);
    }

    @Override
    public List<Item> getAllItemsByOwner(Long ownerId) {
        return items.values().stream()
                .filter(item -> item.getOwnerId().equals(ownerId))
                .collect(Collectors.toList());
    }

    @Override
    public List<Item> searchItems(String text) {
        return items.values().stream()
                .filter(item -> (item.getName().toLowerCase().contains(text.toLowerCase())
                        || item.getDescription().toLowerCase().contains(text.toLowerCase())) && item.getAvailable())
                .collect(Collectors.toList());
    }

    @Override
    public Item updateItem(Long itemId, Long ownerId, Item item) {
        Item existingItem = items.get(itemId);
        Item updatedItem = Item.builder()
                .id(itemId)
                .ownerId(ownerId)
                .name(existingItem.getName())
                .description(existingItem.getDescription())
                .available(existingItem.getAvailable())
                .build();
        if (item.getName() != null) {
            updatedItem.setName(item.getName());
        }
        if (item.getDescription() != null) {
            updatedItem.setDescription(item.getDescription());
        }
        if (item.getAvailable() != null) {
            updatedItem.setAvailable(item.getAvailable());
        }
        items.put(itemId, updatedItem);
        return updatedItem;
    }

    @Override
    public void deleteItem(Long itemId) {
        items.remove(itemId);
    }

    @Override
    public boolean isExists(Long id) {
        return items.containsKey(id);
    }
}
