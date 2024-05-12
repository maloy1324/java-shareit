package ru.practicum.shareit.request.dto;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.request.mapping.ItemRequestMappingImpl;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith(MockitoExtension.class)
public class ItemRequestMapperTest {

    @InjectMocks
    private ItemRequestMappingImpl itemRequestMapping;

    @Test
    public void testInputDTOToEntity_Mapping() {

        ItemRequestDto inputDTO = ItemRequestDto.builder()
                .id(1L)
                .description("Нужен диван.")
                .build();

        ItemRequest itemRequest = itemRequestMapping.fromDto(inputDTO);

        assertThat(itemRequest).isNotNull();
        assertThat(itemRequest.getId()).isEqualTo(inputDTO.getId());
        assertThat(itemRequest.getDescription()).isEqualTo(inputDTO.getDescription());
    }

    @Test
    public void testInputDTOToEntity_ReturnNull() {

        assertNull(itemRequestMapping.fromDto(null));
    }

    @Test
    public void testToOutputDTO_Mapping() {
        User requester = User.builder()
                .id(2L)
                .build();
        ItemRequest itemRequest = ItemRequest.builder()
                .id(1L)
                .description("Нужен диван.")
                .requester(requester)
                .build();

        ItemRequestDto outputDTO = itemRequestMapping.toDto(itemRequest);

        assertThat(outputDTO).isNotNull();
        assertThat(outputDTO.getId()).isEqualTo(itemRequest.getId());
        assertThat(outputDTO.getDescription()).isEqualTo(itemRequest.getDescription());
    }
}
