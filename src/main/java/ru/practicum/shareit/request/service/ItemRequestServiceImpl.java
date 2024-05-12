package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dao.ItemDao;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dao.ItemRequestDao;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapping.ItemRequestMapping;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.dao.UserDao;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemRequestServiceImpl implements ItemRequestService {
    private final UserDao userDao;
    private final ItemRequestDao itemRequestDao;
    private final ItemRequestMapping itemRequestMapping;
    private final ItemDao itemDao;
    private final ItemMapper itemMapper;

    @Override
    @Transactional
    public ItemRequestDto create(Long userId, ItemRequestDto itemRequestDto) {
        ItemRequest itemRequest = itemRequestMapping.fromDto(itemRequestDto);
        User requester = userDao.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));
        itemRequest.setRequester(requester);
        itemRequest.setCreated(LocalDateTime.now());
        return itemRequestMapping.toDto(itemRequestDao.save(itemRequest));
    }

    @Override
    public List<ItemRequestDto> findAllByUser(Long userId) {
        if (!userDao.existsById(userId)) {
            throw new NotFoundException("User not found");
        }
        return getItemRequestOutDtoList(itemRequestDao.findAllByRequesterId(userId));
    }

    @Override
    public List<ItemRequestDto> findAllByOtherUsers(Long userId, Integer from, Integer size) {
        if (!userDao.existsById(userId)) {
            throw new NotFoundException("User not found");
        }
        Pageable pageable = PageRequest.of(from / size, size, Sort.by("created").descending());
        return getItemRequestOutDtoList(itemRequestDao.findAllByRequesterIdNot(userId, pageable).getContent());
    }

    @Override
    public ItemRequestDto findById(Long userId, Long id) {
        if (!userDao.existsById(userId)) {
            throw new NotFoundException("User not found");
        }
        ItemRequestDto itemRequestDto = itemRequestMapping.toDto(itemRequestDao.findById(id)
                .orElseThrow(() -> new NotFoundException("Request not found")));
        itemRequestDto.setItems(itemDao.findAllByRequestId(id).stream()
                .map(itemMapper::toOutDTO)
                .collect(Collectors.toList()));
        return itemRequestDto;
    }

    private List<ItemRequestDto> getItemRequestOutDtoList(List<ItemRequest> itemRequests) {
        List<Item> items = itemDao.findAllByRequestIdIn(itemRequests.stream()
                .map(ItemRequest::getId)
                .collect(Collectors.toList()));

        return itemRequests.stream()
                .map(itemRequestMapping::toDto)
                .peek(a -> a.setItems(items.stream()
                        .map(itemMapper::toOutDTO)
                        .filter(b -> Objects.equals(b.getRequestId(), a.getId()))
                        .collect(Collectors.toList())))
                .collect(Collectors.toList());
    }
}
