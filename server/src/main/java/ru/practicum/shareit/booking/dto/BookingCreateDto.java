package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;

/**
 * // TODO .
 */
@Data
@AllArgsConstructor
public class BookingCreateDto {
    Long id;
    @DateTimeFormat(fallbackPatterns = "yyy-MM-ddTHH:mm:ss")
    LocalDateTime start;
    @DateTimeFormat(fallbackPatterns = "yyy-MM-ddTHH:mm:ss")
    LocalDateTime end;
    Long itemId;
    Long booker;
    BookingStatus status;
}
