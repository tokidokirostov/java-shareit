package ru.practicum.shareit.item.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoController;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.service.ItemServiceImpl;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

/**
 * // TODO .
 */
@Slf4j
@RestController
@RequestMapping("/items")
public class ItemController {
    @Autowired
    ItemServiceImpl itemService;

    @GetMapping
    public List<ItemDto> getAllItems(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Получен запрос GET /items user - {}", userId);
        return itemService.getAllItems(userId);
    }

    @GetMapping("{id}")
    public ItemDto getItem(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable Long id) {
        log.info("Получен запрос GET /items/{} user - {}", id, userId);
        return itemService.getItem(userId, id);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItem(@RequestHeader("X-Sharer-User-Id") Long userId, @RequestParam Optional<String> text) {
        log.info("Получен запрос GET /items/search?text={} user - {}", text.get(), userId);
        //return null;//itemService.getItem(userId, id);
        return itemService.searchItems(text);
    }

    @PostMapping
    public ItemDto addUser(@RequestHeader("X-Sharer-User-Id") Long userId, @Valid @RequestBody ItemDtoController itemDtoController) {
        log.info("Получен запрос POST /item - {} user - {}", itemDtoController, userId);
        return itemService.addItem(userId, ItemMapper.toItemDto(itemDtoController));
    }

    @PatchMapping("{id}")
    public ItemDto patchItem(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable Long id, @RequestBody ItemDto itemDto) {
        log.info("Получен запрос PATCH /item/{} user - {}", id, userId);
        return itemService.patchUser(userId, id, itemDto);
    }

}
