package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemBookingDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;
import java.util.Optional;

public interface ItemService {
    //Запрос всех вещей пользователя.
    List<ItemBookingDto> getAllItems(Long userId);

    //Запрос вещи.
    ItemBookingDto getItem(Long userId, Long id);

    //Добавление вещи.
    ItemDto addItem(Long userId, ItemDto itemDto);

    //Изменение вещи.
    ItemDto patchUser(Long userId, Long id, ItemDto itemDto);

    //Поиск вещи.
    List<ItemDto> searchItems(Optional<String> text);

    //Сохранение коментария
    CommentDto saveComment(Long itemId, Long userId, CommentDto commentDto);
}
