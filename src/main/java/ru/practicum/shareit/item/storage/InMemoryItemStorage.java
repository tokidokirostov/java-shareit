package ru.practicum.shareit.item.storage;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.model.Item;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class InMemoryItemStorage implements ItemStorage {
    private final Map<Long, Item> items = new HashMap<>();
    private Long idItem = 0L;

    //Добавление вещи
    public Item addItem(Item item) {
        item.setId(++idItem);
        items.put(item.getId(), item);
        return item;
    }

    //Изменение вещи
    @Override
    public Item patchUser(Item item) {
        items.remove(item.getId());
        items.put(item.getId(), item);
        return item;
    }

    //Запрос всех вещей пользователя
    @Override
    public List<Item> getAllItems(Long userId) {
        return items.values().stream().filter(item -> item.getOwner().getId().equals(userId)).collect(Collectors.toList());
    }

    //Запрос вещи
    @Override
    public Item getItem(Long id) {
        return items.get(id);
    }

    //Поиск вещи
    public List<Item> searchItems(String text) {
        Set<Item> findItems = new HashSet<>();
        if (text.isEmpty()) {
            List<Item> itemList = new ArrayList<>(findItems);
            return itemList;
        } else {
            for (Item item : items.values()) {
                if ((item.getName().toLowerCase().contains(text.toLowerCase())
                        || item.getDescription().toLowerCase().contains(text.toLowerCase()))
                        && item.getIsAvailable() == true) {
                    findItems.add(item);
                }
            }
            List<Item> itemList = new ArrayList<>(findItems);
            return itemList.stream().sorted((i1, i2) -> i1.getId().compareTo(i2.getId())).collect(Collectors.toList());
        }
    }
}
