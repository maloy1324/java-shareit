package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith(MockitoExtension.class)
public class ItemMapperTest {
    @InjectMocks
    private ItemMapperImpl itemMapper;

    @Test
    public void testToItemOutputDTOs_ReturnNull() {

        assertNull(itemMapper.toListOutDTO(null));
    }

    @Test
    public void testToInputDTO_ReturnNull() {

        assertNull(itemMapper.toDTO(null));
    }

    @Test
    public void testToItemOutputDTO_ReturnNull() {

        assertNull(itemMapper.toOutDTO(null));
    }

    @Test
    public void testUpdateInputDTOToEntity_ReturnNull() {

        assertNull(itemMapper.toModel(null));
    }
}
