package ru.practicum.shareit.item.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.service.ItemServiceImpl;

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
    public List<ItemBookingDto> getAllItems(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Получен запрос GET /items user - {}", userId);
        return itemService.getAllItems(userId);
    }

    @GetMapping("{id}")
    public ItemBookingDto getItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                                  @PathVariable Long id) {
        log.info("Получен запрос GET /items/{} user - {}", id, userId);
        return itemService.getItem(userId, id);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                                    @RequestParam Optional<String> text) {
        log.info("Получен запрос GET /items/search?text={} user - {}", text.get(), userId);
        return itemService.searchItems(text);
    }

    @PostMapping
    public ItemDto addUser(@RequestHeader("X-Sharer-User-Id") Long userId,
                           @RequestBody ItemCreateDto itemCreateDto) {
        log.info("Получен запрос POST /item - {} user - {}", itemCreateDto, userId);
        return itemService.addItem(userId, ItemMapper.toItemDto(itemCreateDto));
    }

    @PatchMapping("{id}")
    public ItemDto patchItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                             @PathVariable Long id,
                             @RequestBody ItemDto itemDto) {
        log.info("Получен запрос PATCH /item/{} user - {}", id, userId);
        return itemService.patchUser(userId, id, itemDto);
    }

    // POST /items/{itemId}/comment
    @PostMapping("{itemId}/comment")
    public CommentDto saveComment(@RequestHeader("X-Sharer-User-Id") Long userId,
                                  @PathVariable Long itemId,
                                  @RequestBody CommentDto commentDto) {
        log.info("Получен запрос POST /items/{}/comment - {}", itemId, userId);
        return itemService.saveComment(itemId, userId, commentDto);
    }

}
