package ru.practicum.shareit.booking;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.booking.dto.BookingDTO;
import ru.practicum.shareit.booking.dto.BookingOutDTO;
import ru.practicum.shareit.booking.dto.BookingShortDTO;

import java.util.List;

@Mapper(componentModel = "spring")
public interface BookingMapper {

    @Mapping(source = "item.id", target = "itemId")
    @Mapping(source = "booker.id", target = "bookerId")
    BookingDTO toDTO(Booking booking);

    BookingOutDTO toOutDTO(Booking booking);

    BookingShortDTO toShortDTO(BookingDTO bookingDTO);

    @Mapping(source = "itemId", target = "item.id")
    @Mapping(source = "bookerId", target = "booker.id")
    Booking toModel(BookingDTO bookingDTO);

    List<BookingDTO> toListDTO(List<Booking> modelList);

    List<BookingOutDTO> toListOutDTO(List<Booking> modelList);
}
