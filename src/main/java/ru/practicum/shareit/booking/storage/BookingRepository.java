package ru.practicum.shareit.booking.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    //ALL
    List<Booking> findAllByBookerIdOrderByStartDesc(Long userId);

    List<Booking> findAllByBookerIdAndStatusOrderByStartDesc(Long userId, BookingStatus bookingStatus);
    //FUTURE
    List<Booking> findAllByBookerIdAndStartAfterOrderByStartDesc(Long userid, LocalDateTime localDateTime);
    //CURRENT
    List<Booking> findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(Long userId, LocalDateTime localDateTimeDefore,
                                                                             LocalDateTime localDateTimeAfter);
    //PAST
    List<Booking> findAllByBookerIdAndEndBeforeOrderByStartDesc(Long userId, LocalDateTime localDateTime);
    //ALL
    List<Booking> findByItemIdInOrderByStartDesc(List<Long> itemId);

    List<Booking> findByItemIdInAndStatusOrderByStartDesc(List<Long> itemId, BookingStatus bookingStatus);
    //FUTURE
    List<Booking> findByItemIdInAndStartAfterOrderByStartDesc(List<Long> itemId, LocalDateTime localDateTime);
    //CURRENT
    List<Booking> findByItemIdInAndStartBeforeAndEndAfterOrderByStartDesc(List<Long> itemId, LocalDateTime localDateTimeDefore,
                                                                             LocalDateTime localDateTimeAfter);
    //PAST
    List<Booking> findByItemIdInAndEndBeforeOrderByStartDesc(List<Long> itemId, LocalDateTime localDateTime);

    List<Booking> findByItemIdAndEndBeforeOrderByStartDesc(Long itemId, LocalDateTime localDateTime);
    List<Booking> findByItemIdAndStartAfterOrderByStartDesc(Long itemId, LocalDateTime localDateTime);

    Booking findByBookerIdAndItemIdAndStatusAndEndBefore(Long userId, Long itemId, BookingStatus status, LocalDateTime localDateTime);
}
