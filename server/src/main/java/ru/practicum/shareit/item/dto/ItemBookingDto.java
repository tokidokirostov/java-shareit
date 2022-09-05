package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.booking.dto.BookingForItemDto;

import java.util.List;

@Data
@AllArgsConstructor
public class ItemBookingDto {
    Long id;
    String name;
    String description;
    Boolean available;
    BookingForItemDto lastBooking;
    BookingForItemDto nextBooking;
    List<CommentDto> comments;
    Long requestId;
}
