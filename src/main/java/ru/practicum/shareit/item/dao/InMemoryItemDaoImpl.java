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
    public Item updateItem(Item item) {
        items.put(item.getId(), item);
        return item;
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
