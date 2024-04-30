package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDTO;
import ru.practicum.shareit.booking.dto.BookingOutDTO;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dao.ItemDao;
import ru.practicum.shareit.item.dto.ItemOutDTO;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.dao.UserDao;
import ru.practicum.shareit.user.dto.UserOutDTO;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;

import static ru.practicum.shareit.booking.Status.*;


@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingDao bookingRepository;
    private final BookingMapper bookingMapper;
    private final UserDao userRepository;
    private final ItemDao itemRepository;
    private final ItemMapper itemMapper;
    private final UserMapper userMapper;


    @Override
    public BookingOutDTO createBooking(BookingDTO bookingDTO) {
        validDate(bookingDTO);
        Long itemId = bookingDTO.getItemId();
        Long bookerId = bookingDTO.getBookerId();

        ItemOutDTO itemOutDTO = itemMapper.toOutDTO(itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item not found")));

        UserOutDTO bookerOutDTO = userMapper.toOutDTO(userRepository.findById(bookerId)
                .orElseThrow(() -> new NotFoundException("User not found")));

        if (!itemOutDTO.getAvailable()) {
            throw new BadRequestException("Booking is not available");
        }

        if (itemRepository.existsItemByIdAndOwner_Id(itemId, bookerId)) {
            throw new NotFoundException("Booking already exists");
        }

        bookingDTO.setStatus(WAITING);

        BookingOutDTO bookingOutDTO = bookingMapper.toOutDTO(bookingRepository.save(bookingMapper.toModel(bookingDTO)));
        bookingOutDTO.setBooker(bookerOutDTO);
        bookingOutDTO.setItem(itemOutDTO);

        return bookingOutDTO;
    }

    @Override
    public BookingOutDTO getBooking(Long bookerId, Long bookingId) {
        return bookingMapper.toOutDTO(
                bookingRepository.findBookingByIdAndBooker_IdOrIdAndItem_Owner_Id(bookingId, bookerId, bookingId, bookerId)
                        .orElseThrow(() -> new NotFoundException("Booking not found")));
    }

    @Override
    public List<BookingOutDTO> getUserBookings(Long userId, String state) {
        State bookingState;
        try {
            bookingState = State.valueOf(state);
        } catch (IllegalArgumentException e) {
            throw new BadRequestException(String.format("Unknown state: %s", state));
        }
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("User not found");
        }
        LocalDateTime now = LocalDateTime.now();

        switch (bookingState) {
            case ALL:
                return bookingMapper.toListOutDTO(
                        bookingRepository.findAllByBooker_IdOrderByStartDesc(userId));
            case CURRENT:
                return bookingMapper.toListOutDTO(
                        bookingRepository.findAllByBooker_IdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(userId, now, now));
            case PAST:
                return bookingMapper.toListOutDTO(
                        bookingRepository.findAllByBooker_IdAndEndIsBeforeOrderByStartDesc(userId, now));
            case FUTURE:
                return bookingMapper.toListOutDTO(
                        bookingRepository.findAllByBooker_IdAndStartIsAfterOrderByStartDesc(userId, now));
            case WAITING:
                return bookingMapper.toListOutDTO(
                        bookingRepository.findAllByBooker_IdAndStatusOrderByStartDesc(userId, WAITING));
            case REJECTED:
                return bookingMapper.toListOutDTO(
                        bookingRepository.findAllByBooker_IdAndStatusOrderByStartDesc(userId, REJECTED));
            default:
                throw new BadRequestException(String.format("Unknown state: %s", state));
        }
    }

    @Override
    public List<BookingOutDTO> getOwnerBookings(Long ownerId, String state) {
        State bookingState;
        try {
            bookingState = State.valueOf(state);
        } catch (IllegalArgumentException e) {
            throw new BadRequestException(String.format("Unknown state: %s", state));
        }
        if (!userRepository.existsById(ownerId)) {
            throw new NotFoundException("User not found");
        }
        LocalDateTime now = LocalDateTime.now();

        switch (bookingState) {
            case ALL:
                return bookingMapper.toListOutDTO(
                        bookingRepository.findAllByItem_Owner_IdOrderByStartDesc(ownerId));
            case CURRENT:
                return bookingMapper.toListOutDTO(
                        bookingRepository.findAllByItem_Owner_IdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(ownerId, now, now));
            case PAST:
                return bookingMapper.toListOutDTO(
                        bookingRepository.findAllByItem_Owner_IdAndEndIsBeforeOrderByStartDesc(ownerId, now));
            case FUTURE:
                return bookingMapper.toListOutDTO(
                        bookingRepository.findAllByItem_Owner_IdAndStartIsAfterOrderByStartDesc(ownerId, now));
            case WAITING:
                return bookingMapper.toListOutDTO(
                        bookingRepository.findAllByItem_Owner_IdAndStatusOrderByStartDesc(ownerId, WAITING));
            case REJECTED:
                return bookingMapper.toListOutDTO(
                        bookingRepository.findAllByItem_Owner_IdAndStatusOrderByStartDesc(ownerId, REJECTED));
            default:
                throw new BadRequestException(String.format("Unknown state: %s", state));
        }
    }

    @Override
    @Transactional
    public BookingOutDTO updateBookingStatus(Long bookerId, Long bookingId, boolean approved) {
        BookingOutDTO bookingOutDTO = bookingMapper.toOutDTO(
                bookingRepository.findById(bookingId).orElseThrow(() -> new NotFoundException("Booking not found")));
        Long itemId = bookingOutDTO.getItem().getId();

        if (bookingOutDTO.getStatus() == APPROVED) {
            throw new BadRequestException("Item already booked.");
        }

        if (!itemRepository.existsItemByIdAndOwner_Id(itemId, bookerId)) {
            throw new NotFoundException("Item not found");
        }

        if (approved) {
            bookingRepository.approvedBooking(bookingId, APPROVED.toString());
            bookingOutDTO.setStatus(APPROVED);
        } else {
            bookingRepository.approvedBooking(bookingId, REJECTED.toString());
            bookingOutDTO.setStatus(REJECTED);
        }

        return bookingOutDTO;
    }

    private void validDate(BookingDTO bookingDTO) {
        if (bookingDTO.getEnd().isBefore(bookingDTO.getStart())) {
            throw new BadRequestException("The booking end date cannot be earlier than the start date");
        } else if (bookingDTO.getEnd().equals(bookingDTO.getStart())) {
            throw new BadRequestException("Same dates");
        }
    }
}