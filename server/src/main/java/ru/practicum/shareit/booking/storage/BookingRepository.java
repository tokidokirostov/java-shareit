package ru.practicum.shareit.booking.storage;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    //ALL
    Page<Booking> findAllByBookerIdOrderByStartDesc(Long userId, Pageable pageable);

    Page<Booking> findAllByBookerIdAndStatusOrderByStartDesc(Long userId, BookingStatus bookingStatus, Pageable pageable);

    //FUTURE
    Page<Booking> findAllByBookerIdAndStartAfterOrderByStartDesc(Long userid, LocalDateTime localDateTime, Pageable pageable);

    //CURRENT
    Page<Booking> findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(Long userId, LocalDateTime localDateTimeDefore,
                                                                             LocalDateTime localDateTimeAfter, Pageable pageable);

    //PAST
    Page<Booking> findAllByBookerIdAndEndBeforeOrderByStartDesc(Long userId, LocalDateTime localDateTime, Pageable pageable);

    //ALL
    Page<Booking> findByItemIdInOrderByStartDesc(List<Long> itemId, Pageable pageable);

    Page<Booking> findByItemIdInAndStatusOrderByStartDesc(List<Long> itemId, BookingStatus bookingStatus, Pageable pageable);

    //FUTURE
    Page<Booking> findByItemIdInAndStartAfterOrderByStartDesc(List<Long> itemId, LocalDateTime localDateTime, Pageable pageable);

    //CURRENT
    Page<Booking> findByItemIdInAndStartBeforeAndEndAfterOrderByStartDesc(List<Long> itemId, LocalDateTime localDateTimeDefore,
                                                                          LocalDateTime localDateTimeAfter, Pageable pageable);

    //PAST
    Page<Booking> findByItemIdInAndEndBeforeOrderByStartDesc(List<Long> itemId, LocalDateTime localDateTime, Pageable pageable);

    //lastbooking
    Page<Booking> findByItemIdAndEndBeforeOrderByStartDesc(Long itemId, LocalDateTime localDateTime, Pageable pageable);

    Page<Booking> findByItemIdAndStartAfterOrderByStartDesc(Long itemId, LocalDateTime localDateTime, Pageable pageable);

    Booking findByBookerIdAndItemIdAndStatusAndEndBefore(Long userId, Long itemId, BookingStatus status, LocalDateTime localDateTime);

}
