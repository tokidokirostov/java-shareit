package ru.practicum.shareit.requests;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.requests.dto.ItemRequestDto;

import javax.validation.Valid;

@Controller
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
@Validated
public class RequestController {

    @Autowired
    RequestClient requestClient;

    @PostMapping
    public ResponseEntity<Object> addRequest(@RequestHeader("X-Sharer-User-Id") long userId,
                                             @Valid @RequestBody ItemRequestDto itemRequestDto) {
        log.info("Получен запрос POST /requests user - {}", userId);
        return requestClient.addRequest(userId, itemRequestDto);
    }

    @GetMapping
    public ResponseEntity<Object> getAllRequest(@RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Получен запрос GET /requests user - {}", userId);
        return requestClient.getAllRequest(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllRequestByPage(@RequestHeader("X-Sharer-User-Id") long userId,
                                                      @RequestParam(value = "from", required = false, defaultValue = "0") long itemRequestId,
                                                      @RequestParam(value = "size", required = false, defaultValue = "10") int size) {
        log.info("Получен запрос GET /requests/all user - {}, from - {}, size - {}", userId, itemRequestId, size);
        return requestClient.getAllRequestByPage(userId, itemRequestId, size);
    }

    @GetMapping("{requestId}")
    public ResponseEntity<Object> getRequest(@RequestHeader("X-Sharer-User-Id") long userId,
                                             @PathVariable(value = "requestId") long requestId) {
        log.info("Получен запрос GET /requests/{} user - {}", requestId, userId);
        return requestClient.getRequest(userId, requestId);
    }
}
