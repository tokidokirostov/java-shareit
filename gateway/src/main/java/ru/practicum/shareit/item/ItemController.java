package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.Valid;
import java.util.Optional;

@Controller
@RequestMapping(path = "/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemController {
    @Autowired
    ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> createUser(@RequestHeader("X-Sharer-User-Id") Long userId,
                                             @Valid @RequestBody ItemCreateDto itemCreateDto) {
        log.info("Получен запрос POST /item - {} user - {}", itemCreateDto, userId);
        return itemClient.createItem(userId, itemCreateDto);
    }

    @GetMapping("{id}")
    public ResponseEntity<Object> getItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                                          @PathVariable Long id) {
        log.info("Получен запрос GET /items/{} user - {}", id, userId);
        return itemClient.getItem(userId, id);
    }

    @GetMapping
    public ResponseEntity<Object> getAllItems(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Получен запрос GET /items user - {}", userId);
        return itemClient.getAllItems(userId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                                             @RequestParam Optional<String> text) {
        log.info("Получен запрос GET /items/search?text={} user - {}", text.get(), userId);
        return itemClient.searchItem(userId, text.get());
    }

    @PatchMapping("{id}")
    public ResponseEntity<Object> patchItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                                            @PathVariable Long id,
                                            @RequestBody ItemDto itemDto) {
        log.info("Получен запрос PATCH /item/{} user - {}", id, userId);
        return itemClient.patchItem(userId, id, itemDto);
    }

    @PostMapping("{itemId}/comment")
    public ResponseEntity<Object> addComment(@RequestHeader("X-Sharer-User-Id") Long userId,
                                             @PathVariable Long itemId,
                                             @Valid @RequestBody CommentDto commentDto) {
        log.info("Получен запрос POST /items/{}/comment - {}", itemId, userId);
        return itemClient.addComment(userId, itemId, commentDto);
    }
}
