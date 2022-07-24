package ru.practicum.shareit.item.storage;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.*;

@Component
public class InMemoryItemStorage implements ItemStorage {
    private final Map<Long, List<Item>> items = new HashMap<>();
    private Long idItem = 0L;

    //Добавление вещи
    public Item addItem(Long userId, Item item) {
        item.setId(++idItem);
        if (items.containsKey(userId)) {
            items.get(userId).add(item);
        } else {
            List<Item> itemList = new ArrayList<>();
            itemList.add(item);
            items.put(userId, itemList);
        }
        return item;
    }

    //Изменение вещи
    @Override
    public Item patchUser(Long userId, Long id, ItemDto itemDto) {
        List<Item> itemList = items.get(userId);
        Item returnItem = new Item();
        if (itemList == null) {
            throw new NotFoundException("Item не найден!");
        } else {
            for (Item item1 : itemList) {
                if (item1.getId().equals(id)) {
                    if (itemDto.getName() != null) {
                        item1.setName(itemDto.getName());
                    }
                    if (itemDto.getDescription() != null) {
                        item1.setDescription(itemDto.getDescription());
                    }
                    if (itemDto.getAvailable() != null) {
                        item1.setAvailable(itemDto.getAvailable());
                    }
                    returnItem = item1;
                }
            }
            return returnItem;
        }
    }

    //Запрос всех вещей пользователя
    @Override
    public List<Item> getAllItems(Long userId) {
        return items.get(userId);
    }

    //Запрос вещи
    @Override
    public Item getItem(Long userId, Long id) {
        Item item = new Item();
        for (List<Item> itemList1 : items.values())
            for (Item item1 : itemList1) {
                if (item1.getId().equals(id)) {
                    item = item1;
                }
            }
        return item;
    }

    //Поиск вещи
    public List<Item> searchItems(String text) {
        Set<Item> findItems = new HashSet<>();
        if (text.isEmpty()) {
            List<Item> itemList = new ArrayList<>(findItems);
            return itemList;
        } else {

            for (List<Item> listItems : items.values()) {
                for (Item item : listItems) {
                    if ((item.getName().toLowerCase().contains(text.toLowerCase())
                            || item.getDescription().toLowerCase().contains(text.toLowerCase()))
                            && item.getAvailable() == true) {
                        findItems.add(item);
                    }
                }
            }
            List<Item> itemList = new ArrayList<>(findItems);
            return itemList;
        }
    }
}
