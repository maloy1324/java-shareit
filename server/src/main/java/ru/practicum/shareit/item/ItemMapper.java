package ru.practicum.shareit.item;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.item.dto.ItemDTO;
import ru.practicum.shareit.item.dto.ItemOutDTO;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ItemMapper {
    @Mapping(source = "owner.id", target = "ownerId")
    ItemDTO toDTO(Item item);

    ItemOutDTO toOutDTO(Item item);

    @Mapping(source = "ownerId", target = "owner.id")
    Item toModel(ItemDTO itemDTO);

    List<ItemOutDTO> toListOutDTO(List<Item> modelList);
}