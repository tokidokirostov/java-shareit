package ru.practicum.shareit.item.storage;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;


public interface ItemStorage {
    Item addItem(Long userId, Item item);

    Item patchUser(Long userId, Long id, ItemDto itemDto);

    List<Item> getAllItems(Long userId);

    Item getItem(Long userId, Long id);

    List<Item> searchItems(String text);
}
