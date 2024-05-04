package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingDao extends JpaRepository<Booking, Long> {
    Optional<Booking> findBookingByIdAndBooker_IdOrIdAndItem_Owner_Id(
            Long id, Long bookerId, Long bookingId, Long ownerId);

    List<Booking> findAllByBooker_IdOrderByStartDesc(Long bookerId);

    List<Booking> findAllByItem_IdAndStatusIsNot(Long itemId, Status status);

    List<Booking> findAllByItem_IdInAndStatusIsNot(List<Long> itemsIds, Status status);

    List<Booking> findAllByBooker_IdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(
            Long bookerId, LocalDateTime now, LocalDateTime timeNow);

    List<Booking> findAllByBooker_IdAndEndIsBeforeOrderByStartDesc(Long bookerId, LocalDateTime now);

    List<Booking> findAllByBooker_IdAndStartIsAfterOrderByStartDesc(Long bookerId, LocalDateTime now);

    List<Booking> findAllByBooker_IdAndStatusOrderByStartDesc(Long bookerId, Status status);

    List<Booking> findAllByItem_Owner_IdOrderByStartDesc(Long ownerId);

    List<Booking> findAllByItem_Owner_IdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(
            Long ownerId, LocalDateTime now, LocalDateTime timeNow);

    List<Booking> findAllByItem_Owner_IdAndEndIsBeforeOrderByStartDesc(Long ownerId, LocalDateTime now);

    List<Booking> findAllByItem_Owner_IdAndStartIsAfterOrderByStartDesc(Long ownerId, LocalDateTime now);

    List<Booking> findAllByItem_Owner_IdAndStatusOrderByStartDesc(Long ownerId, Status status);

    boolean existsByItem_IdAndBooker_IdAndStatusAndEndIsBefore(
            Long itemId, Long bookerId, Status status, LocalDateTime now);

    @Modifying
    @Query(nativeQuery = true,
            value = "UPDATE bookings SET status = :status WHERE id = :bookingId")
    void approvedBooking(Long bookingId, String status);
}
