package ru.practicum.shareit.item.dto;

import ru.practicum.shareit.booking.dto.BookingForItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.requests.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public class ItemMapper {
    public static ItemDto toItemDto(Item item) {
        return new ItemDto(item.getId(),
                item.getName(),
                item.getDescription(),
                item.getIsAvailable(),
                item.getRequestId() != null ? item.getRequestId().getId() : null
        );
    }

    public static Item toItem(ItemDto itemDto, User user, ItemRequest itemRequest) {
        return new Item(itemDto.getId(),
                itemDto.getName(),
                itemDto.getDescription(),
                itemDto.getAvailable(),
                user,
                itemRequest
        );
    }

    public static ItemDto toItemDto(ItemCreateDto itemDtoController) {
        return new ItemDto(itemDtoController.getId(),
                itemDtoController.getName(),
                itemDtoController.getDescription(),
                itemDtoController.getAvailable(),
                itemDtoController.getRequestId());
    }

    public static ItemBookingDto toItemTimesDto(Item itemDto, BookingForItemDto lastBooking,
                                                BookingForItemDto nextBooking, List<CommentDto> comments) {
        return new ItemBookingDto(itemDto.getId(),
                itemDto.getName(),
                itemDto.getDescription(),
                itemDto.getIsAvailable(),
                lastBooking,
                nextBooking,
                comments,
                itemDto.getRequestId() != null ? itemDto.getRequestId().getId() : null
        );
    }
}
