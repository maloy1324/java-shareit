package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.item.CommentMapperImpl;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith(MockitoExtension.class)
public class CommentMapperTest {
    @InjectMocks
    private CommentMapperImpl commentMapper;

    @Test
    public void testToDTO() {
        // Arrange
        User user = User.builder()
                .id(1L)
                .email("test@test.com")
                .name("Test")
                .build();
        Comment comment = new Comment();
        comment.setItem(Item.builder()
                .id(1L)
                .name("testitem")
                .available(true)
                .description("description")
                .build());
        comment.setAuthor(user);
        comment.setText("Test comment");
        comment.setCreated(LocalDateTime.now());

        // Act
        CommentDTO result = commentMapper.toDTO(comment);

        // Assert
        assertEquals(1L, result.getItemId());
        assertEquals(1L, result.getAuthorId());
        assertEquals("Test comment", result.getText());
        // Check other properties as needed
    }

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
