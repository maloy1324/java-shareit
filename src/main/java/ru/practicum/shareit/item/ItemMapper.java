package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDTO;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemMapper {
    ItemDTO toDTO(Item item);

    Item toModel(ItemDTO itemDTO);

    List<ItemDTO> toListDTO(List<Item> modelList);

    List<Item> toModelList(List<ItemDTO> itemDTOList);
}