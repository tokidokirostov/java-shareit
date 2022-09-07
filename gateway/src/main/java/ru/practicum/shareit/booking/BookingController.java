package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.exception.ValidationException;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;

@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {
    private final BookingClient bookingClient;

    @GetMapping
    public ResponseEntity<Object> getBookings(@RequestHeader("X-Sharer-User-Id") long userId,
                                              @RequestParam(name = "state", required = false, defaultValue = "ALL") String stateParam,
                                              @RequestParam(name = "from", required = false, defaultValue = "0") @PositiveOrZero Integer from,
                                              @RequestParam(name = "size", required = false, defaultValue = "10") @Positive Integer size) {
        log.info("Получен запрос GET /booking?state={} user - {}", stateParam, userId);
        return bookingClient.getBookings(userId, stateParam, from, size);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getBookingsByOwner(@RequestHeader("X-Sharer-User-Id") long userId,
                                                     @RequestParam(name = "state", defaultValue = "ALL") String stateParam,
                                                     @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                     @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        log.info("Получен запрос GET /booking/owner?state={} user - {}", stateParam, userId);
        return bookingClient.getBookingsByOwner(userId, stateParam, from, size);
    }

    @PostMapping
    public ResponseEntity<Object> bookItem(@RequestHeader("X-Sharer-User-Id") long userId,
                                           @RequestBody @Valid BookingCreateDto bookingCreateDto) {
        log.info("Creating booking {}, userId={}", bookingCreateDto, userId);
        if (bookingCreateDto.getStart().isBefore(LocalDateTime.now())) {
            log.info("Не верная дата начала бронирования");
            throw new ValidationException("Не верная дата начала бронирования");
        }
        if (bookingCreateDto.getEnd().isBefore(LocalDateTime.now())) {
            log.info("Не верная дата конца бронирования");
            throw new ValidationException("Не верная дата конца бронирования");
        }
        if (bookingCreateDto.getStart().isAfter(bookingCreateDto.getEnd())) {
            log.info("дата конца раньше даты начала");
            throw new ValidationException("дата конца раньше даты начала");
        }
        return bookingClient.bookItem(userId, bookingCreateDto);
    }

    @PatchMapping("{bookingId}")
    public ResponseEntity<Object> approvedBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                  @PathVariable Long bookingId,
                                                  @RequestParam(value = "approved") String text) {
        log.info("Получен запрос PATCH /{} user - {}", bookingId, userId);
        return bookingClient.approvedBooking(userId, bookingId, text);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getBooking(@RequestHeader("X-Sharer-User-Id") long userId,
                                             @PathVariable Long id) {
        log.info("Получен запрос GET /booking/{} user - {}", id, userId);
        return bookingClient.getBooking(userId, id);
    }
}
