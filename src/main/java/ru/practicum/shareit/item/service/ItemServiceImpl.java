package ru.practicum.shareit.item.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
    public List<ItemDto> getAllItems(Long userId) {
        return itemStorage.getAllItems(userId).stream().map(item -> ItemMapper.toItemDto(item)).collect(Collectors.toList());
    }

    //Запрос вещи
    @Override
    public ItemDto getItem(Long userId, Long id) {
        if (itemStorage.getItem(id).getId() == null) {
            throw new NotFoundException("Item not found");
        } else {
            return ItemMapper.toItemDto(itemStorage.getItem(id));
        }
    }

    //Добавление вещи
    @Override
    public ItemDto addItem(Long userId, ItemDto itemDto) {
        if (userStorage.getUserById(userId) != null) {
            log.info("Пользователь существует. Создание item.");
            return ItemMapper.toItemDto(itemStorage.addItem(ItemMapper.toItem(itemDto, userStorage.getUserById(userId))));
        } else {
            log.info("Пользователь не найден!");
            throw new NotFoundException("Пользователь не найден!");
        }
    }

    //Изменение вещи
    @Override
    public ItemDto patchUser(Long userId, Long id, ItemDto itemDto) {
        if (userStorage.getUserById(userId) == null) {
            log.info("Пользователь не найден!");
            throw new NotFoundException("Пользователь не найден!");
        } else {
            log.info("Пользователь существует. Обновление item. ID - {}", id);
            Item patchItem = itemStorage.getItem(id);
            System.out.println(patchItem.getOwner().getId());
            if (!patchItem.getOwner().getId().equals(userId) || patchItem == null) {
                throw new NotFoundException("Item not found");
            } else {
                if (patchItem.getId().equals(id)) {
                    if (itemDto.getName() != null) {
                        patchItem.setName(itemDto.getName());
                    }
                    if (itemDto.getDescription() != null) {
                        patchItem.setDescription(itemDto.getDescription());
                    }
                    if (itemDto.getAvailable() != null) {
                        patchItem.setAvailable(itemDto.getAvailable());
                    }
                }
                return ItemMapper.toItemDto(itemStorage.patchUser(patchItem));
            }
        }
    }

    //Поиск вещи
    @Override
    public List<ItemDto> searchItems(Optional<String> text) {
        return itemStorage.searchItems(text.get()).stream().map(item -> ItemMapper.toItemDto(item)).collect(Collectors.toList());
    }

}
