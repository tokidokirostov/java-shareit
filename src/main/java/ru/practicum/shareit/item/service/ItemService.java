package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;
import java.util.Optional;

public interface ItemService {
    //Запрос всех вещей пользователя.
    List<ItemDto> getAllItems(Long userId);

    //Запрос вещи.
    ItemDto getItem(Long userId, Long id);

    //Добавление вещи.
    ItemDto addItem(Long userId, ItemDto itemDto);

    //Изменение вещи.
    ItemDto patchUser(Long userId, Long id, ItemDto itemDto);

    //Поиск вещи.
    List<ItemDto> searchItems(Optional<String> text);
}
