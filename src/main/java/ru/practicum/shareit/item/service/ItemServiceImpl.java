package ru.practicum.shareit.item.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@AllArgsConstructor
public class ItemServiceImpl implements ItemService {
    @Autowired
    private final ItemStorage itemStorage;
    @Autowired
    private final UserStorage userStorage;

    //Запрос всех вещей пользователя
    @Override
    public List<Item> getAllItems(Long userId) {
        return itemStorage.getAllItems(userId);

    }

    //Запрос вещи
    @Override
    public Item getItem(Long userId, Long id) {
        if (itemStorage.getItem(userId, id).getId() == null) {
            throw new NotFoundException("Item not found");
        } else {
            return itemStorage.getItem(userId, id);
        }
    }

    //Добавление вещи
    @Override
    public Item addItem(Long userId, Item item) {
        if (userStorage.getUserById(userId) != null) {
            log.info("Пользователь существует. Создание item.");
            return itemStorage.addItem(userId, item);
        } else {
            log.info("Пользователь не найден!");
            throw new NotFoundException("Пользователь не найден!");
        }
    }

    //Изменение вещи
    @Override
    public Item patchUser(Long userId, Long id, ItemDto itemDto) {
        if (userStorage.getUserById(userId) != null) {
            log.info("Пользователь существует. Обновление item. ID - {}", id);
            return itemStorage.patchUser(userId, id, itemDto);
        } else {
            log.info("Пользователь не найден!");
            throw new NotFoundException("Пользователь не найден!");
        }
    }

    //Поиск вещи
    @Override
    public List<Item> searchItems(Optional<String> text) {
        return itemStorage.searchItems(text.get());
    }

}
