package ru.practicum.shareit.comment;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.item.dto.CommentDTO;
import ru.practicum.shareit.item.dto.CommentOutDTO;
import ru.practicum.shareit.item.model.Comment;

import java.util.List;


@Mapper(componentModel = "spring")
public interface CommentMapper {
    @Mapping(source = "item.id", target = "itemId")
    @Mapping(source = "author.id", target = "authorId")
    CommentDTO toDTO(Comment comment);

    @Mapping(source = "item.id", target = "itemId")
    @Mapping(source = "author.name", target = "authorName")
    CommentOutDTO toOutDTO(Comment comment);

    @Mapping(source = "itemId", target = "item.id")
    @Mapping(source = "authorId", target = "author.id")
    Comment toModel(CommentDTO commentDTO);

    List<CommentDTO> toListDTO(List<Comment> modelList);

    List<CommentOutDTO> toListOutDTO(List<Comment> modelList);

    List<Comment> toModelList(List<CommentDTO> commentDTOList);
}
