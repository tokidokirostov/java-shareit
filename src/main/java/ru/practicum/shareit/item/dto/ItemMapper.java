package ru.practicum.shareit.item.dto;

import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.requests.ItemRequest;
import ru.practicum.shareit.user.model.User;

public class ItemMapper {
    public static ItemDto toItemDto(Item item) {
        return new ItemDto(item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getRequest() != null ? item.getRequest().getId() : null);
    }

    public static Item toItem(ItemDto itemDto, User user) {
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(itemDto.getRequest());
        return new Item(itemDto.getId(),
                itemDto.getName(),
                itemDto.getDescription(),
                itemDto.getAvailable(),
                user,
                itemRequest);
    }

    public static ItemDto toItemDto(ItemDtoController itemDtoController) {
        return new ItemDto(itemDtoController.getId(),
                itemDtoController.getName(),
                itemDtoController.getDescription(),
                itemDtoController.getAvailable(),
                itemDtoController.getRequest());
    }
}
