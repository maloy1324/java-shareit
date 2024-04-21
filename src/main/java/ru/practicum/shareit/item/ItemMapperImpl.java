package ru.practicum.shareit.item;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.ItemDTO;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ItemMapperImpl implements ItemMapper {

    @Override
    public ItemDTO toDTO(Item item) {
        if (item == null) {
            return null;
        }
        return ItemDTO.builder()
                .id(item.getId())
                .ownerId(item.getOwnerId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .build();
    }

    @Override
    public Item toModel(ItemDTO itemDTO) {
        if (itemDTO == null) {
            return null;
        }
        return Item.builder()
                .id(itemDTO.getId())
                .ownerId(itemDTO.getOwnerId())
                .name(itemDTO.getName())
                .description(itemDTO.getDescription())
                .available(itemDTO.getAvailable())
                .build();
    }

    @Override
    public List<ItemDTO> toListDTO(List<Item> modelList) {
        if (modelList == null) {
            return null;
        }
        return modelList.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<Item> toModelList(List<ItemDTO> itemDTOList) {
        if (itemDTOList == null) {
            return null;
        }
        return itemDTOList.stream()
                .map(this::toModel)
                .collect(Collectors.toList());
    }
}
