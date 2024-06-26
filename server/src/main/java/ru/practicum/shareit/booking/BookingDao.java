package ru.practicum.shareit.booking;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingDao extends JpaRepository<Booking, Long> {
    Optional<Booking> findBookingByIdAndBooker_IdOrIdAndItem_Owner_Id(
            Long id, Long bookerId, Long bookingId, Long ownerId);

    Page<Booking> findAllByBooker_Id(Long bookerId, Pageable pageable);

    Page<Booking> findAllByBooker_IdAndStartIsBeforeAndEndIsAfter(
            Long bookerId, LocalDateTime now, LocalDateTime timeNow, Pageable pageable);

    Page<Booking> findAllByBooker_IdAndEndIsBefore(
            Long bookerId, LocalDateTime now, Pageable pageable);

    Page<Booking> findAllByBooker_IdAndStartIsAfter(
            Long bookerId, LocalDateTime now, Pageable pageable);

    Page<Booking> findAllByBooker_IdAndStatus(
            Long bookerId, Status status, Pageable pageable);

    Page<Booking> findAllByItem_Owner_Id(Long ownerId, Pageable pageable);

    Page<Booking> findAllByItem_Owner_IdAndStartIsBeforeAndEndIsAfter(
            Long ownerId, LocalDateTime now, LocalDateTime timeNow, Pageable pageable);

    Page<Booking> findAllByItem_Owner_IdAndEndIsBefore(
            Long ownerId, LocalDateTime now, Pageable pageable);

    Page<Booking> findAllByItem_Owner_IdAndStartIsAfter(
            Long ownerId, LocalDateTime now, Pageable pageable);

    Page<Booking> findAllByItem_Owner_IdAndStatus(
            Long ownerId, Status status, Pageable pageable);

    Page<Booking> findAllByItem_IdAndStatusIsNot(
            Long itemId, Status status, Pageable pageable);

    Page<Booking> findAllByItem_IdInAndStatusIsNot(
            List<Long> itemsIds, Status status, Pageable pageable);

    boolean existsByItem_IdAndBooker_IdAndStatusAndEndIsBefore(
            Long itemId, Long bookerId, Status status, LocalDateTime now);

    @Modifying
    @Query(nativeQuery = true,
            value = "UPDATE bookings SET status = :status WHERE id = :bookingId")
    void approvedBooking(Long bookingId, String status);
}