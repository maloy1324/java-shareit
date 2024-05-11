package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDTO;
import ru.practicum.shareit.item.dto.CommentOutDTO;
import ru.practicum.shareit.item.dto.ItemDTO;
import ru.practicum.shareit.item.dto.ItemOutDTO;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/items")
public class ItemController {

    private final ItemService itemService;

    @Autowired
    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemOutDTO addItem(@RequestHeader("X-Sharer-User-Id") Long ownerId, @RequestBody @Valid ItemDTO itemDto) {
        return itemService.addItem(ownerId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ItemOutDTO updateItem(@PathVariable Long itemId, @RequestHeader("X-Sharer-User-Id") Long ownerId, @RequestBody ItemDTO itemDto) {
        return itemService.updateItem(itemId, ownerId, itemDto);
    }

    @GetMapping("/{itemId}")
    public ItemOutDTO getItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                              @PathVariable Long itemId) {
        return itemService.getItemById(itemId, userId);
    }

    @GetMapping
    public List<ItemOutDTO> getAllItemsByOwner(@RequestHeader("X-Sharer-User-Id") Long ownerId) {
        return itemService.getAllItemsByOwner(ownerId);
    }

    @GetMapping("/search")
    public List<ItemOutDTO> searchItems(@RequestParam String text) {
        return itemService.searchItems(text);
    }

    @PostMapping("/{itemId}/comment")
    public CommentOutDTO addComment(@RequestHeader("X-Sharer-User-Id") Long userId,
                                    @PathVariable Long itemId,
                                    @RequestBody @Valid CommentDTO commentDTO) {
        return itemService.addComment(userId, itemId, commentDTO);
    }
}
