package ru.practicum.shareit.booking.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingStateDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingServiceImp;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

/**
 * // TODO .
 */
@Validated
@RestController
@RequestMapping(path = "/bookings")
@Slf4j
public class BookingController {
    @Autowired
    BookingServiceImp bookingService;

    @PostMapping
    public BookingDto addBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                                 @Valid @RequestBody BookingCreateDto bookingCreateDto) {
        log.info("Получен запрос POST /bookings - {} user - {}", bookingCreateDto, userId);
        return bookingService.addBooking(userId, BookingMapper.toBookingDto(bookingCreateDto));
    }

    //PATCH /bookings/{bookingId}?approved={approved}
    @PatchMapping("/{bookingId}")
    public BookingStateDto approvedBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                                   @PathVariable Long bookingId,
                                   @RequestParam(value = "approved") boolean text) {
        log.info("Получен запрос GET /{} user - {}", bookingId, userId);
        return bookingService.setApprove(userId, bookingId, text);
    }

    @GetMapping("{id}")
    public BookingStateDto getBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                              @PathVariable Long id) {
        log.info("Получен запрос GET /booking/{} user - {}", id, userId);
        return bookingService.getBooking(userId, id);
    }

    //GET /bookings?state={state}
    @GetMapping
    public List<BookingStateDto> getAllBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                                               @RequestParam( value = "from", required = false, defaultValue = "0") @PositiveOrZero Long itemRequestId,
                                               @RequestParam(value = "size", required = false, defaultValue = "10") @Positive Integer size,
                                               @RequestParam(value = "state", required = false, defaultValue = "ALL") String state) {
        log.info("Получен запрос GET /booking?state={} user - {}", state, userId);
        return bookingService.getAllBooking(userId, state, itemRequestId, size);
    }

    //GET /bookings/owner?state={state}
    @GetMapping("/owner")
    public List<Booking> getAllBookingByOwner(@RequestHeader("X-Sharer-User-Id") Long userId,
                                              @RequestParam(value = "from", required = false, defaultValue = "0") Long itemRequestId,
                                              @RequestParam(value = "size", required = false, defaultValue = "10") Integer size,
                                              @RequestParam(value = "state", required = false, defaultValue = "ALL") String state) {
        log.info("Получен запрос GET /booking/owner?state={} user - {}", state, userId);
        return bookingService.getAllBookingByOwner(userId, state, itemRequestId, size);
    }
}
