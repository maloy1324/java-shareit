package ru.practicum.shareit.request.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

public interface ItemRequestDao extends JpaRepository<ItemRequest, Long> {
    List<ItemRequest> findAllByRequesterId(Long userId);

    Page<ItemRequest> findAllByRequesterIdNot(Long userId, Pageable pageable);
}
