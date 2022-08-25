package ru.practicum.shareit.requests.service;

import ru.practicum.shareit.requests.dto.ItemRequestDto;

import java.util.List;

public interface ItemRequestService {
    ItemRequestDto addItemRequest(Long userId, ItemRequestDto itemRequestDto);

    List<ItemRequestDto> getAllItemsRequest(Long userId);

    List<ItemRequestDto> getAllItemsByPage(Long userId, Long itemRequestId, Integer size);

    ItemRequestDto getItemRequest(Long requestId, Long userId);

}