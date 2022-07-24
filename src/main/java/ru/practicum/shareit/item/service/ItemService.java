package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

public interface ItemService {
    //Запрос всех вещей пользователя.
    List<Item> getAllItems(Long userId);

    //Запрос вещи.
    Item getItem(Long userId, Long id);

    //Добавление вещи.
    Item addItem(Long userId, Item item);

    //Изменение вещи.
    Item patchUser(Long userId, Long id, ItemDto itemDto);

    //Поиск вещи.
    List<Item> searchItems(Optional<String> text);
}
