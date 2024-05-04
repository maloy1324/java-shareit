package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookingDao;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingDTO;
import ru.practicum.shareit.booking.dto.BookingShortDTO;
import ru.practicum.shareit.comment.CommentMapper;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dao.CommentDao;
import ru.practicum.shareit.item.dao.ItemDao;
import ru.practicum.shareit.item.dto.CommentDTO;
import ru.practicum.shareit.item.dto.CommentOutDTO;
import ru.practicum.shareit.item.dto.ItemDTO;
import ru.practicum.shareit.item.dto.ItemOutDTO;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.dao.UserDao;
import ru.practicum.shareit.user.dto.UserDTO;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static ru.practicum.shareit.booking.Status.APPROVED;
import static ru.practicum.shareit.booking.Status.REJECTED;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemDao itemDao;
    private final ItemMapper itemMapper;
    private final UserDao userDao;
    private final BookingMapper bookingMapper;
    private final BookingDao bookingDao;
    private final UserMapper userMapper;
    private final CommentMapper commentMapper;
    private final CommentDao commentDao;

    @Override
    public ItemOutDTO addItem(Long ownerId, ItemDTO itemDto) {
        checkUserExists(ownerId);
        itemDto.setOwnerId(ownerId);
        return itemMapper.toOutDTO(itemDao.save(itemMapper.toModel(itemDto)));
    }

    @Override
    public ItemOutDTO getItemById(Long itemId, Long userId) {
        ItemOutDTO itemOutDTO = itemMapper.toOutDTO(itemDao.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item not found")));
        List<BookingDTO> bookings = bookingMapper.toListDTO(bookingDao.findAllByItem_IdAndStatusIsNot(itemId, REJECTED));
        List<CommentOutDTO> comments = commentMapper.toListOutDTO(commentDao.findAllByItem_IdOrderByCreatedDesc(itemId));
        if (!itemDao.existsItemByIdAndOwner_Id(itemId, userId)) {
            itemOutDTO.setComments(comments);
            return itemOutDTO;
        }
        LocalDateTime now = LocalDateTime.now();

        return collectItem(itemOutDTO, bookings, comments, now);
    }

    @Override
    public ItemOutDTO updateItem(Long itemId, Long ownerId, ItemDTO itemDto) {
        ItemDTO itemFromDB = itemMapper.toDTO(itemDao.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item not found")));

        if (!itemFromDB.getOwnerId().equals(ownerId)) {
            throw new ForbiddenException("Вы не имеете права редактировать данный предмет");
        }

        itemDto.setId(itemId);
        itemDto.setOwnerId(ownerId);
        if (Objects.isNull(itemDto.getName())) {
            itemDto.setName(itemFromDB.getName());
        }
        if (Objects.isNull(itemDto.getDescription())) {
            itemDto.setDescription(itemFromDB.getDescription());
        }
        if (Objects.isNull(itemDto.getAvailable())) {
            itemDto.setAvailable(itemFromDB.getAvailable());
        }
        return itemMapper.toOutDTO(itemDao.save(itemMapper.toModel(itemDto)));
    }

    @Override
    public List<ItemOutDTO> getAllItemsByOwner(Long ownerId) {
        List<ItemOutDTO> responseItems = itemMapper.toListOutDTO(itemDao.findAllByOwnerIdOrderById(ownerId));
        List<Long> itemsIds = responseItems.stream()
                .map(ItemOutDTO::getId)
                .collect(Collectors.toList());
        Map<Long, List<BookingDTO>> bookings = bookingDao.findAllByItem_IdInAndStatusIsNot(itemsIds, REJECTED).stream()
                .map(bookingMapper::toDTO)
                .collect(Collectors.groupingBy(BookingDTO::getItemId));
        Map<Long, List<CommentOutDTO>> comments = commentDao.findAllByItem_IdInOrderByCreatedDesc(itemsIds).stream()
                .map(commentMapper::toOutDTO)
                .collect(Collectors.groupingBy(CommentOutDTO::getItemId));
        LocalDateTime now = LocalDateTime.now();

        return responseItems.stream().map(itemDto -> {
            Long itemId = itemDto.getId();
            return collectItem(itemDto, bookings.get(itemId), comments.get(itemId), now);
        }).collect(Collectors.toList());
    }

    @Override
    public List<ItemOutDTO> searchItems(String text) {
        if (text == null || text.isEmpty()) {
            return new ArrayList<>();
        }
        return itemMapper.toListOutDTO(itemDao.search(text));
    }

    @Override
    public CommentOutDTO addComment(Long userId, Long itemId, CommentDTO commentDTO) {
        UserDTO userFromDB = userMapper.toDTO(userDao.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found")));
        if (!itemDao.existsById(itemId)) {
            throw new NotFoundException("Item not found");
        }

        LocalDateTime now = LocalDateTime.now();

        boolean isBookingConfirmed = bookingDao.existsByItem_IdAndBooker_IdAndStatusAndEndIsBefore(itemId, userId, APPROVED, now);
        if (!isBookingConfirmed) {
            throw new BadRequestException("User did not rent item");
        }

        commentDTO.setCreated(now);
        commentDTO.setAuthorId(userId);
        commentDTO.setItemId(itemId);

        CommentOutDTO commentOutDTO = commentMapper.toOutDTO(commentDao.save(commentMapper.toModel(commentDTO)));
        commentOutDTO.setAuthorName(userFromDB.getName());

        return commentOutDTO;
    }

    @Override
    public void deleteItem(Long itemId) {
        if (!itemDao.existsById(itemId)) {
            throw new NotFoundException("Вещь с таким ID не найдена.");
        }
        itemDao.deleteById(itemId);
    }

    private void checkUserExists(Long id) {
        if (!userDao.existsById(id)) {
            throw new NotFoundException("User not found");
        }
    }

    private ItemOutDTO collectItem(ItemOutDTO item,
                                   List<BookingDTO> bookings,
                                   List<CommentOutDTO> comments,
                                   LocalDateTime now) {

        if (Objects.isNull(bookings)) {
            return item.toBuilder()
                    .lastBooking(null)
                    .nextBooking(null)
                    .comments(comments)
                    .build();
        }

        BookingShortDTO lastBooking = bookingMapper.toShortDTO(bookings.stream()
                .filter(booking -> booking.getStart().isBefore(now))
                .max(Comparator.comparing(BookingDTO::getStart))
                .orElse(null));

        BookingShortDTO nextBooking = bookingMapper.toShortDTO(bookings.stream()
                .filter(booking -> booking.getStart().isAfter(now))
                .min(Comparator.comparing(BookingDTO::getStart))
                .orElse(null));

        return item.toBuilder()
                .lastBooking(lastBooking)
                .nextBooking(nextBooking)
                .comments(comments)
                .build();
    }
}

