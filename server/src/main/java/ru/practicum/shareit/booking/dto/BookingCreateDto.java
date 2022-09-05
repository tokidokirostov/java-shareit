package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;
import ru.practicum.shareit.booking.model.BookingStatus;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * // TODO .
 */
@Data
@AllArgsConstructor
public class BookingCreateDto {
    Long id;
    @NotNull
    @DateTimeFormat(fallbackPatterns = "yyy-MM-ddTHH:mm:ss")
    LocalDateTime start;
    @NotNull
    @DateTimeFormat(fallbackPatterns = "yyy-MM-ddTHH:mm:ss")
    LocalDateTime end;
    Long itemId;
    Long booker;
    BookingStatus status;
}
