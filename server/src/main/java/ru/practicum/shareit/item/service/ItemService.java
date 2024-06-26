package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDTO;
import ru.practicum.shareit.item.dto.CommentOutDTO;
import ru.practicum.shareit.item.dto.ItemDTO;
import ru.practicum.shareit.item.dto.ItemOutDTO;

import java.util.List;

public interface ItemService {
    ItemOutDTO addItem(Long ownerId, ItemDTO itemDto);

    ItemOutDTO updateItem(Long itemId, Long ownerId, ItemDTO itemDto);

    ItemOutDTO getItemById(Long itemId, Long userId);

    List<ItemOutDTO> getAllItemsByOwner(Long ownerId, Integer from, Integer size);

    List<ItemOutDTO> searchItems(String text, Integer from, Integer size);

    CommentOutDTO addComment(Long userId, Long itemId, CommentDTO commentDTO);
}
