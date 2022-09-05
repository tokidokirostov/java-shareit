package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingStateDto;
import ru.practicum.shareit.booking.model.Booking;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingService {

    BookingDto addBooking(Long userId, BookingDto bookingDto);

    BookingStateDto setApprove(Long userId, Long bookingId, boolean approved);

    BookingStateDto getBooking(Long userId, Long id);

    List<BookingStateDto> getAllBooking(Long userId, String state, Long itemRequestId, Integer size);

    List<Booking> getAllBookingByOwner(Long userId, String state, Long page1, Integer size);

    Booking getLastBooking(Long id, LocalDateTime localDateTime);

    Booking getNextBooking(Long id, LocalDateTime localDateTime);

}
