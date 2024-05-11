package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.comment.CommentMapperImpl;
import ru.practicum.shareit.item.model.Comment;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith(MockitoExtension.class)
public class CommentMapperTest {
    @InjectMocks
    private CommentMapperImpl commentMapper;

    @Test
    public void testInputDTOToEntity_ReturnNull() {

        assertNull(commentMapper.toModel(null));
    }

    @Test
    public void testToOutputDTO_ReturnNull() {

        assertNull(commentMapper.toOutDTO(null));
    }

    @Test
    public void testToOutputDTOs_ReturnNull() {

        assertNull(commentMapper.toListOutDTO(null));
    }

    @Test
    public void testToOutputDTOs_whenEntitiesIsEmpty() {

        assertEquals(0, commentMapper.toListOutDTO(new ArrayList<>()).size());
    }

    @Test
    public void testToOutputDTOs_whenEntitiesAreNotEmpty() {
        Comment comment1 = Comment.builder().id(1L).text("Норм").build();
        Comment comment2 = Comment.builder().id(2L).text("Отличная отвертка").build();
        List<Comment> entities = List.of(comment1, comment2);

        assertEquals(2, commentMapper.toListOutDTO(entities).size());

    }

}
