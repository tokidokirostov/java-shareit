package ru.practicum.shareit.requests.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.requests.dto.ItemRequestDto;
import ru.practicum.shareit.requests.service.ItemRequestServiceImpl;

import javax.validation.Valid;
import java.util.List;

/**
 * // TODO .
 */
@Slf4j
@RestController
@RequestMapping(path = "/requests")
public class ItemRequestController {
    @Autowired
    ItemRequestServiceImpl itemRequestService;

    @PostMapping
    public ItemRequestDto addRequest(@RequestHeader("X-Sharer-User-Id") Long userId,
                                  @Valid @RequestBody ItemRequestDto itemRequestDto) {
        log.info("Получен запрос POST /requests user - {}", userId);
        return itemRequestService.addItemRequest(userId, itemRequestDto);
    }

    @GetMapping
    public List<ItemRequestDto> getAllItems(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Получен запрос GET /requests user - {}", userId);
        return itemRequestService.getAllItemsRequest(userId);
    }

    //GET /requests/all?from={from}&size={size}
    @GetMapping("/all")
    public List<ItemRequestDto> getAllItemsByPage(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                  @RequestParam(value = "from", required = false, defaultValue = "0") Long itemRequestId,
                                                  @RequestParam(value = "size", required = false, defaultValue = "10") Integer size) {
        log.info("Получен запрос GET /requests/all user - {}, from - {}, size - {}", userId, itemRequestId, size);
        return itemRequestService.getAllItemsByPage(userId, itemRequestId, size);
    }

    @GetMapping("{requestId}")
    public ItemRequestDto getItemRequest(@RequestHeader("X-Sharer-User-Id") Long userId,
            @PathVariable(value = "requestId") Long requestId) {
        log.info("Получен запрос GET /requests/{} user - {}",requestId, userId);
        return itemRequestService.getItemRequest(requestId, userId);
    }
}
