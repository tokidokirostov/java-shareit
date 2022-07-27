package ru.practicum.shareit.item.storage;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemStorage {
    Item addItem(Item item);

    Item patchUser(Item item);

    List<Item> getAllItems(Long userId);

    Item getItem(Long id);

    List<Item> searchItems(String text);
}
