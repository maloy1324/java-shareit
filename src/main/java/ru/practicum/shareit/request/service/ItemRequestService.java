package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

public interface ItemRequestService {
    ItemRequestDto create(Long userId, ItemRequestDto itemRequestDto);

    List<ItemRequestDto> findAllByUser(Long userId);

    List<ItemRequestDto> findAllByOtherUsers(Long userId, Integer from, Integer size);

    ItemRequestDto findById(Long userId, Long id);
}
