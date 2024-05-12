package ru.practicum.shareit.item.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.item.model.Comment;

import java.util.List;

public interface CommentDao extends JpaRepository<Comment, Long> {
    List<Comment> findAllByItem_IdOrderByCreatedDesc(Long itemId);

    Page<Comment> findAllByItem_IdInOrderByCreatedDesc(List<Long> itemIds, Pageable pageable);
}
