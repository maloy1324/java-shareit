package ru.practicum.shareit.item.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemDao extends JpaRepository<Item, Long> {
    @Query(" select i from Item i " +
            "where upper(i.name) like upper(concat('%', ?1, '%')) " +
            " or upper(i.description) like upper(concat('%', ?1, '%'))" +
            " and i.available = true")
    List<Item> search(String text);

    List<Item> findByOwnerId(Long ownerId);

    void deleteByOwnerId(Long ownerId);

    List<Item> findAllByOwnerIdOrderById(Long ownerId);

    boolean existsItemByIdAndOwner_Id(Long itemId, Long ownerId);
}
